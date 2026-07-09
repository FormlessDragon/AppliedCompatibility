package github.formlessdragon.appcompat.mixins.enderioae;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crazypants.enderio.base.EnderIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EnderIO.class, remap = false)
public class MixinEnderIO {

    @WrapOperation(method = "initCrashData", at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;loadClass(Ljava/lang/String;)Ljava/lang/Class;"))
    private static Class<?> initCrashData(ClassLoader instance, String name, Operation<Class<?>> original) {
        return original.call(instance, "net.minecraft.world.World$2");
    }

}
