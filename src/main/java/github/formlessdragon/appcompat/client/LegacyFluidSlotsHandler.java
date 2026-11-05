package github.formlessdragon.appcompat.client;

import appeng.fluids.container.IFluidSyncContainer;
import github.formlessdragon.appcompat.bridge.ae.LegacyFluidSlotsMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class LegacyFluidSlotsHandler {
    public static void handle(LegacyFluidSlotsMessage message) {
        Minecraft client = Minecraft.getMinecraft();
        client.addScheduledTask(() -> {
            if (client.player != null && client.player.openContainer.windowId == message.windowId
                && client.player.openContainer instanceof IFluidSyncContainer container) container.receiveFluidSlots(message.fluids);
        });
    }
}
