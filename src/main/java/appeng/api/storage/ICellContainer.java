package appeng.api.storage;

import appeng.api.networking.security.IActionHost;

public interface ICellContainer extends IActionHost, ICellProvider, ISaveProvider {

    void blinkCell(int slot);
}
