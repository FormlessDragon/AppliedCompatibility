package github.formlessdragon.appcompat.mixins.ae;

import ae2.api.config.Actionable;
import ae2.api.config.PowerMultiplier;
import ae2.tile.grid.AENetworkedPoweredTile;
import ae2.tile.misc.TileCharger;
import ae2.util.inv.AppEngInternalInventory;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemPowerStorageBridge;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileCharger.class, remap = false)
public abstract class MixinTileCharger extends AENetworkedPoweredTile {

    @Shadow
    @Final
    private AppEngInternalInventory inv;

    @Shadow
    private boolean working;

    @Inject(method = "doWork", at = @At("HEAD"), cancellable = true)
    private void appcompat$chargeLegacyPoweredItem(final int ticksSinceLastCall, final CallbackInfo ci) {
        final ItemStack stack = this.inv.getStackInSlot(0);
        if (!LegacyAeItemPowerStorageBridge.isChargeable(stack)
            || stack.getItem() instanceof ae2.api.implementations.items.IAEItemPowerStorage) {
            return;
        }

        final boolean wasWorking = this.working;
        this.working = false;
        boolean changed = false;

        final double currentPower = LegacyAeItemPowerStorageBridge.getCurrentPower(stack);
        final double maxPower = LegacyAeItemPowerStorageBridge.getMaxPower(stack);
        if (currentPower < maxPower) {
            final double chargeRate = LegacyAeItemPowerStorageBridge.getChargeRate(stack);
            double extractedAmount = this.extractAEPower(chargeRate, Actionable.MODULATE, PowerMultiplier.CONFIG);

            final double missingChargeRate = chargeRate - extractedAmount;
            final double missingAEPower = maxPower - currentPower;
            final double toExtract = Math.min(missingChargeRate, missingAEPower);

            final ae2.api.networking.IGrid grid = this.getMainNode().getGrid();
            if (grid != null) {
                extractedAmount += grid.getEnergyService()
                                       .extractAEPower(toExtract, Actionable.MODULATE, PowerMultiplier.ONE);
            }

            if (extractedAmount > 0.0D) {
                final double remainder = LegacyAeItemPowerStorageBridge.injectPower(stack, extractedAmount,
                    Actionable.MODULATE);
                this.setInternalCurrentPower(this.getInternalCurrentPower() + remainder);
                this.working = true;
                changed = true;
            }
        }

        if (this.getInternalCurrentPower() < 1599.0D) {
            final ae2.api.networking.IGrid grid = this.getMainNode().getGrid();
            if (grid != null) {
                final double toExtract = Math.min(800.0D, this.getInternalMaxPower() - this.getInternalCurrentPower());
                final double extracted = grid.getEnergyService()
                                             .extractAEPower(toExtract, Actionable.MODULATE, PowerMultiplier.ONE);
                this.injectExternalPower(ae2.api.config.PowerUnit.AE, extracted, Actionable.MODULATE);
                changed = changed || extracted > 0.0D;
            }
        }

        if (changed) {
            this.saveChanges();
        }
        if (changed || this.working != wasWorking) {
            this.markForUpdate();
        }
        ci.cancel();
    }
}
