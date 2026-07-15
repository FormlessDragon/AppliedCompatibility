package appeng.api.networking.crafting;

import appeng.api.storage.data.IAEStack;

/**
 * Old AE watcher supplied to a crafting watcher host for tracking requested stacks.
 */
public interface ICraftingWatcher {

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
