package github.formlessdragon.appcompat.bridge.enderioae;

import ae2.api.networking.IGridNode;
import ae2.api.networking.IGridNodeListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class EnderIONodeListener implements IGridNodeListener<EnderIOLegacyGridNode> {

    public static final EnderIONodeListener INSTANCE = new EnderIONodeListener();

    private EnderIONodeListener() {
    }

    @Override
    public void onSaveChanges(final EnderIOLegacyGridNode nodeOwner, final IGridNode node) {
        final World world = nodeOwner.access().appcompat$world();
        final TileEntity tile = world.getTileEntity(nodeOwner.access().appcompat$pos());
        if (tile == null) {
            throw new IllegalStateException("EnderIO ME conduit tile is missing at " + nodeOwner.access().appcompat$pos());
        }
        tile.markDirty();
    }

    @Override
    public void onStateChanged(final EnderIOLegacyGridNode nodeOwner, final IGridNode node, final State state) {
        nodeOwner.access().appcompat$gridChanged();
    }

    @Override
    public void onGridChanged(final EnderIOLegacyGridNode nodeOwner, final IGridNode node) {
        nodeOwner.access().appcompat$gridChanged();
    }
}
