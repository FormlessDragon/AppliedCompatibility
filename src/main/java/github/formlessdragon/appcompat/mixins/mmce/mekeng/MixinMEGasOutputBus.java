package github.formlessdragon.appcompat.mixins.mmce.mekeng;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.me.helpers.IGridConnectedTile;
import appeng.api.networking.ticking.ITickManager;
import github.formlessdragon.appcompat.bridge.ae.ManagedNodeHelper;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatGasBridge;
import github.kasuminova.mmce.common.tile.MEGasOutputBus;
import github.kasuminova.mmce.common.tile.base.MEGasBus;
import mekanism.api.gas.GasStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MEGasOutputBus.class, remap = false)
public abstract class MixinMEGasOutputBus extends MEGasBus implements IGridTickable {

    @Shadow
    public abstract boolean hasGas();

    @Override
    @Unique
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(5, 60, !hasGas(), 5);
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

        synchronized (tanks) {
            inTick = true;
            try {
                boolean successAtLeastOnce = false;

                for (final int slot : needUpdateSlots) {
                    changedSlots[slot] = false;
                    final GasStack gas = tanks.getGasStack(slot);
                    if (gas == null) {
                        continue;
                    }

                    final GasStack left = AppCompatGasBridge.insert(grid, hostTile, gas);
                    if (left == null || gas.amount != left.amount) {
                        successAtLeastOnce = true;
                    }
                    tanks.setGas(slot, left);
                }

                return successAtLeastOnce ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
            } finally {
                inTick = false;
            }
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
