package github.formlessdragon.appcompat.mixins.mmce;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.security.IActionHost;
import ae2.api.networking.security.IActionSource;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.api.stacks.AEItemKey;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.me.helpers.ActionHostEnergySource;
import ae2.me.helpers.IGridConnectedTile;
import appeng.api.networking.ticking.ITickManager;
import github.formlessdragon.appcompat.bridge.ae.ManagedNodeHelper;
import github.kasuminova.mmce.common.tile.MEItemOutputBus;
import github.kasuminova.mmce.common.tile.base.MEItemBus;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.locks.ReadWriteLock;

@Mixin(value = MEItemOutputBus.class, remap = false)
public abstract class MixinMEItemOutputBus extends MEItemBus implements IGridTickable {

    @Override
    @Unique
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(5, 60, !hasItem(), 5);
    }

    @Override
    @Unique
    public TickRateModulation tickingRequest(final IGridNode gridNode, final int ticksSinceLastCall) {
        final IGridConnectedTile host = (IGridConnectedTile) this;
        final IManagedGridNode managedNode = host.getMainNode();
        if (managedNode == null || !managedNode.isActive()) {
            return TickRateModulation.IDLE;
        }
        final IGrid grid = managedNode.getGrid();
        if (grid == null) {
            return TickRateModulation.IDLE;
        }

        final int[] needUpdateSlots = getNeedUpdateSlots();
        if (needUpdateSlots.length == 0) {
            return TickRateModulation.SLOWER;
        }

        inTick = true;
        boolean successAtLeastOnce = false;

        final MEStorage net = grid.getStorageService().getInventory();
        final ActionHostEnergySource energy = new ActionHostEnergySource(host);
        final IActionSource source = IActionSource.ofMachine((IActionHost) this);

        final ReadWriteLock rwLock = inventory.getRWLock();
        try {
            rwLock.writeLock().lock();

            for (final int slot : needUpdateSlots) {
                changedSlots[slot] = false;
                final ItemStack stack = inventory.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                final ItemStack extracted = inventory.extractItem(slot, stack.getCount(), false);
                final AEItemKey key = AEItemKey.of(extracted);
                if (key == null) {

                    inventory.setStackInSlot(slot, extracted);
                    continue;
                }

                final int toInsert = extracted.getCount();
                final long inserted = StorageHelper.poweredInsert(energy, net, key, toInsert, source);
                final int remaining = toInsert - (int) inserted;

                if (remaining > 0) {
                    inventory.setStackInSlot(slot, key.toStack(remaining));
                    if (inserted > 0) {
                        successAtLeastOnce = true;
                        failureCounter[slot] = 0;
                    } else {
                        failureCounter[slot] = Math.min(failureCounter[slot] + 1, 10);
                    }
                } else {
                    inventory.setStackInSlot(slot, ItemStack.EMPTY);
                    successAtLeastOnce = true;
                    failureCounter[slot] = 0;
                }
            }

            return successAtLeastOnce ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
        } finally {
            inTick = false;
            rwLock.writeLock().unlock();
        }
    }

    @Redirect(
        method = {"markNoUpdate", "uploadSettings"},
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/ticking/ITickManager;alertDevice(Lappeng/api/networking/IGridNode;)Z",
            remap = false
        ),
        require = 0
    )
    private boolean appcompat$redirectAlert(final ITickManager tick,
                                            final appeng.api.networking.IGridNode oldNode) {
        return ManagedNodeHelper.alert(((IGridConnectedTile) this).getMainNode());
    }
}
