package github.formlessdragon.appcompat.mixins.congregamystica;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import congregamystica.integrations.ModuleManager;
import congregamystica.utils.libs.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModuleManager.class, remap = false)
public abstract class MixinModuleManager {

    @WrapOperation(
        method = "initModModules",
        at = @At(
            value = "FIELD",
            target = "Lcongregamystica/utils/libs/ModIds;isLoaded:Z"
        ),
        require = 1
    )
    private static boolean appcompat$disableLegacyAeIntegration(final ModIds instance,
                                                               final Operation<Boolean> original) {
        return instance != ModIds.applied_energistics && original.call(instance);
    }
}
