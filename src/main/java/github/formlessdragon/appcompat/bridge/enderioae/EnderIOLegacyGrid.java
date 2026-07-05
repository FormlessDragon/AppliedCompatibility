package github.formlessdragon.appcompat.bridge.enderioae;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.events.MENetworkEvent;

public final class EnderIOLegacyGrid implements IGrid {

    private final EnderIOLegacyGridNode node;

    public EnderIOLegacyGrid(final EnderIOLegacyGridNode node) {
        this.node = node;
    }

    @Override
    public MENetworkEvent postEvent(final MENetworkEvent ev) {
        this.node.access().appcompat$gridChanged();
        return ev;
    }

    @Override
    public IGridCache getCache(final Class<? extends IGridCache> iface) {
        throw new IllegalArgumentException("EnderIO ME conduit does not expose old AE grid cache " + iface.getName());
    }
}
