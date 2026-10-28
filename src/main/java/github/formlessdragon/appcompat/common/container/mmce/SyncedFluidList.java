package github.formlessdragon.appcompat.common.container.mmce;

import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.GenericStack;
import ae2.container.guisync.PacketWritable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.PacketBuffer;

import java.util.List;

public class SyncedFluidList implements PacketWritable {

    private final List<GenericStack> stacks;

    public SyncedFluidList(final List<GenericStack> stacks) {
        this.stacks = stacks;
    }

    public SyncedFluidList(final ByteBuf data) {
        final PacketBuffer buffer = new PacketBuffer(data);
        final int size = buffer.readVarInt();
        this.stacks = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.stacks.add(GenericStack.readBuffer(buffer));
        }
    }

    public static SyncedFluidList from(final IAEFluidTank tank) {
        final List<GenericStack> list = new ObjectArrayList<>();
        if (tank != null) {
            for (int i = 0; i < tank.getSlots(); i++) {
                final IAEFluidStack fluid = tank.getFluidInSlot(i);
                if (fluid instanceof AEFluidStack carrier && carrier.getKey() != null && carrier.getStackSize() > 0) {
                    list.add(new GenericStack(carrier.getKey(), carrier.getStackSize()));
                } else {
                    list.add(null);
                }
            }
        }
        return new SyncedFluidList(list);
    }

    public AEFluidKey keyAt(final int index) {
        final GenericStack stack = get(index);
        return stack != null && stack.what() instanceof AEFluidKey fluidKey ? fluidKey : null;
    }

    public long amountAt(final int index) {
        final GenericStack stack = get(index);
        return stack == null ? 0 : stack.amount();
    }

    public int size() {
        return this.stacks.size();
    }

    public GenericStack get(final int index) {
        if (index < 0 || index >= this.stacks.size()) {
            return null;
        }
        return this.stacks.get(index);
    }

    @Override
    public void writeToPacket(final ByteBuf data) {
        final PacketBuffer buffer = new PacketBuffer(data);
        buffer.writeVarInt(this.stacks.size());
        for (final GenericStack stack : this.stacks) {
            GenericStack.writeBuffer(stack, buffer);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SyncedFluidList other)) {
            return false;
        }
        return this.stacks.equals(other.stacks);
    }

    @Override
    public int hashCode() {
        return this.stacks.hashCode();
    }
}
