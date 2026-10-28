package github.formlessdragon.appcompat.bridge.gtceu;

import ae2.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;

public final class GtceuActionHostBridge implements ae2.api.networking.security.IActionHost {

    private final IActionHost host;

    public GtceuActionHostBridge(final IActionHost host) {
        this.host = host;
    }

    @Override
    public IGridNode getActionableNode() {
        if (this.host.getActionableNode() instanceof GtceuGridNodeAdapter node) {
            return node.unwrap();
        }
        throw new IllegalStateException("Unsupported GTCEu old AE action host node " + this.host.getActionableNode().getClass().getName());
    }
}
