package github.formlessdragon.appcompat.bridge.ae;

import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.AEFluidStack;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server-to-client snapshot for the currently open legacy fluid container. */
public final class LegacyFluidSlotsMessage implements IMessage {
    public int windowId;
    public final Map<Integer, IAEFluidStack> fluids = new LinkedHashMap<>();
    public LegacyFluidSlotsMessage() {}
    public LegacyFluidSlotsMessage(int windowId, Map<Integer, IAEFluidStack> fluids) {
        this.windowId = windowId;
        fluids.forEach((slot, stack) -> this.fluids.put(slot, stack == null ? null : stack.copy()));
    }
    @Override public void toBytes(ByteBuf buffer) {
        buffer.writeInt(windowId).writeInt(fluids.size());
        fluids.forEach((slot, stack) -> {
            buffer.writeInt(slot);
            NBTTagCompound data = null;
            if (stack != null) { data = new NBTTagCompound(); stack.writeToNBT(data); }
            ByteBufUtils.writeTag(buffer, data);
        });
    }
    @Override public void fromBytes(ByteBuf buffer) {
        fluids.clear();
        windowId = buffer.readInt();
        int size = buffer.readInt();
        if (size < 0 || size > 1024) throw new IllegalArgumentException("Invalid fluid slot count");
        for (int i = 0; i < size; i++) {
            int slot = buffer.readInt();
            NBTTagCompound data = ByteBufUtils.readTag(buffer);
            fluids.put(slot, data == null ? null : AEFluidStack.fromNBT(data));
        }
    }

    /** Safe to register on either physical side; the proxy owns client access. */
    public static final class Handler implements IMessageHandler<LegacyFluidSlotsMessage, IMessage> {
        @Override public IMessage onMessage(LegacyFluidSlotsMessage message, MessageContext context) {
            github.formlessdragon.appcompat.AppliedCompatibility.proxy.receiveLegacyFluidSlots(message);
            return null;
        }
    }
}
