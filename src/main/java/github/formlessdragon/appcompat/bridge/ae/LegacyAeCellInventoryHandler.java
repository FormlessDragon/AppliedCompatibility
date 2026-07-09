package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.storage.cells.StorageCell;
import ae2.api.stacks.AEKey;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public final class LegacyAeCellInventoryHandler<T extends IAEStack<T>> implements ICellInventoryHandler<T> {

    private final StorageCell cell;
    private final IStorageChannel<T> channel;

    public LegacyAeCellInventoryHandler(final StorageCell cell, final IStorageChannel<T> channel) {
        this.cell = cell;
        this.channel = channel;
    }

    @Override
    public T injectItems(final T input, final Actionable mode, final IActionSource source) {
        if (input == null || input.getStackSize() <= 0) {
            return null;
        }
        final AEKey key = LegacyAeStorageBridge.toKey(input);
        ensureChannel(key);
        final long inserted = this.cell.insert(key, input.getStackSize(), LegacyAeStorageBridge.toNewActionable(mode),
            ae2.api.networking.security.IActionSource.empty());
        final long remainder = input.getStackSize() - inserted;
        if (remainder <= 0) {
            return null;
        }
        final T result = input.copy();
        result.setStackSize(remainder);
        return result;
    }

    @Override
    public T extractItems(final T request, final Actionable mode, final IActionSource source) {
        if (request == null || request.getStackSize() <= 0) {
            return null;
        }
        final AEKey key = LegacyAeStorageBridge.toKey(request);
        ensureChannel(key);
        final long extracted = this.cell.extract(key, request.getStackSize(),
            LegacyAeStorageBridge.toNewActionable(mode), ae2.api.networking.security.IActionSource.empty());
        if (extracted <= 0) {
            return null;
        }
        final T result = request.copy();
        result.setStackSize(extracted);
        return result;
    }

    @Override
    public IItemList<T> getStorageList() {
        final IItemList<T> list = LegacyAeStorageBridge.createList(this.channel);
        for (final Object2LongMap.Entry<AEKey> entry : this.cell.getAvailableStacks()) {
            if (entry.getLongValue() > 0 && LegacyAeStorageBridge.matchesChannel(this.channel, entry.getKey())) {
                list.addStorage(LegacyAeStorageBridge.castOldStack(
                    LegacyAeStorageBridge.toOldStack(entry.getKey(), entry.getLongValue())));
            }
        }
        return list;
    }

    @Override
    public IStorageChannel<T> getChannel() {
        return this.channel;
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final T input) {
        if (input == null) {
            return false;
        }
        final AEKey key = LegacyAeStorageBridge.toKey(input);
        return LegacyAeStorageBridge.matchesChannel(this.channel, key)
            && this.cell.isPreferredStorageFor(key, ae2.api.networking.security.IActionSource.empty());
    }

    @Override
    public boolean canAccept(final T input) {
        if (input == null || input.getStackSize() <= 0) {
            return false;
        }
        final AEKey key = LegacyAeStorageBridge.toKey(input);
        return LegacyAeStorageBridge.matchesChannel(this.channel, key)
            && this.cell.insert(key, input.getStackSize(), ae2.api.config.Actionable.SIMULATE,
                ae2.api.networking.security.IActionSource.empty()) > 0;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(final int i) {
        return true;
    }

    @Override
    public ICellInventory<T> getCellInv() {
        return null;
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }

    @Override
    public boolean isFuzzy() {
        return false;
    }

    @Override
    public IncludeExclude getIncludeExcludeMode() {
        return IncludeExclude.WHITELIST;
    }

    private void ensureChannel(final AEKey key) {
        if (!LegacyAeStorageBridge.matchesChannel(this.channel, key)) {
            throw new IllegalArgumentException("Stack " + key + " does not belong to old AE channel " + this.channel.getClass().getName());
        }
    }
}
