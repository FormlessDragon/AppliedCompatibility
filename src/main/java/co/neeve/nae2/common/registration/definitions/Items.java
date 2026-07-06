package co.neeve.nae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Optional;

public final class Items {

    private static final String PREFIX = "nae2:";
    private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
    private final IItemDefinition patternMultiTool = this.item("pattern_multiplier");
    private final IItemDefinition storageCellVoid = this.item("storage_cell_void");
    private final IItemDefinition fluidStorageCellVoid = this.item("fluid_storage_cell_void");
    private final IItemDefinition gasStorageCellVoid = this.item("gas_storage_cell_void");
    private final IItemDefinition virtualPattern = this.item("virtual_pattern");
    private final IItemDefinition storageCell256K = this.item("storage_cell_256k");
    private final IItemDefinition storageCell1024K = this.item("storage_cell_1024k");
    private final IItemDefinition storageCell4096K = this.item("storage_cell_4096k");
    private final IItemDefinition storageCell16384K = this.item("storage_cell_16384k");
    private final IItemDefinition storageCellFluid256K = this.item("storage_cell_fluid_256k");
    private final IItemDefinition storageCellFluid1024K = this.item("storage_cell_fluid_1024k");
    private final IItemDefinition storageCellFluid4096K = this.item("storage_cell_fluid_4096k");
    private final IItemDefinition storageCellFluid16384K = this.item("storage_cell_fluid_16384k");
    private final IItemDefinition storageCellGas256K = this.item("storage_cell_gas_256k");
    private final IItemDefinition storageCellGas1024K = this.item("storage_cell_gas_1024k");
    private final IItemDefinition storageCellGas4096K = this.item("storage_cell_gas_4096k");
    private final IItemDefinition storageCellGas16384K = this.item("storage_cell_gas_16384k");

    public Optional<IItemDefinition> getById(final String id) {
        return Optional.ofNullable(this.byId.get(id));
    }

    public IItemDefinition patternMultiTool() { return this.patternMultiTool; }
    public IItemDefinition storageCellVoid() { return this.storageCellVoid; }
    public IItemDefinition fluidStorageCellVoid() { return this.fluidStorageCellVoid; }
    public IItemDefinition gasStorageCellVoid() { return this.gasStorageCellVoid; }
    public IItemDefinition virtualPattern() { return this.virtualPattern; }
    public IItemDefinition storageCell256K() { return this.storageCell256K; }
    public IItemDefinition storageCell1024K() { return this.storageCell1024K; }
    public IItemDefinition storageCell4096K() { return this.storageCell4096K; }
    public IItemDefinition storageCell16384K() { return this.storageCell16384K; }
    public IItemDefinition storageCellFluid256K() { return this.storageCellFluid256K; }
    public IItemDefinition storageCellFluid1024K() { return this.storageCellFluid1024K; }
    public IItemDefinition storageCellFluid4096K() { return this.storageCellFluid4096K; }
    public IItemDefinition storageCellFluid16384K() { return this.storageCellFluid16384K; }
    public IItemDefinition storageCellGas256K() { return this.storageCellGas256K; }
    public IItemDefinition storageCellGas1024K() { return this.storageCellGas1024K; }
    public IItemDefinition storageCellGas4096K() { return this.storageCellGas4096K; }
    public IItemDefinition storageCellGas16384K() { return this.storageCellGas16384K; }

    private IItemDefinition item(final String id) {
        final IItemDefinition definition = LegacyAeItemMappings.itemDefinition(PREFIX + id);
        this.byId.put(id, definition);
        return definition;
    }
}
