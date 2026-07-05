package appeng.core;

import appeng.api.definitions.IBlocks;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItems;
import appeng.api.definitions.IParts;
import appeng.api.definitions.IMaterials;
import appeng.core.api.definitions.ApiBlocks;
import appeng.core.api.definitions.ApiItems;
import appeng.core.api.definitions.ApiMaterials;
import appeng.core.api.definitions.ApiParts;

public final class ApiDefinitions implements IDefinitions {

    private final IItems items = new ApiItems();
    private final IBlocks blocks = new ApiBlocks();
    private final IMaterials materials = new ApiMaterials();
    private final IParts parts = new ApiParts();

    @Override
    public IItems items() {
        return this.items;
    }

    @Override
    public IBlocks blocks() {
        return this.blocks;
    }

    @Override
    public IMaterials materials() {
        return this.materials;
    }

    @Override
    public IParts parts() {
        return this.parts;
    }
}
