package github.formlessdragon.appcompat.common.container.mmce;

import ae2.api.stacks.GenericStack;
import ae2.container.guisync.PacketWritable;
import com.mekeng.github.common.me.inventory.impl.GasInventory;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.ramidzkh.mekae2.ae2.AEGasKey;
import mekanism.api.gas.GasStack;
import net.minecraft.network.PacketBuffer;

import java.util.List;

public class SyncedGasList implements PacketWritable {

    private final List<GenericStack> stacks;

    public SyncedGasList(final List<GenericStack> stacks) {
        this.stacks = stacks;
    }

    public SyncedGasList(final ByteBuf data) {
        final PacketBuffer buffer = new PacketBuffer(data);
        final int size = buffer.readVarInt();
        this.stacks = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.stacks.add(GenericStack.readBuffer(buffer));
        }
    }

    public static SyncedGasList from(final GasInventory inventory) {
        final List<GenericStack> list = new ObjectArrayList<>();
        if (inventory != null) {
            for (int i = 0; i < inventory.size(); i++) {
                final GasStack gas = inventory.getGasStack(i);
                final AEGasKey key = gas == null ? null : AEGasKey.of(gas);
                if (key != null && gas.amount > 0) {
                    list.add(new GenericStack(key, gas.amount));
                } else {
                    list.add(null);
                }
            }
        }
        return new SyncedGasList(list);
    }

    public AEGasKey keyAt(final int index) {
        final GenericStack stack = get(index);
        return stack != null && stack.what() instanceof AEGasKey gasKey ? gasKey : null;
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
        if (!(obj instanceof SyncedGasList other)) {
            return false;
        }
        return this.stacks.equals(other.stacks);
    }

    @Override
    public int hashCode() {
        return this.stacks.hashCode();
    }
}
