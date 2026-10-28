package github.formlessdragon.appcompat.mixins.moremekasuitmodules;

import ae2.core.gui.AEGuiHandler;
import github.formlessdragon.appcompat.bridge.moremekasuitmodules.MekaSuitWirelessBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEGuiHandler.class, remap = false)
public abstract class MixinAEGuiHandler {

    @Inject(method = "getServerGuiElement", at = @At("HEAD"), cancellable = true)
    private void appcompat$openHelmetTerminal(final int ID, final EntityPlayer player, final World world,
                                              final int x, final int y, final int z,
                                              final CallbackInfoReturnable<Object> cir) {
        if (MekaSuitWirelessBridge.handlesGui(ID, player, x)) {
            cir.setReturnValue(MekaSuitWirelessBridge.createContainer(ID, player, x));
        }
    }
}
