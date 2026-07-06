package co.neeve.nae2.common.registration.definitions;

import appeng.api.definitions.ITileDefinition;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;

public final class Blocks {

    private static final String PREFIX = "nae2:";
    private final ITileDefinition reconstructionChamber = this.tile("reconstruction_chamber");
    private final ITileDefinition storageCrafting256K = this.tile("storage_crafting_256k");
    private final ITileDefinition storageCrafting1024K = this.tile("storage_crafting_1024k");
    private final ITileDefinition storageCrafting4096K = this.tile("storage_crafting_4096k");
    private final ITileDefinition storageCrafting16384K = this.tile("storage_crafting_16384k");
    private final ITileDefinition coprocessor4x = this.tile("coprocessor_4x");
    private final ITileDefinition coprocessor16x = this.tile("coprocessor_16x");
    private final ITileDefinition coprocessor64x = this.tile("coprocessor_64x");
    private final ITileDefinition exposer = this.tile("exposer");

    public ITileDefinition reconstructionChamber() { return this.reconstructionChamber; }
    public ITileDefinition storageCrafting256K() { return this.storageCrafting256K; }
    public ITileDefinition storageCrafting1024K() { return this.storageCrafting1024K; }
    public ITileDefinition storageCrafting4096K() { return this.storageCrafting4096K; }
    public ITileDefinition storageCrafting16384K() { return this.storageCrafting16384K; }
    public ITileDefinition coprocessor4x() { return this.coprocessor4x; }
    public ITileDefinition coprocessor16x() { return this.coprocessor16x; }
    public ITileDefinition coprocessor64x() { return this.coprocessor64x; }
    public ITileDefinition exposer() { return this.exposer; }

    private ITileDefinition tile(final String id) {
        return LegacyAeItemMappings.tileDefinition(PREFIX + id);
    }
}
