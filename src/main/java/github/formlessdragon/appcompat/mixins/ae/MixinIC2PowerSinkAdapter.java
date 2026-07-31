package github.formlessdragon.appcompat.mixins.ae;

import ae2.api.config.PowerUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "ae2.integration.modules.ic2.IC2PowerSinkAdapter", remap = false)
public abstract class MixinIC2PowerSinkAdapter {

    private static final double MAX_SAFE_EU_DEMAND = Double.MAX_VALUE / PowerUnit.EU.conversionRatio;

    @ModifyArg(
        method = "getDemandedEnergy",
        at = @At(
            value = "INVOKE",
            target = "Lae2/tile/powersink/IExternalPowerSink;getExternalPowerDemand(Lae2/api/config/PowerUnit;D)D",
            remap = false
        ),
        index = 1,
        require = 1
    )
    private double appcompat$boundEuDemand(final double maximumEuDemand) {
        if (maximumEuDemand == Double.MAX_VALUE) {
            return MAX_SAFE_EU_DEMAND;
        }
        if (!Double.isFinite(maximumEuDemand) || maximumEuDemand < 0.0D) {
            throw new IllegalArgumentException("IC2 power demand limit must be finite and non-negative: "
                + maximumEuDemand);
        }
        if (maximumEuDemand > MAX_SAFE_EU_DEMAND) {
            throw new IllegalArgumentException("IC2 power demand limit exceeds AE's finite EU conversion range: "
                + maximumEuDemand);
        }
        return maximumEuDemand;
    }
}
