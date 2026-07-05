package appeng.api.storage;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface ICellInventory<T extends IAEStack<T>> extends IMEInventory<T> {

    IStorageChannel<T> getChannel();

    ItemStack getItemStack();

    double getIdleDrain();

    FuzzyMode getFuzzyMode();

    IItemHandler getConfigInventory();

    IItemHandler getUpgradesInventory();

    int getBytesPerType();

    boolean canHoldNewItem();

    long getTotalBytes();

    long getFreeBytes();

    long getUsedBytes();

    long getTotalItemTypes();

    long getStoredItemCount();

    long getStoredItemTypes();

    long getRemainingItemTypes();

    long getRemainingItemCount();

    int getUnusedItemCount();

    int getStatusForCell();

    void persist();
}
