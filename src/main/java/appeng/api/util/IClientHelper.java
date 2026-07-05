package appeng.api.util;

import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.data.IAEStack;

import java.util.List;

public interface IClientHelper {

    <T extends IAEStack<T>> void addCellInformation(ICellInventoryHandler<T> handler, List<String> lines);
}
