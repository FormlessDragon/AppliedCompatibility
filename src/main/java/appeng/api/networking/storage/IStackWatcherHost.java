package appeng.api.networking.storage;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * Old AE storage watcher host ABI implemented by devices that react to ME inventory changes.
 */
public interface IStackWatcherHost {

    /**
     * Supplies the watcher for the host's current grid after its previous watcher was detached.
     */
    void updateWatcher(IStackWatcher newWatcher);

    /**
     * Reports a watched stack change from the ME network.
     */
    void onStackChange(IItemList<?> previous, IAEStack<?> fullStack, IAEStack<?> diffStack, IActionSource source,
                       IStorageChannel<?> channel);
}
