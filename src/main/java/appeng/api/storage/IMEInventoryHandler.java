package appeng.api.storage;

import appeng.api.config.AccessRestriction;
import appeng.api.storage.data.IAEStack;

public interface IMEInventoryHandler<T extends IAEStack<T>> extends IMEInventory<T> {

    AccessRestriction getAccess();

    boolean isPrioritized(T input);

    boolean canAccept(T input);

    int getPriority();

    int getSlot();

    boolean validForPass(int i);
}
