package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.networking.IManagedGridNode;

/**
 * Optional MMCE addon hook, queried before the bridge creates an in-world endpoint.
 * The addon configures the supplied node and owns its listeners and services;
 * AppliedCompatibility forwards the tile's create, save, load and destroy lifecycle.
 * Return null when the addon cannot supply a node during the base constructor.
 */
public interface MMCEProxyNodeHost {
    IManagedGridNode appcompat$getLegacyProxyNode();
}
