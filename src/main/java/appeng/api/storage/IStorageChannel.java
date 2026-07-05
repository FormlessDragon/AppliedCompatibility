package appeng.api.storage;

import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public interface IStorageChannel<T extends IAEStack<T>> {

    default int transferFactor() {
        return 1;
    }

    default int getUnitsPerByte() {
        return 8;
    }

    IItemList<T> createList();

    T createStack(Object input);

    T readFromPacket(ByteBuf input) throws IOException;

    T createFromNBT(NBTTagCompound nbt);
}
