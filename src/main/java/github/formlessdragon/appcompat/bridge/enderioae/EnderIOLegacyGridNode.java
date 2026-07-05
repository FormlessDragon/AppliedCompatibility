package github.formlessdragon.appcompat.bridge.enderioae;

import ae2.api.networking.GridHelper;
import ae2.api.networking.IManagedGridNode;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridVisitor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IReadOnlyCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public final class EnderIOLegacyGridNode implements IGridNode {

    private final IGridBlock gridBlock;
    private final EnderIOGridBlockAccess access;
    private final EnderIOLegacyGrid grid;
    private final IManagedGridNode managedNode;
    private final EnumSet<GridFlags> initialFlags;
    private int playerId = -1;
    private boolean createScheduled;
    private boolean destroyed;

    public EnderIOLegacyGridNode(final IGridBlock gridBlock, final EnderIOGridBlockAccess access) {
        this.gridBlock = gridBlock;
        this.access = access;
        this.grid = new EnderIOLegacyGrid(this);
        this.initialFlags = EnumSet.copyOf(access.appcompat$legacyFlags());
        this.managedNode = GridHelper.createManagedNode(this, EnderIONodeListener.INSTANCE)
                                     .setTagName("appcompat_enderio_me_conduit")
                                     .setInWorldNode(true)
                                     .setIdlePowerUsage(gridBlock.getIdlePowerUsage())
                                     .setVisualRepresentation(access.appcompat$visualItemStack());
        applyInitialGridBlockState();
    }

    public EnderIOGridBlockAccess access() {
        return this.access;
    }

    public ae2.api.networking.IGridNode newNode() {
        return this.managedNode.getNode();
    }

    @Override
    public IGrid getGrid() {
        return this.grid;
    }

    @Override
    public void beginVisit(final IGridVisitor visitor) {
        final ae2.api.networking.IGridNode node = requireNode();
        node.beginVisit(visited -> visitor.visitNode(visited == node ? this : new EnderIOLegacyExternalGridNode(visited)));
    }

    @Override
    public IGridHost getMachine() {
        return this.gridBlock.getMachine();
    }

    @Override
    public World getWorld() {
        return this.access.appcompat$world();
    }

    @Override
    public EnumSet<AEPartLocation> getConnectedSides() {
        final ae2.api.networking.IGridNode node = this.managedNode.getNode();
        final EnumSet<AEPartLocation> sides = EnumSet.noneOf(AEPartLocation.class);
        if (node != null) {
            for (final net.minecraft.util.EnumFacing side : node.getConnectedSides()) {
                sides.add(AEPartLocation.fromFacing(side));
            }
        }
        return sides;
    }

    @Override
    public IReadOnlyCollection<IGridConnection> getConnections() {
        final ae2.api.networking.IGridNode node = this.managedNode.getNode();
        if (node == null) {
            return new EnderIOReadOnlyCollection<>(Collections.emptyList());
        }
        final List<IGridConnection> connections = new ArrayList<>(node.getConnections().size());
        for (final ae2.api.networking.IGridConnection connection : node.getConnections()) {
            connections.add(new EnderIOLegacyGridConnection(connection, node, this));
        }
        return new EnderIOReadOnlyCollection<>(connections);
    }

    @Override
    public boolean meetsChannelRequirements() {
        final ae2.api.networking.IGridNode node = this.managedNode.getNode();
        return node != null && node.meetsChannelRequirements();
    }

    @Override
    public boolean hasFlag(final GridFlags flag) {
        final ae2.api.networking.IGridNode node = this.managedNode.getNode();
        return node != null && node.hasFlag(toNewFlag(flag));
    }

    @Override
    public int getPlayerID() {
        return this.playerId;
    }

    @Override
    public void loadFromNBT(final String name, final NBTTagCompound data) {
        if (data.hasKey(name)) {
            this.managedNode.loadFromNBT(data.getCompoundTag(name));
        }
    }

    @Override
    public void saveToNBT(final String name, final NBTTagCompound data) {
        final NBTTagCompound nodeTag = new NBTTagCompound();
        this.managedNode.saveToNBT(nodeTag);
        data.setTag(name, nodeTag);
    }

    @Override
    public void setPlayerID(final int playerId) {
        this.playerId = playerId;
        this.managedNode.setOwningPlayerId(playerId);
    }

    @Override
    public void updateState() {
        if (this.destroyed) {
            throw new IllegalStateException("EnderIO ME conduit node was updated after destroy");
        }
        applyRuntimeGridBlockState();
        if (this.managedNode.isReady() || this.createScheduled) {
            return;
        }
        final World world = this.access.appcompat$world();
        if (world == null) {
            throw new IllegalStateException("EnderIO ME conduit has no world for node creation");
        }
        final TileEntity tile = world.getTileEntity(this.access.appcompat$pos());
        if (tile == null) {
            throw new IllegalStateException("EnderIO ME conduit tile is missing at " + this.access.appcompat$pos());
        }
        this.createScheduled = true;
        GridHelper.onFirstTick(tile, ignored -> {
            if (!this.destroyed && !this.managedNode.isReady()) {
                this.managedNode.create(world, this.access.appcompat$pos());
            }
            this.createScheduled = false;
        });
    }

    @Override
    public void destroy() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.managedNode.destroy();
        }
    }

    @Override
    public boolean isActive() {
        return this.managedNode.isActive();
    }

    @Override
    public boolean isPowered() {
        return this.managedNode.isPowered();
    }

    @Override
    public IGridBlock getGridBlock() {
        return this.gridBlock;
    }

    private ae2.api.networking.IGridNode requireNode() {
        final ae2.api.networking.IGridNode node = this.managedNode.getNode();
        if (node == null) {
            throw new IllegalStateException("EnderIO ME conduit node is not ready");
        }
        return node;
    }

    private void applyInitialGridBlockState() {
        final ae2.api.networking.GridFlags[] newFlags = new ae2.api.networking.GridFlags[this.initialFlags.size()];
        int index = 0;
        for (final GridFlags flag : this.initialFlags) {
            newFlags[index++] = toNewFlag(flag);
        }
        this.managedNode.setFlags(newFlags);
        applyRuntimeGridBlockState();
    }

    private void applyRuntimeGridBlockState() {
        final EnumSet<GridFlags> currentFlags = this.access.appcompat$legacyFlags();
        if (!currentFlags.equals(this.initialFlags)) {
            throw new IllegalStateException(
                "EnderIO ME conduit changed immutable AE grid flags from " + this.initialFlags + " to "
                    + currentFlags);
        }
        this.managedNode.setExposedOnSides(this.access.appcompat$connectableSides());
        this.managedNode.setIdlePowerUsage(this.gridBlock.getIdlePowerUsage());
        this.managedNode.setVisualRepresentation(this.access.appcompat$visualItemStack());
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
