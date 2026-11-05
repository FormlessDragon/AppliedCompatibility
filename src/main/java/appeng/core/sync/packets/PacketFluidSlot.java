package appeng.core.sync.packets;

import appeng.api.storage.data.IAEFluidStack;
import appeng.core.sync.AppEngPacket;
import appeng.fluids.container.ContainerFluidConfigurable;
import github.formlessdragon.appcompat.bridge.ae.LegacyFluidSlotsMessage;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Only phantom configuration containers accept client fluid edits. */
public final class PacketFluidSlot extends AppEngPacket implements IMessage {
    private final LegacyFluidSlotsMessage data;
    public PacketFluidSlot() { data = new LegacyFluidSlotsMessage(); }
    public PacketFluidSlot(Map<Integer, IAEFluidStack> fluids) { data = new LegacyFluidSlotsMessage(0, fluids); }
    public void setWindowId(int id) { data.windowId = id; }
    @Override public void toBytes(ByteBuf buffer) { data.toBytes(buffer); }
    @Override public void fromBytes(ByteBuf buffer) { data.fromBytes(buffer); }
    public static final class Handler implements IMessageHandler<PacketFluidSlot, IMessage> {
        @Override public IMessage onMessage(PacketFluidSlot message, MessageContext context) {
            final var player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (player.openContainer instanceof ContainerFluidConfigurable container
                    && container.windowId == message.data.windowId && container.canInteractWith(player)) {
                    container.receiveFluidSlots(message.data.fluids);
                    container.detectAndSendChanges();
                }
            });
            return null;
        }
    }
}
