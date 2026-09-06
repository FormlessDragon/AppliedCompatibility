package github.formlessdragon.appcompat.mixins.recipehandler;

import assets.recipehandler.Proxy;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import github.formlessdragon.appcompat.AppliedCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Proxy.class, remap = false)
public class MixinProxy {

    @WrapOperation(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fml/common/Loader;isModLoaded(Ljava/lang/String;)Z",
            remap = false
        ),
        require = 1
    )
    private boolean ignoreLegacyAE2Integration(String modId, Operation<Boolean> original) {
        return !AppliedCompatibility.LEGACY_AE_MOD_ID.equals(modId) && original.call(modId);
    }
}
