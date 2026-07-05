package appeng.api.networking;

import appeng.api.util.AEPartLocation;
import appeng.api.util.IReadOnlyCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.EnumSet;

public interface IGridNode {

    IGrid getGrid();

    default void beginVisit(final IGridVisitor visitor) {
        throw new UnsupportedOperationException("Legacy AE grid node visit is not implemented");
    }

    default IGridHost getMachine() {
        throw new UnsupportedOperationException("Legacy AE grid node machine access is not implemented");
    }

    default World getWorld() {
        throw new UnsupportedOperationException("Legacy AE grid node world access is not implemented");
    }

    default EnumSet<AEPartLocation> getConnectedSides() {
        throw new UnsupportedOperationException("Legacy AE grid node connected sides are not implemented");
    }

    default IReadOnlyCollection<IGridConnection> getConnections() {
        throw new UnsupportedOperationException("Legacy AE grid node connections are not implemented");
    }

    default boolean meetsChannelRequirements() {
        throw new UnsupportedOperationException("Legacy AE grid node channel state is not implemented");
    }

    default boolean hasFlag(final GridFlags flag) {
        throw new UnsupportedOperationException("Legacy AE grid node flags are not implemented");
    }

    default int getPlayerID() {
        throw new UnsupportedOperationException("Legacy AE grid node owner is not implemented");
    }

    default void loadFromNBT(final String name, final NBTTagCompound data) {
        throw new UnsupportedOperationException("Legacy AE grid node NBT load is not implemented");
    }

    default void saveToNBT(final String name, final NBTTagCompound data) {
        throw new UnsupportedOperationException("Legacy AE grid node NBT save is not implemented");
    }

    default void setPlayerID(final int playerId) {
        throw new UnsupportedOperationException("Legacy AE grid node player ownership is not implemented");
    }

    default void updateState() {
        throw new UnsupportedOperationException("Legacy AE grid node state update is not implemented");
    }

    default void destroy() {
        throw new UnsupportedOperationException("Legacy AE grid node destroy is not implemented");
    }

    default boolean isActive() {
        throw new UnsupportedOperationException("Legacy AE grid node active state is not implemented");
    }

    default boolean isPowered() {
        throw new UnsupportedOperationException("Legacy AE grid node powered state is not implemented");
    }

    default IGridBlock getGridBlock() {
        throw new UnsupportedOperationException("Legacy AE grid block access is not implemented");
    }
}
