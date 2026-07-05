package github.formlessdragon.appcompat.bridge.enderioae;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;

public final class EnderIOLegacyGridConnection implements IGridConnection {

    private final ae2.api.networking.IGridConnection connection;
    private final ae2.api.networking.IGridNode localNode;
    private final IGridNode localLegacyNode;

    public EnderIOLegacyGridConnection(final ae2.api.networking.IGridConnection connection,
                                       final ae2.api.networking.IGridNode localNode,
                                       final IGridNode localLegacyNode) {
        this.connection = connection;
        this.localNode = localNode;
        this.localLegacyNode = localLegacyNode;
    }

    @Override
    public IGridNode getOtherSide(final IGridNode gridNode) {
        return legacyNode(this.connection.getOtherSide(unwrap(gridNode)));
    }

    @Override
    public AEPartLocation getDirection(final IGridNode gridNode) {
        return AEPartLocation.fromFacing(this.connection.getDirection(unwrap(gridNode)));
    }

    @Override
    public void destroy() {
        this.connection.destroy();
    }

    @Override
    public IGridNode a() {
        return legacyNode(this.connection.a());
    }

    @Override
    public IGridNode b() {
        return legacyNode(this.connection.b());
    }

    @Override
    public boolean hasDirection() {
        return this.connection.isInWorld();
    }

    @Override
    public int getUsedChannels() {
        return this.connection.getUsedChannels();
    }

    private ae2.api.networking.IGridNode unwrap(final IGridNode node) {
        if (node == this.localLegacyNode) {
            return this.localNode;
        }
        if (node instanceof EnderIOLegacyGridNode enderIONode) {
            return enderIONode.newNode();
        }
        if (node instanceof EnderIOLegacyExternalGridNode externalNode) {
            return externalNode.unwrap();
        }
        throw new IllegalArgumentException("Unsupported old AE grid node implementation " + node.getClass().getName());
    }

    private IGridNode legacyNode(final ae2.api.networking.IGridNode node) {
        if (node == this.localNode) {
            return this.localLegacyNode;
        }
        return new EnderIOLegacyExternalGridNode(node);
    }
}
