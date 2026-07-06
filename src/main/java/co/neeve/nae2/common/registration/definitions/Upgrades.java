package co.neeve.nae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Optional;

public final class Upgrades {

    private static final String UPGRADE = "nae2:upgrade";
    private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
    private final IItemDefinition hyperAcceleration = this.upgrade("hyper_acceleration", 0);
    private final IItemDefinition autoComplete = this.upgrade("auto_complete", 1);
    private final IItemDefinition gregtechCircuit = this.upgrade("gregtech_circuit", 2);

    public Optional<IItemDefinition> getById(final String id) {
        return Optional.ofNullable(this.byId.get(id));
    }

    public Optional<UpgradeType> getById(final int itemDamage) {
        final UpgradeType[] values = UpgradeType.values();
        if (itemDamage < 0 || itemDamage >= values.length) {
            return Optional.empty();
        }
        return Optional.of(values[itemDamage]);
    }

    public IItemDefinition hyperAcceleration() { return this.hyperAcceleration; }
    public IItemDefinition autoComplete() { return this.autoComplete; }
    public IItemDefinition gregtechCircuit() { return this.gregtechCircuit; }

    private IItemDefinition upgrade(final String id, final int meta) {
        final IItemDefinition definition = LegacyAeItemMappings.itemDefinition(UPGRADE, meta);
        this.byId.put(id, definition);
        return definition;
    }

    public enum UpgradeType {
        HYPER_ACCELERATION("hyper_acceleration"),
        AUTO_COMPLETE("auto_complete"),
        GREGTECH_CIRCUIT("gregtech_circuit");

        private final String id;

        UpgradeType(final String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public int getDamageValue() {
            return this.ordinal();
        }
    }
}
