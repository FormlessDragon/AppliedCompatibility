package appeng.api.storage;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.data.IAEStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Old AE GUI hook for storage cells placed in ME chests or drives.
 */
public interface ICellGuiHandler {

    /**
     * Returns whether this GUI handler supports the storage channel.
     */
    <T extends IAEStack<T>> boolean isHandlerFor(IStorageChannel<T> channel);

    /**
     * Returns whether this GUI handler should win over the channel fallback for this stack.
     */
    default boolean isSpecializedFor(final ItemStack is) {
        return false;
    }

    /**
     * Opens a GUI for the cell.
     */
    <T extends IAEStack<T>> void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler,
                                             IMEInventoryHandler<T> inv, ItemStack is, IStorageChannel<T> chan);
}
