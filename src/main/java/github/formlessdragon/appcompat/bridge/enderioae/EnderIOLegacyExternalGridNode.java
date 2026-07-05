package github.formlessdragon.appcompat.bridge.enderioae;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridVisitor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IReadOnlyCollection;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class EnderIOLegacyExternalGridNode implements IGridNode {

    private final ae2.api.networking.IGridNode node;

    public EnderIOLegacyExternalGridNode(final ae2.api.networking.IGridNode node) {
        this.node = node;
    }

    @Override
    public IGrid getGrid() {
        throw new UnsupportedOperationException("EnderIO external grid node adapter does not expose old AE grid caches");
    }

    @Override
    public void beginVisit(final IGridVisitor visitor) {
        this.node.beginVisit(visited -> visitor.visitNode(new EnderIOLegacyExternalGridNode(visited)));
    }

    @Override
    public IGridHost getMachine() {
        final Object owner = this.node.getOwner();
        if (owner instanceof IGridHost host) {
            return host;
        }
        throw new UnsupportedOperationException("New AE node owner is not an old AE grid host: " + owner.getClass().getName());
    }

    @Override
    public World getWorld() {
        return this.node.getLevel();
    }

    @Override
    public EnumSet<AEPartLocation> getConnectedSides() {
        final EnumSet<AEPartLocation> sides = EnumSet.noneOf(AEPartLocation.class);
        for (final net.minecraft.util.EnumFacing side : this.node.getConnectedSides()) {
            sides.add(AEPartLocation.fromFacing(side));
        }
        return sides;
    }

    @Override
    public IReadOnlyCollection<IGridConnection> getConnections() {
        final List<IGridConnection> connections = new ArrayList<>(this.node.getConnections().size());
        for (final ae2.api.networking.IGridConnection connection : this.node.getConnections()) {
            connections.add(new EnderIOLegacyGridConnection(connection, this.node, this));
        }
        return new EnderIOReadOnlyCollection<>(connections);
    }

    @Override
    public boolean meetsChannelRequirements() {
        return this.node.meetsChannelRequirements();
    }

    @Override
    public boolean hasFlag(final GridFlags flag) {
        return this.node.hasFlag(toNewFlag(flag));
    }

    @Override
    public int getPlayerID() {
        return this.node.getOwningPlayerId();
    }

    @Override
    public boolean isActive() {
        return this.node.isActive();
    }

    @Override
    public boolean isPowered() {
        return this.node.isPowered();
    }

    @Override
    public IGridBlock getGridBlock() {
        throw new UnsupportedOperationException("EnderIO external grid node adapter has no old AE grid block");
    }

    ae2.api.networking.IGridNode unwrap() {
        return this.node;
    }

    private static ae2.api.networking.GridFlags toNewFlag(final GridFlags flag) {
        return switch (flag) {
            case REQUIRE_CHANNEL, REQUIRE_CHANNEL_POWER -> ae2.api.networking.GridFlags.REQUIRE_CHANNEL;
            case COMPRESSED_CHANNEL -> ae2.api.networking.GridFlags.COMPRESSED_CHANNEL;
            case CANNOT_CARRY -> ae2.api.networking.GridFlags.CANNOT_CARRY;
            case DENSE_CAPACITY -> ae2.api.networking.GridFlags.DENSE_CAPACITY;
            case MULTIBLOCK -> ae2.api.networking.GridFlags.MULTIBLOCK;
        };
    }
}
