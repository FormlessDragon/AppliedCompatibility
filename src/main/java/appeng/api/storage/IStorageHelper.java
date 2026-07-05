package appeng.api.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEStack;

import java.util.Collection;

public interface IStorageHelper {

    <T extends IAEStack<T>, C extends IStorageChannel<T>> void registerStorageChannel(Class<C> channel, C factory);

    <T extends IAEStack<T>, C extends IStorageChannel<T>> C getStorageChannel(Class<C> channel);

    Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels();

    <T extends IAEStack<T>> T poweredInsert(IEnergySource energy, IMEInventory<T> cell, T input, IActionSource src, Actionable mode);

    <T extends IAEStack<T>> T poweredExtraction(IEnergySource energy, IMEInventory<T> cell, T request, IActionSource src, Actionable mode);
}
