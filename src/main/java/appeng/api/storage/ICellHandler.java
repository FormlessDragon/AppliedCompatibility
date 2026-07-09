package appeng.api.storage;

import appeng.api.storage.data.IAEStack;
import net.minecraft.item.ItemStack;

/**
 * Old AE storage cell handler API used by integrations that expose custom cell inventories.
 */
public interface ICellHandler {

    /**
     * Returns whether this handler can provide a cell inventory for the stack.
     */
    boolean isCell(ItemStack is);

    /**
     * Returns an old AE cell inventory handler for the stack and requested channel.
     */
    <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(ItemStack is, ISaveProvider host,
                                                                      IStorageChannel<T> channel);

    /**
     * Returns the drive/chest status code for this cell.
     */
    default <T extends IAEStack<T>> int getStatusForCell(final ItemStack is, final ICellInventoryHandler<T> handler) {
        if (handler == null || handler.getCellInv() == null) {
            return 0;
        }
        int status = handler.getCellInv().getStatusForCell();
        if (status == 1 && handler.isPreformatted()) {
            status = 2;
        }
        return status;
    }

    /**
     * Returns the idle AE/t drain for this cell.
     */
    default <T extends IAEStack<T>> double cellIdleDrain(final ItemStack is, final ICellInventoryHandler<T> handler) {
        if (handler == null || handler.getCellInv() == null) {
            return 1.0D;
        }
        return handler.getCellInv().getIdleDrain();
    }
}
