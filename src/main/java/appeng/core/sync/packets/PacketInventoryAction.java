package appeng.core.sync.packets;

import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.IJEITargetSlot;
import appeng.core.sync.AppEngPacket;
import appeng.helpers.InventoryAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketInventoryAction extends AppEngPacket implements IMessage {
    private InventoryAction action;
    private int slot, windowId;
    private long id;
    private ItemStack ghost = ItemStack.EMPTY;

    public PacketInventoryAction() {}

    public PacketInventoryAction(final InventoryAction action, final int slot, final long id) {
        this.action = action;
        this.slot = slot;
        this.id = id;
    }

    public PacketInventoryAction(final InventoryAction action, final IJEITargetSlot slot, final IAEItemStack slotItem) {
        this(action, slot instanceof Slot actual ? actual.slotNumber : -1, 0);
        this.ghost = slotItem == null ? ItemStack.EMPTY : slotItem.createItemStack();
    }

    public void setWindowId(int windowId) { this.windowId = windowId; }
    @Override public void toBytes(ByteBuf buffer) {
        buffer.writeInt(windowId).writeInt(action.ordinal()).writeInt(slot).writeLong(id);
        ByteBufUtils.writeItemStack(buffer, ghost);
    }
    @Override public void fromBytes(ByteBuf buffer) {
        windowId = buffer.readInt();
        int ordinal = buffer.readInt();
        if (ordinal < 0 || ordinal >= InventoryAction.values().length) throw new IllegalArgumentException("Invalid inventory action");
        action = InventoryAction.values()[ordinal]; slot = buffer.readInt(); id = buffer.readLong();
        ghost = ByteBufUtils.readItemStack(buffer);
    }

    public static final class Handler implements IMessageHandler<PacketInventoryAction, IMessage> {
        @Override public IMessage onMessage(PacketInventoryAction message, MessageContext context) {
            final var player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!(player.openContainer instanceof appeng.container.AEBaseContainer container)
                    || container.windowId != message.windowId || !container.canInteractWith(player)) return;
                if (message.action == InventoryAction.PLACE_JEI_GHOST_ITEM && message.slot >= 0
                    && message.slot < container.inventorySlots.size()
                    && container.inventorySlots.get(message.slot) instanceof appeng.container.slot.SlotFake target) {
                    target.putStack(message.ghost.copy());
                    container.detectAndSendChanges();
                } else container.doAction(player, message.action, message.slot, message.id);
            });
            return null;
        }
    }
}
