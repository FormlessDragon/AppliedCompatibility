package appeng.core.sync;

import ae2.container.GuiIds;
import ae2.core.gui.AEGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public enum GuiBridge implements IGuiHandler {
    GUI_RENAMER(GuiIds.GuiKey.RENAMER),
    GUI_WIRELESS_TERM(GuiIds.GuiKey.WIRELESS_TERMINAL),
    GUI_WIRELESS_CRAFTING_TERMINAL(GuiIds.GuiKey.WIRELESS_CRAFTING_TERMINAL),
    GUI_WIRELESS_PATTERN_TERMINAL(GuiIds.GuiKey.WIRELESS_PATTERN_ENCODING_TERMINAL),
    GUI_WIRELESS_FLUID_TERMINAL(GuiIds.GuiKey.WIRELESS_TERMINAL);

    private final GuiIds.GuiKey guiKey;

    GuiBridge(final GuiIds.GuiKey guiKey) {
        this.guiKey = guiKey;
    }

    public GuiIds.GuiKey getGuiKey() {
        return this.guiKey;
    }

    @Override
    public Object getServerGuiElement(final int id, final EntityPlayer player, final World world,
                                      final int x, final int y, final int z) {
        return new AEGuiHandler().getServerGuiElement(this.guiKey.getGuiId(), player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world,
                                      final int x, final int y, final int z) {
        return new AEGuiHandler().getClientGuiElement(this.guiKey.getGuiId(), player, world, x, y, z);
    }
}
