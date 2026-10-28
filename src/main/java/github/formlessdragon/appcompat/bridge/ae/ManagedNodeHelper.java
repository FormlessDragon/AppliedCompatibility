package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.networking.IManagedGridNode;
import org.jetbrains.annotations.Nullable;

public final class ManagedNodeHelper {

    private ManagedNodeHelper() {
    }

    public static boolean alert(@Nullable final IManagedGridNode managedNode) {
        if (managedNode == null || !managedNode.isActive()) {
            return false;
        }
        final var node = managedNode.getNode();
        final var grid = managedNode.getGrid();
        return node != null && grid != null && grid.getTickManager().alertDevice(node);
    }
}
