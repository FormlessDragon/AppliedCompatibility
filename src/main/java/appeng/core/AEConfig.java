package appeng.core;

import appeng.core.features.AEFeature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class AEConfig {

    private static final String[] DEFAULT_GRINDER_ORES = {
        "Obsidian", "Ender", "EnderPearl", "Coal", "Iron", "Gold", "Charcoal", "NetherQuartz",
        "CertusQuartz", "Wheat", "Fluix",
        "Copper", "Tin", "Silver", "Lead", "Bronze",
        "Brass", "Platinum", "Nickel", "Invar", "Aluminium", "Electrum", "Osmium", "Zinc"
    };
    private static final AEConfig INSTANCE = new AEConfig();

    private final Set<String> grinderBlackList = new HashSet<>();

    private AEConfig() {
    }

    public static AEConfig instance() {
        return INSTANCE;
    }

    public boolean isFeatureEnabled(final AEFeature feature) {
        return Objects.requireNonNull(feature, "feature").isEnabled();
    }

    public boolean areFeaturesEnabled(final Collection<AEFeature> features) {
        for (final AEFeature feature : Objects.requireNonNull(features, "features")) {
            if (!this.isFeatureEnabled(feature)) {
                return false;
            }
        }
        return true;
    }

    public String[] getGrinderOres() {
        return DEFAULT_GRINDER_ORES.clone();
    }

    public Set<String> getGrinderBlackList() {
        return this.grinderBlackList;
    }

    public double getOreDoublePercentage() {
        return 90.0D;
    }
}
