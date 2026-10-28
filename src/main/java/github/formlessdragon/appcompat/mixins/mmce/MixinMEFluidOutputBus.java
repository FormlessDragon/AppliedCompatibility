package github.formlessdragon.appcompat.mixins.mmce;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.me.helpers.IGridConnectedTile;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.data.IAEFluidStack;
import github.formlessdragon.appcompat.bridge.ae.ManagedNodeHelper;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatFluidBridge;
import github.kasuminova.mmce.common.tile.MEFluidOutputBus;
import github.kasuminova.mmce.common.tile.base.MEFluidBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.locks.ReadWriteLock;

@Mixin(value = MEFluidOutputBus.class, remap = false)
public abstract class MixinMEFluidOutputBus extends MEFluidBus implements IGridTickable {

    @Shadow
    public abstract boolean hasFluid();

    @Override
    @Unique
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(5, 60, !hasFluid(), 5);
    }

    @Override
    @Unique
    public TickRateModulation tickingRequest(final IGridNode gridNode, final int ticksSinceLastCall) {
        final IGridConnectedTile hostTile = (IGridConnectedTile) this;
        final IManagedGridNode managedNode = hostTile.getMainNode();
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

        final ReadWriteLock rwLock = tanks.getRWLock();
        try {
            rwLock.writeLock().lock();
            inTick = true;
            boolean successAtLeastOnce = false;

            for (final int slot : needUpdateSlots) {
                changedSlots[slot] = false;
                final IAEFluidStack fluid = tanks.getFluidInSlot(slot);
                if (fluid == null) {
                    continue;
                }

                final IAEFluidStack left = AppCompatFluidBridge.insert(grid, hostTile, fluid);
                if (left == null) {
                    successAtLeastOnce = true;
                } else if (fluid.getStackSize() != left.getStackSize()) {
                    successAtLeastOnce = true;
                }
                tanks.setFluidInSlot(slot, left);
            }

            return successAtLeastOnce ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
        } finally {
            inTick = false;
            rwLock.writeLock().unlock();
        }
    }

    @Redirect(
        method = "markNoUpdate",
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
