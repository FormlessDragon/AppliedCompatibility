package appeng.core.sync.network;

import appeng.core.sync.AppEngPacket;

public class NetworkHandler {
    private static final NetworkHandler INSTANCE = new NetworkHandler();
    private final net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper channel =
        net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.newSimpleChannel("appcompat_legacy");

    private boolean initialized;

    public void initialize() {
        if (initialized) return;
        initialized = true;
        channel.registerMessage(appeng.core.sync.packets.PacketInventoryAction.Handler.class,
            appeng.core.sync.packets.PacketInventoryAction.class, 0, net.minecraftforge.fml.relauncher.Side.SERVER);
        // The dedicated server also needs the discriminator to encode snapshots.
        channel.registerMessage(github.formlessdragon.appcompat.bridge.ae.LegacyFluidSlotsMessage.Handler.class,
            github.formlessdragon.appcompat.bridge.ae.LegacyFluidSlotsMessage.class, 1, net.minecraftforge.fml.relauncher.Side.CLIENT);
        channel.registerMessage(appeng.core.sync.packets.PacketFluidSlot.Handler.class,
            appeng.core.sync.packets.PacketFluidSlot.class, 2, net.minecraftforge.fml.relauncher.Side.SERVER);
    }

    public net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper channel() { return channel; }

    public static NetworkHandler instance() {
        return INSTANCE;
    }

    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void sendToServer(final AppEngPacket message) {
        if (message instanceof appeng.core.sync.packets.PacketInventoryAction action) {
            action.setWindowId(net.minecraft.client.Minecraft.getMinecraft().player.openContainer.windowId);
            channel.sendToServer(action);
        } else if (message instanceof appeng.core.sync.packets.PacketFluidSlot fluid) {
            fluid.setWindowId(net.minecraft.client.Minecraft.getMinecraft().player.openContainer.windowId);
            channel.sendToServer(fluid);
        }
    }
}
