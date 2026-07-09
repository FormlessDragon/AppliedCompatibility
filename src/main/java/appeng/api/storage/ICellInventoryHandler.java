package appeng.api.storage;

import appeng.api.config.IncludeExclude;
import appeng.api.storage.data.IAEStack;

/**
 * Old AE wrapper around a cell inventory with partitioning metadata.
 */
public interface ICellInventoryHandler<T extends IAEStack<T>> extends IMEInventoryHandler<T> {

    /**
     * Returns the backing old AE cell inventory when it is available.
     */
    ICellInventory<T> getCellInv();

    /**
     * Returns whether the cell is preformatted.
     */
    boolean isPreformatted();

    /**
     * Returns whether the cell uses fuzzy partition matching.
     */
    boolean isFuzzy();

    /**
     * Returns whether the partition list is a whitelist or blacklist.
     */
    IncludeExclude getIncludeExcludeMode();
}
