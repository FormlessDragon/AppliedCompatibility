package appeng.api.storage;

import appeng.api.storage.data.IAEStack;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICellRegistry {

    void addCellHandler(@Nonnull ICellHandler handler);

    void addCellGuiHandler(@Nonnull ICellGuiHandler handler);

    boolean isCellHandled(ItemStack is);

    @Nullable
    ICellHandler getHandler(ItemStack is);

    @Nullable
    <T extends IAEStack<T>> ICellGuiHandler getGuiHandler(IStorageChannel<T> channel, ItemStack is);

    @Nullable
    <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(ItemStack is, ISaveProvider host,
                                                                      IStorageChannel<T> chan);
}
