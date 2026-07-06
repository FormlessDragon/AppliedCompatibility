package extracells.integration;

import net.minecraftforge.fml.common.Loader;

public final class Integration {

    private Integration() {
    }

    public enum Mods {
        MEKANISMGAS("mekeng"),
        OPENCOMPUTERS("opencomputers");

        private final String modid;

        Mods(final String modid) {
            this.modid = modid;
        }

        public boolean isEnabled() {
            return Loader.isModLoaded(this.modid);
        }
    }
}
