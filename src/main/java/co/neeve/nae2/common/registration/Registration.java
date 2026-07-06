package co.neeve.nae2.common.registration;

import co.neeve.nae2.common.registration.definitions.Blocks;
import co.neeve.nae2.common.registration.definitions.Items;
import co.neeve.nae2.common.registration.definitions.Materials;
import co.neeve.nae2.common.registration.definitions.Parts;
import co.neeve.nae2.common.registration.definitions.Upgrades;

public final class Registration {

    private final Blocks blocks = new Blocks();
    private final Items items = new Items();
    private final Materials materials = new Materials();
    private final Parts parts = new Parts();
    private final Upgrades upgrades = new Upgrades();

    public Blocks blocks() {
        return this.blocks;
    }

    public Items items() {
        return this.items;
    }

    public Materials materials() {
        return this.materials;
    }

    public Parts parts() {
        return this.parts;
    }

    public Upgrades upgrades() {
        return this.upgrades;
    }
}
