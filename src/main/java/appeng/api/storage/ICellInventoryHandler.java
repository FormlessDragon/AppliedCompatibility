package appeng.api.storage;

import appeng.api.config.IncludeExclude;
import appeng.api.storage.data.IAEStack;

import javax.annotation.Nullable;

public interface ICellInventoryHandler<T extends IAEStack<T>> extends IMEInventoryHandler<T> {

    @Nullable
    ICellInventory<T> getCellInv();

    boolean isPreformatted();

    boolean isFuzzy();

    IncludeExclude getIncludeExcludeMode();
}
