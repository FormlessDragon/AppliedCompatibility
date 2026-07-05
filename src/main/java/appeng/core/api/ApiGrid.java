package appeng.core.api;

import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHelper;
import appeng.api.networking.IGridNode;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOGridBlockAccess;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOLegacyGridNode;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoLegacyGridNode;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoNodeAccess;

public class ApiGrid implements IGridHelper {

    @Override
    public IGridNode createGridNode(final IGridBlock gridBlock) {
        if (gridBlock == null) {
            throw new IllegalArgumentException("Legacy AE grid block is required");
        }
        if (gridBlock instanceof EnderIOGridBlockAccess access) {
            return new EnderIOLegacyGridNode(gridBlock, access);
        }
        final Object machine = gridBlock.getMachine();
        if (machine instanceof PackagedAutoNodeAccess access) {
            final IGridNode node = new PackagedAutoLegacyGridNode(gridBlock, access);
            access.appcompat$setLegacyNode(node);
            return node;
        }
        throw new IllegalStateException("Legacy AE grid node creation is not registered for " + machine.getClass().getName());
    }
}
