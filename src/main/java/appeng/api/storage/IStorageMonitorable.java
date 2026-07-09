package appeng.api.storage;

import appeng.api.storage.data.IAEStack;

/**
 * Old AE storage monitor provider exposed by grid proxies and storage hosts.
 */
public interface IStorageMonitorable {

    /**
     * Returns the ME monitor for the requested old AE storage channel.
     */
    <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel);
}
