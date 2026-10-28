package github.formlessdragon.appcompat.mixins.mmce.mekeng;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.me.helpers.IGridConnectedTile;
import appeng.api.networking.ticking.ITickManager;
import com.mekeng.github.common.me.inventory.impl.GasInventory;
import github.formlessdragon.appcompat.bridge.ae.ManagedNodeHelper;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatGasBridge;
import github.kasuminova.mmce.common.tile.MEGasInputBus;
import github.kasuminova.mmce.common.tile.base.MEGasBus;
import mekanism.api.gas.GasStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MEGasInputBus.class, remap = false)
public abstract class MixinMEGasInputBus extends MEGasBus implements IGridTickable {

    @Unique
    private static boolean appcompat$sameGas(final GasStack left, final GasStack right) {
        return left != null && right != null && left.isGasEqual(right);
    }

    @Shadow
    public abstract GasInventory getConfig();

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

        final GasInventory config = getConfig();
        synchronized (tanks) {
            inTick = true;
            try {
                boolean successAtLeastOnce = false;
                final int capacity = tanks.tanks()[0].getMaxGas();

                for (final int slot : needUpdateSlots) {
                    changedSlots[slot] = false;
                    final GasStack cfgStack = config.getGasStack(slot);
                    final GasStack invStack = tanks.getGasStack(slot);

                    if (cfgStack == null) {
                        if (invStack != null) {
                            tanks.setGas(slot, AppCompatGasBridge.insert(grid, hostTile, invStack));
                        }
                        continue;
                    }

                    if (!appcompat$sameGas(cfgStack, invStack)) {
                        if (invStack != null) {
                            final GasStack left = AppCompatGasBridge.insert(grid, hostTile, invStack);
                            if (left != null) {
                                tanks.setGas(slot, left);
                                continue;
                            }
                        }
                        final GasStack request = cfgStack.copy();
                        request.amount = capacity;
                        final GasStack extracted = AppCompatGasBridge.extract(grid, hostTile, request);
                        tanks.setGas(slot, extracted);
                        if (extracted != null) {
                            successAtLeastOnce = true;
                        }
                        continue;
                    }

                    if (capacity == invStack.amount) {
                        continue;
                    }

                    if (capacity > invStack.amount) {
                        final GasStack request = invStack.copy();
                        request.amount = capacity - invStack.amount;
                        final GasStack extracted = AppCompatGasBridge.extract(grid, hostTile, request);
                        if (extracted != null) {
                            final GasStack updated = invStack.copy();
                            updated.amount += extracted.amount;
                            tanks.setGas(slot, updated);
                            successAtLeastOnce = true;
                        }
                        continue;
                    }

                    final int countToExtract = invStack.amount - capacity;
                    final GasStack request = invStack.copy();
                    request.amount = countToExtract;
                    final GasStack left = AppCompatGasBridge.insert(grid, hostTile, request);
                    final GasStack updated = invStack.copy();
                    updated.amount = left == null ? capacity : capacity + left.amount;
                    tanks.setGas(slot, updated.amount <= 0 ? null : updated);
                    successAtLeastOnce = true;
                }

                return successAtLeastOnce ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
            } finally {
                inTick = false;
            }
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
