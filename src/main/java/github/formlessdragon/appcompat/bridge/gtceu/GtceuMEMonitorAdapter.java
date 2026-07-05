package github.formlessdragon.appcompat.bridge.gtceu;

import ae2.api.networking.energy.IEnergyService;
import ae2.api.networking.storage.IStorageService;
import ae2.api.stacks.AEKey;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.util.FluidList;
import appeng.util.item.ItemList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public final class GtceuMEMonitorAdapter<T extends IAEStack<T>> implements IMEMonitor<T> {

    private final IStorageChannel<T> channel;
    private final IStorageService storageService;

    public GtceuMEMonitorAdapter(final IStorageChannel<T> channel,
                                 final IStorageService storageService,
                                 final IEnergyService energyService) {
        this.channel = channel;
        this.storageService = storageService;
    }

    @Override
    public T injectItems(final T input, final Actionable mode, final IActionSource source) {
        if (input == null || input.getStackSize() <= 0) {
            return null;
        }
        final AEKey key = GtceuAeKeyBridge.toKey(input);
        ensureChannel(key);
        final long inserted = this.storageService.getInventory().insert(key, input.getStackSize(),
                GtceuAeKeyBridge.toNewActionable(mode), GtceuActionSourceBridge.toNew(source));
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
        final AEKey key = GtceuAeKeyBridge.toKey(request);
        ensureChannel(key);
        final long extracted = this.storageService.getInventory().extract(key, request.getStackSize(),
                GtceuAeKeyBridge.toNewActionable(mode), GtceuActionSourceBridge.toNew(source));
        if (extracted <= 0) {
            return null;
        }
        final T result = request.copy();
        result.setStackSize(extracted);
        return result;
    }

    @Override
    public IItemList<T> getStorageList() {
        final IItemList<T> list = createList();
        for (final Object2LongMap.Entry<AEKey> entry : this.storageService.getInventory().getAvailableStacks()) {
            final AEKey key = entry.getKey();
            if (GtceuAeKeyBridge.matchesChannel(this.channel, key) && entry.getLongValue() > 0) {
                list.addStorage(castOldStack(GtceuAeKeyBridge.toOldStack(key, entry.getLongValue())));
            }
        }
        return list;
    }

    @Override
    public IStorageChannel<T> getChannel() {
        return this.channel;
    }

    private void ensureChannel(final AEKey key) {
        if (!GtceuAeKeyBridge.matchesChannel(this.channel, key)) {
            throw new IllegalArgumentException("Stack " + key + " does not belong to old AE channel " + this.channel.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private IItemList<T> createList() {
        if (this.channel instanceof IItemStorageChannel) {
            return (IItemList<T>) new ItemList();
        }
        if (this.channel instanceof IFluidStorageChannel) {
            return (IItemList<T>) new FluidList();
        }
        throw new IllegalArgumentException("Unsupported old AE storage channel " + this.channel.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private T castOldStack(final IAEStack<?> stack) {
        return (T) stack;
    }
}
