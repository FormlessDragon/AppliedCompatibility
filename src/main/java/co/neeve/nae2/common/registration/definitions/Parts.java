package co.neeve.nae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Optional;

public final class Parts {

    private static final String PART = "nae2:part";
    private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
    private final IItemDefinition beamFormer = this.part("beam_former", 0);
    private final IItemDefinition p2pTunnelInterface = this.part("p2p_tunnel_interface", 1);
    private final IItemDefinition exposer = this.part("exposer", 2);

    public Optional<IItemDefinition> getById(final String id) {
        return Optional.ofNullable(this.byId.get(id));
    }

    public static Optional<PartType> getById(final int itemDamage) {
        final PartType[] values = PartType.values();
        if (itemDamage < 0 || itemDamage >= values.length) {
            return Optional.empty();
        }
        return Optional.of(values[itemDamage]);
    }

    public IItemDefinition getBeamFormer() { return this.beamFormer; }
    public IItemDefinition p2pTunnelInterface() { return this.p2pTunnelInterface; }
    public IItemDefinition exposer() { return this.exposer; }

    private IItemDefinition part(final String id, final int meta) {
        final IItemDefinition definition = LegacyAeItemMappings.itemDefinition(PART, meta);
        this.byId.put(id, definition);
        return definition;
    }

    public enum PartType {
        BEAM_FORMER("beam_former"),
        P2P_TUNNEL_INTERFACE("p2p_tunnel_interface"),
        EXPOSER("exposer");

        private final String id;

        PartType(final String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public int getBaseDamage() {
            return this.ordinal();
        }
    }
}
