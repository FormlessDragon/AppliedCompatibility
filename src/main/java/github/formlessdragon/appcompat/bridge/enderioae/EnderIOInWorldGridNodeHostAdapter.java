package github.formlessdragon.appcompat.bridge.enderioae;

import ae2.api.networking.IGridNode;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.util.AECableType;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.conduit.me.conduit.IMEConduit;
import crazypants.enderio.conduit.me.conduit.MEConduit;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import net.minecraft.util.EnumFacing;

public final class EnderIOInWorldGridNodeHostAdapter implements IInWorldGridNodeHost {

    private final TileConduitBundle bundle;

    public EnderIOInWorldGridNodeHostAdapter(final TileConduitBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public IGridNode getGridNode(final EnumFacing dir) {
        final MEConduit conduit = conduit();
        if (conduit == null || dir != null && conduit.getConnectionMode(dir) == ConnectionMode.DISABLED) {
            return null;
        }
        final appeng.api.networking.IGridNode legacyNode = conduit.getGridNode();
        if (legacyNode == null) {
            return null;
        }
        if (legacyNode instanceof EnderIOLegacyGridNode enderIONode) {
            return enderIONode.newNode();
        }
        throw new IllegalStateException("Unexpected EnderIO ME conduit grid node " + legacyNode.getClass().getName());
    }

    @Override
    public AECableType getCableConnectionType(final EnumFacing dir) {
        final MEConduit conduit = conduit();
        if (conduit == null || dir != null && conduit.getConnectionMode(dir) == ConnectionMode.DISABLED) {
            return AECableType.NONE;
        }
        return conduit.isDense() ? AECableType.DENSE_SMART : AECableType.SMART;
    }

    private MEConduit conduit() {
        final IMEConduit conduit = this.bundle.getConduit(IMEConduit.class);
        if (conduit == null) {
            return null;
        }
        if (conduit instanceof MEConduit meConduit) {
            return meConduit;
        }
        throw new IllegalStateException("Unexpected EnderIO ME conduit implementation " + conduit.getClass().getName());
    }
}
