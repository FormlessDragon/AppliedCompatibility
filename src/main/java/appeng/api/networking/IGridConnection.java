package appeng.api.networking;

import appeng.api.util.AEPartLocation;

/**
 * Old AE connection ABI for mods that inspect channel usage and neighboring grid nodes.
 */
public interface IGridConnection {

    /**
     * @param gridNode one side of this connection.
     * @return the opposite side as an old AE node facade.
     */
    IGridNode getOtherSide(IGridNode gridNode);

    /**
     * @param gridNode one side of this connection.
     * @return old AE part location for in-world connections, or INTERNAL when the new AE connection has no direction.
     */
    AEPartLocation getDirection(IGridNode gridNode);

    /**
     * Destroys this connection through the new AE connection object.
     */
    void destroy();

    /**
     * @return first side of the connection.
     */
    IGridNode a();

    /**
     * @return second side of the connection.
     */
    IGridNode b();

    /**
     * @return true when this connection has a world side direction.
     */
    boolean hasDirection();

    /**
     * @return channels used by this connection.
     */
    int getUsedChannels();
}
