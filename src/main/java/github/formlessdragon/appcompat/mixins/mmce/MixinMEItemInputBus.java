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
import github.kasuminova.mmce.common.tile.MEItemInputBus;
import github.kasuminova.mmce.common.tile.base.MEItemBus;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.locks.ReadWriteLock;

@Mixin(value = MEItemInputBus.class, remap = false)
public abstract class MixinMEItemInputBus extends MEItemBus implements IGridTickable {

    @Shadow
    public abstract IOInventory getConfigInventory();

    @Shadow
    private boolean needsUpdate() {
        throw new AbstractMethodError();
    }

    @Override
    @Unique
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(10, 120, !needsUpdate(), 10);
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

        final MEStorage net = grid.getStorageService().getInventory();
        final ActionHostEnergySource energy = new ActionHostEnergySource(host);
        final IActionSource source = IActionSource.ofMachine((IActionHost) this);
        final IOInventory configInventory = getConfigInventory();

        final ReadWriteLock rwLock = inventory.getRWLock();
        try {
            rwLock.writeLock().lock();
            boolean successAtLeastOnce = false;
            inTick = true;

            for (final int slot : needUpdateSlots) {
                changedSlots[slot] = false;
                final ItemStack cfgStack = configInventory.getStackInSlot(slot);
                final ItemStack invStack = inventory.getStackInSlot(slot);

                if (cfgStack.isEmpty()) {
                    if (invStack.isEmpty()) {
                        continue;
                    }
                    inventory.setStackInSlot(slot, appcompat$insertToAE(net, energy, source, invStack));
                    continue;
                }

                if (!ItemUtils.matchStacks(cfgStack, invStack)) {
                    final ItemStack remaining = invStack.isEmpty()
                        ? ItemStack.EMPTY : appcompat$insertToAE(net, energy, source, invStack);
                    inventory.setStackInSlot(slot, remaining);
                    if (remaining.isEmpty()) {
                        final ItemStack stack = appcompat$extractFromAE(net, energy, source, cfgStack);
                        inventory.setStackInSlot(slot, stack);
                        if (!stack.isEmpty()) {
                            successAtLeastOnce = true;
                        }
                    }
                    continue;
                }

                if (cfgStack.getCount() == invStack.getCount()) {
                    continue;
                }

                if (cfgStack.getCount() > invStack.getCount()) {
                    final int countToReceive = cfgStack.getCount() - invStack.getCount();
                    final ItemStack stack = appcompat$extractFromAE(net, energy, source, ItemUtils.copyStackWithSize(invStack, countToReceive));
                    if (!stack.isEmpty()) {
                        final int newCount = invStack.getCount() + stack.getCount();
                        inventory.setStackInSlot(slot, ItemUtils.copyStackWithSize(invStack, newCount));
                        successAtLeastOnce = true;
                        failureCounter[slot] = 0;
                    } else {
                        failureCounter[slot]++;
                    }
                } else {
                    final int countToExtract = invStack.getCount() - cfgStack.getCount();
                    final ItemStack stack = appcompat$insertToAE(net, energy, source, ItemUtils.copyStackWithSize(invStack, countToExtract));
                    if (stack.isEmpty()) {
                        inventory.setStackInSlot(slot, ItemUtils.copyStackWithSize(invStack, invStack.getCount() - countToExtract));
                    } else {
                        inventory.setStackInSlot(slot, ItemUtils.copyStackWithSize(invStack, invStack.getCount() - countToExtract + stack.getCount()));
                    }
                    successAtLeastOnce = true;
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

    @Unique
    private ItemStack appcompat$insertToAE(final MEStorage net, final ActionHostEnergySource energy,
                                           final IActionSource source, final ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }
        final AEItemKey key = AEItemKey.of(stack);
        if (key == null) {
            return stack;
        }
        final long inserted = StorageHelper.poweredInsert(energy, net, key, stack.getCount(), source);
        final int remaining = stack.getCount() - (int) inserted;
        if (remaining <= 0) {
            return ItemStack.EMPTY;
        }
        return ItemUtils.copyStackWithSize(stack, remaining);
    }

    @Unique
    private ItemStack appcompat$extractFromAE(final MEStorage net, final ActionHostEnergySource energy,
                                              final IActionSource source, final ItemStack template) {
        if (template.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final AEItemKey key = AEItemKey.of(template);
        if (key == null) {
            return ItemStack.EMPTY;
        }
        final long extracted = StorageHelper.poweredExtraction(energy, net, key, template.getCount(), source);
        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }
        return key.toStack((int) extracted);
    }
}
