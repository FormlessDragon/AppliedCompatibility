package appeng.core;

import appeng.api.IAppEngApi;
import appeng.api.definitions.IDefinitions;
import appeng.api.features.IRegistryContainer;
import appeng.api.networking.IGridHelper;
import appeng.api.parts.IPartHelper;
import appeng.api.storage.IStorageHelper;
import appeng.api.util.IClientHelper;
import appeng.core.api.ApiClientHelper;
import appeng.core.api.ApiGrid;
import appeng.core.api.ApiPart;
import appeng.core.api.ApiStorage;
import appeng.core.features.registries.RegistryContainer;

public final class Api implements IAppEngApi {

    public static final Api INSTANCE = new Api();

    private final IGridHelper grid = new ApiGrid();
    private final IRegistryContainer registries = new RegistryContainer();
    private final IStorageHelper storage = new ApiStorage();
    private final IPartHelper partHelper = new ApiPart();
    private final IClientHelper clientHelper = new ApiClientHelper();
    private final IDefinitions definitions = new ApiDefinitions();

    private Api() {
    }

    @Override
    public IGridHelper grid() {
        return this.grid;
    }

    @Override
    public IRegistryContainer registries() {
        return this.registries;
    }

    @Override
    public IStorageHelper storage() {
        return this.storage;
    }

    @Override
    public IPartHelper partHelper() {
        return this.partHelper;
    }

    @Override
    public IDefinitions definitions() {
        return this.definitions;
    }

    @Override
    public IClientHelper client() {
        return this.clientHelper;
    }
}
