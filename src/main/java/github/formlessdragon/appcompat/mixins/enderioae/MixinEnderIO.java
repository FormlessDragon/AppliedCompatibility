package github.formlessdragon.appcompat.mixins.enderioae;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crazypants.enderio.base.EnderIO;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EnderIO.class, remap = false)
public class MixinEnderIO {

    @Definition(id = "World", type = World.class)
    @Definition(id = "getClassLoader", method = "Ljava/lang/Class;getClassLoader()Ljava/lang/ClassLoader;")
    @Definition(id = "loadClass", method = "Ljava/lang/ClassLoader;loadClass(Ljava/lang/String;)Ljava/lang/Class;")
    @Expression("World.class.getClassLoader().loadClass('net/minecraft/world/World$2')")
    @WrapOperation(method = "initCrashData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static Class<?> initCrashData(ClassLoader instance, String name, Operation<Class<?>> original) {
        return original.call(instance, "net.minecraft.world.World$2");
    }

}
