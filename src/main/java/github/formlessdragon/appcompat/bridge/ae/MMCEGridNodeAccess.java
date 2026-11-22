package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.networking.IManagedGridNode;

/**
 * Allows an MMCE extension to supply its legacy proxy's native node before creation.
 * Replacing a different node destroys the old endpoint. The caller must configure
 * the replacement's listeners and services before it is created on the first tick.
 */
public interface MMCEGridNodeAccess {
    void appcompat$setMainNode(IManagedGridNode node);
}
