package appeng.api.networking.crafting;

import appeng.api.storage.data.IAEItemStack;

/**
 * Old AE crafting watcher host ABI implemented by request-capable network devices.
 */
public interface ICraftingWatcherHost {

    /**
     * Supplies the watcher for the host's current grid after its previous watcher was detached.
     */
    void updateWatcher(ICraftingWatcher newWatcher);

    /**
     * Reports a change to the crafting request state of an item.
     */
    void onRequestChange(ICraftingGrid craftingGrid, IAEItemStack what);
}
