package appeng.api.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public interface IMEInventory<T extends IAEStack<T>> {

    T injectItems(T input, Actionable mode, IActionSource source);

    T extractItems(T request, Actionable mode, IActionSource source);

    IItemList<T> getStorageList();

    default IItemList<T> getAvailableItems(final IItemList<T> out) {
        if (out == null) {
            throw new IllegalArgumentException("Output item list is required");
        }
        final IItemList<T> storage = getStorageList();
        for (final T stack : storage) {
            out.addStorage(stack);
        }
        return out;
    }

    default IStorageChannel<T> getChannel() {
        throw new UnsupportedOperationException("This ME inventory does not expose a legacy AE storage channel");
    }
}
