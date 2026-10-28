package github.formlessdragon.appcompat.mixins.moremekasuitmodules;

import ae2.client.gui.me.common.GuiMEStorage;
import ae2.client.gui.me.items.GuiCraftingTerm;
import ae2.client.gui.me.patternencode.GuiPatternEncodingTerm;
import ae2.client.gui.style.GuiStyleManager;
import ae2.container.AEBaseContainer;
import ae2.container.me.common.ContainerMEStorage;
import ae2.container.me.items.ContainerCraftingTerm;
import ae2.container.me.patternencode.ContainerPatternEncodingTerm;
import ae2.core.gui.AEGuiHandler;
import github.formlessdragon.appcompat.bridge.moremekasuitmodules.MekaSuitWirelessBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEGuiHandler.class, remap = false)
public abstract class MixinAEGuiHandlerClient {

    @Inject(method = "getClientGuiElement", at = @At("HEAD"), cancellable = true)
    private void appcompat$openHelmetTerminal(final int ID, final EntityPlayer player, final World world,
                                              final int x, final int y, final int z,
                                              final CallbackInfoReturnable<Object> cir) {
        if (!MekaSuitWirelessBridge.handlesGui(ID, player, x)) {
            return;
        }
        final AEBaseContainer container = MekaSuitWirelessBridge.createContainer(ID, player, x);
        switch (container) {
            case ContainerPatternEncodingTerm pattern ->
                cir.setReturnValue(new GuiPatternEncodingTerm(pattern, player.inventory, null,
                    GuiStyleManager.loadStyleDoc("/screens/terminals/pattern_encoding_terminal.json")));
            case ContainerCraftingTerm crafting ->
                cir.setReturnValue(new GuiCraftingTerm(crafting, player.inventory, null,
                    GuiStyleManager.loadStyleDoc("/screens/terminals/crafting_terminal.json")));
            case ContainerMEStorage storage -> cir.setReturnValue(new GuiMEStorage<>(storage, player.inventory, null,
                GuiStyleManager.loadStyleDoc("/screens/terminals/wireless_terminal.json")));
            case null, default -> cir.setReturnValue(null);
        }
    }
}
