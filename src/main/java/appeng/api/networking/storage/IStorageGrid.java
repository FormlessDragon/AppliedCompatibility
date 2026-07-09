package appeng.api.networking.storage;

import appeng.api.networking.IGridCache;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEStack;

public interface IStorageGrid extends IGridCache, IStorageMonitorable {

    @Override
    <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel);
}
