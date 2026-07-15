package appeng.api.networking.storage;

import appeng.api.storage.data.IAEStack;

/**
 * Old AE watcher supplied to a storage watcher host for tracking storage-stack changes.
 */
public interface IStackWatcher {

    /**
     * Adds a stack to this watcher.
     */
    boolean add(IAEStack<?> stack);

    /**
     * Removes a stack from this watcher.
     */
    boolean remove(IAEStack<?> stack);

    /**
     * Clears all watched stacks.
     */
    void reset();
}
