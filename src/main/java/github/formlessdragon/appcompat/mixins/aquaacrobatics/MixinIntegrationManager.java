package github.formlessdragon.appcompat.mixins.aquaacrobatics;

import com.fuzs.aquaacrobatics.integration.IntegrationManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = IntegrationManager.class, remap = false)
public abstract class MixinIntegrationManager {

    @ModifyExpressionValue(
        method = "isAE2Enabled",
        at = @At(
            value = "FIELD",
            target = "Lcom/fuzs/aquaacrobatics/integration/IntegrationManager;isAE2Loaded:Z"
        ),
        require = 1
    )
    private static boolean appcompat$disableLegacyAeIntegration(final boolean original) {
        return false;
    }
}
