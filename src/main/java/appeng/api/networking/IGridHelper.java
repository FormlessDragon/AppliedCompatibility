package appeng.api.networking;

import appeng.api.exceptions.FailedConnectionException;

public interface IGridHelper {

    IGridNode createGridNode(IGridBlock gridBlock);

    default IGridConnection createGridConnection(final IGridNode first, final IGridNode second)
        throws FailedConnectionException {
        throw new FailedConnectionException("Legacy AE grid connections are unavailable for "
            + (first == null ? "null" : first.getClass().getName()) + " and "
            + (second == null ? "null" : second.getClass().getName()));
    }
}
