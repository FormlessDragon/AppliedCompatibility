package appeng.api;

import appeng.api.definitions.IDefinitions;
import appeng.api.features.IRegistryContainer;
import appeng.api.networking.IGridHelper;
import appeng.api.parts.IPartHelper;
import appeng.api.storage.IStorageHelper;
import appeng.api.util.IClientHelper;

public interface IAppEngApi {

    IRegistryContainer registries();

    IStorageHelper storage();

    IGridHelper grid();

    IPartHelper partHelper();

    IDefinitions definitions();

    IClientHelper client();
}
