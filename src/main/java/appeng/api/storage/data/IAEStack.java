package appeng.api.storage.data;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public interface IAEStack<T extends IAEStack<T>> {

    void add(T option);

    long getStackSize();

    T setStackSize(long size);

    long getCountRequestable();

    T setCountRequestable(long countRequestable);

    boolean isCraftable();

    T setCraftable(boolean craftable);

    T reset();

    boolean isMeaningful();

    void incStackSize(long amount);

    void decStackSize(long amount);

    void incCountRequestable(long amount);

    void decCountRequestable(long amount);

    boolean fuzzyComparison(T stack, FuzzyMode mode);

    void writeToPacket(ByteBuf data);

    void writeToNBT(NBTTagCompound data);

    T copy();

    T empty();

    boolean isItem();

    boolean isFluid();

    IStorageChannel<T> getChannel();

    ItemStack asItemStackRepresentation();
}
