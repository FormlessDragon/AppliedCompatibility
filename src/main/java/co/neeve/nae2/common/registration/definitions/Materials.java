package co.neeve.nae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Optional;

public final class Materials {

    private static final String MATERIAL = "nae2:material";
    private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
    private final IItemDefinition cellPartVoid = this.material("cell_part_void", 0);
    private final IItemDefinition cellPart256K = this.material("cell_part_256k", 1);
    private final IItemDefinition cellPart1024K = this.material("cell_part_1024k", 2);
    private final IItemDefinition cellPart4096K = this.material("cell_part_4096k", 3);
    private final IItemDefinition cellPart16384K = this.material("cell_part_16384k", 4);
    private final IItemDefinition cellFluidPart256K = this.material("cell_part_fluid_256k", 5);
    private final IItemDefinition cellFluidPart1024K = this.material("cell_part_fluid_1024k", 6);
    private final IItemDefinition cellFluidPart4096K = this.material("cell_part_fluid_4096k", 7);
    private final IItemDefinition cellFluidPart16384K = this.material("cell_part_fluid_16384k", 8);
    private final IItemDefinition cellGasPart256K = this.material("cell_part_gas_256k", 9);
    private final IItemDefinition cellGasPart1024K = this.material("cell_part_gas_1024k", 10);
    private final IItemDefinition cellGasPart4096K = this.material("cell_part_gas_4096k", 11);
    private final IItemDefinition cellGasPart16384K = this.material("cell_part_gas_16384k", 12);

    public Optional<IItemDefinition> getById(final String id) {
        return Optional.ofNullable(this.byId.get(id));
    }

    public Optional<MaterialType> getById(final int itemDamage) {
        final MaterialType[] values = MaterialType.values();
        if (itemDamage < 0 || itemDamage >= values.length) {
            return Optional.empty();
        }
        return Optional.of(values[itemDamage]);
    }

    public IItemDefinition cellPartVoid() { return this.cellPartVoid; }
    public IItemDefinition cellPart256K() { return this.cellPart256K; }
    public IItemDefinition cellPart1024K() { return this.cellPart1024K; }
    public IItemDefinition cellPart4096K() { return this.cellPart4096K; }
    public IItemDefinition cellPart16384K() { return this.cellPart16384K; }
    public IItemDefinition cellFluidPart256K() { return this.cellFluidPart256K; }
    public IItemDefinition cellFluidPart1024K() { return this.cellFluidPart1024K; }
    public IItemDefinition cellFluidPart4096K() { return this.cellFluidPart4096K; }
    public IItemDefinition cellFluidPart16384K() { return this.cellFluidPart16384K; }
    public IItemDefinition cellGasPart256K() { return this.cellGasPart256K; }
    public IItemDefinition cellGasPart1024K() { return this.cellGasPart1024K; }
    public IItemDefinition cellGasPart4096K() { return this.cellGasPart4096K; }
    public IItemDefinition cellGasPart16384K() { return this.cellGasPart16384K; }

    private IItemDefinition material(final String id, final int meta) {
        final IItemDefinition definition = LegacyAeItemMappings.itemDefinition(MATERIAL, meta);
        this.byId.put(id, definition);
        return definition;
    }

    public enum MaterialType {
        CELL_PART_VOID("cell_part_void"),
        CELL_PART_256K("cell_part_256k"),
        CELL_PART_1024K("cell_part_1024k"),
        CELL_PART_4096K("cell_part_4096k"),
        CELL_PART_16384K("cell_part_16384k"),
        CELL_FLUID_PART_256K("cell_part_fluid_256k"),
        CELL_FLUID_PART_1024K("cell_part_fluid_1024k"),
        CELL_FLUID_PART_4096K("cell_part_fluid_4096k"),
        CELL_FLUID_PART_16384K("cell_part_fluid_16384k"),
        CELL_GAS_PART_256K("cell_part_gas_256k"),
        CELL_GAS_PART_1024K("cell_part_gas_1024k"),
        CELL_GAS_PART_4096K("cell_part_gas_4096k"),
        CELL_GAS_PART_16384K("cell_part_gas_16384k");

        private final String id;

        MaterialType(final String id) {
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
