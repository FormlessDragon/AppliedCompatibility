package github.formlessdragon.appcompat.mixins.enderioae;

import crazypants.enderio.base.EnderIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EnderIO.class, remap = false)
public class MixinEnderIO {

    @ModifyArg(
        method = "initCrashData",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/ClassLoader;loadClass(Ljava/lang/String;)Ljava/lang/Class;",
            remap = false
        ),
        remap = false
    )
    private static String initCrashData(String name) {
        return "net.minecraft.world.World$2";
    }

}
