package appeng.core.api.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IBlocks;
import appeng.api.definitions.ITileDefinition;
import appeng.core.features.BlockDefinition;
import appeng.core.features.TileDefinition;
import ae2.core.definitions.AEBlockEntities;
import ae2.core.definitions.AEBlocks;

public final class ApiBlocks implements IBlocks {

    private final IBlockDefinition quartzOre = BlockDefinition.disabled("quartz_ore");
    private final IBlockDefinition quartzOreCharged = BlockDefinition.disabled("charged_quartz_ore");
    private final IBlockDefinition matrixFrame = block("matrix_frame", AEBlocks.MATRIX_FRAME);
    private final IBlockDefinition quartzBlock = block("quartz_block", AEBlocks.QUARTZ_BLOCK);
    private final IBlockDefinition quartzPillar = block("quartz_pillar", AEBlocks.QUARTZ_PILLAR);
    private final IBlockDefinition chiseledQuartzBlock = block("chiseled_quartz_block", AEBlocks.CHISELED_QUARTZ_BLOCK);
    private final IBlockDefinition quartzGlass = block("quartz_glass", AEBlocks.QUARTZ_GLASS);
    private final IBlockDefinition quartzVibrantGlass = block("quartz_vibrant_glass", AEBlocks.QUARTZ_VIBRANT_GLASS);
    private final IBlockDefinition quartzFixture = block("quartz_fixture", AEBlocks.QUARTZ_FIXTURE);
    private final IBlockDefinition fluixBlock = block("fluix_block", AEBlocks.FLUIX_BLOCK);
    private final IBlockDefinition skyStoneBlock = block("sky_stone_block", AEBlocks.SKY_STONE_BLOCK);
    private final IBlockDefinition smoothSkyStoneBlock = block("smooth_sky_stone_block", AEBlocks.SMOOTH_SKY_STONE_BLOCK);
    private final IBlockDefinition skyStoneBrick = block("sky_stone_brick", AEBlocks.SKY_STONE_BRICK);
    private final IBlockDefinition skyStoneSmallBrick = block("sky_stone_small_brick", AEBlocks.SKY_STONE_SMALL_BRICK);
    private final IBlockDefinition skyStoneChest = block("sky_stone_chest", AEBlocks.SKY_STONE_CHEST);
    private final IBlockDefinition smoothSkyStoneChest = block("smooth_sky_stone_chest", AEBlocks.SMOOTH_SKY_STONE_CHEST);
    private final IBlockDefinition skyCompass = BlockDefinition.disabled("sky_compass");
    private final IBlockDefinition skyStoneStairs = block("sky_stone_stairs", AEBlocks.SKY_STONE_STAIRS);
    private final IBlockDefinition smoothSkyStoneStairs = block("smooth_sky_stone_stairs", AEBlocks.SMOOTH_SKY_STONE_STAIRS);
    private final IBlockDefinition skyStoneBrickStairs = block("sky_stone_brick_stairs", AEBlocks.SKY_STONE_BRICK_STAIRS);
    private final IBlockDefinition skyStoneSmallBrickStairs = block("sky_stone_small_brick_stairs", AEBlocks.SKY_STONE_SMALL_BRICK_STAIRS);
    private final IBlockDefinition fluixStairs = block("fluix_stairs", AEBlocks.FLUIX_STAIRS);
    private final IBlockDefinition quartzStairs = block("quartz_stairs", AEBlocks.QUARTZ_STAIRS);
    private final IBlockDefinition chiseledQuartzStairs = block("chiseled_quartz_stairs", AEBlocks.CHISELED_QUARTZ_STAIRS);
    private final IBlockDefinition quartzPillarStairs = block("quartz_pillar_stairs", AEBlocks.QUARTZ_PILLAR_STAIRS);
    private final IBlockDefinition skyStoneSlab = block("sky_stone_slab", AEBlocks.SKY_STONE_SLAB);
    private final IBlockDefinition smoothSkyStoneSlab = block("smooth_sky_stone_slab", AEBlocks.SMOOTH_SKY_STONE_SLAB);
    private final IBlockDefinition skyStoneBrickSlab = block("sky_stone_brick_slab", AEBlocks.SKY_STONE_BRICK_SLAB);
    private final IBlockDefinition skyStoneSmallBrickSlab = block("sky_stone_small_brick_slab", AEBlocks.SKY_STONE_SMALL_BRICK_SLAB);
    private final IBlockDefinition fluixSlab = block("fluix_slab", AEBlocks.FLUIX_SLAB);
    private final IBlockDefinition quartzSlab = block("quartz_slab", AEBlocks.QUARTZ_SLAB);
    private final IBlockDefinition chiseledQuartzSlab = block("chiseled_quartz_slab", AEBlocks.CHISELED_QUARTZ_SLAB);
    private final IBlockDefinition quartzPillarSlab = block("quartz_pillar_slab", AEBlocks.QUARTZ_PILLAR_SLAB);
    private final ITileDefinition grindstone = TileDefinition.disabled("grindstone");
    private final ITileDefinition crank = tile("crank", AEBlocks.CRANK, AEBlockEntities.CRANK);
    private final ITileDefinition inscriber = tile("inscriber", AEBlocks.INSCRIBER, AEBlockEntities.INSCRIBER);
    private final ITileDefinition wirelessAccessPoint = tile("wireless_access_point", AEBlocks.WIRELESS_ACCESS_POINT, AEBlockEntities.WIRELESS_ACCESS_POINT);
    private final ITileDefinition charger = tile("charger", AEBlocks.CHARGER, AEBlockEntities.CHARGER);
    private final IBlockDefinition tinyTNT = block("tiny_tnt", AEBlocks.TINY_TNT);
    private final ITileDefinition securityStation = TileDefinition.disabled("security_station");
    private final ITileDefinition quantumRing = tile("quantum_ring", AEBlocks.QUANTUM_RING, AEBlockEntities.QUANTUM_BRIDGE);
    private final ITileDefinition quantumLink = tile("quantum_link", AEBlocks.QUANTUM_LINK, AEBlockEntities.QUANTUM_BRIDGE);
    private final ITileDefinition spatialPylon = tile("spatial_pylon", AEBlocks.SPATIAL_PYLON, AEBlockEntities.SPATIAL_PYLON);
    private final ITileDefinition spatialIOPort = tile("spatial_io_port", AEBlocks.SPATIAL_IO_PORT, AEBlockEntities.SPATIAL_IO_PORT);
    private final ITileDefinition multiPart = tile("cable_bus", AEBlocks.CABLE_BUS, AEBlockEntities.CABLE_BUS);
    private final ITileDefinition controller = tile("controller", AEBlocks.CONTROLLER, AEBlockEntities.CONTROLLER);
    private final ITileDefinition drive = tile("drive", AEBlocks.DRIVE, AEBlockEntities.DRIVE);
    private final ITileDefinition chest = tile("chest", AEBlocks.ME_CHEST, AEBlockEntities.ME_CHEST);
    private final ITileDefinition iface = tile("interface", AEBlocks.INTERFACE, AEBlockEntities.INTERFACE);
    private final ITileDefinition fluidIface = tile("fluid_interface", AEBlocks.INTERFACE, AEBlockEntities.INTERFACE);
    private final ITileDefinition cellWorkbench = tile("cell_workbench", AEBlocks.CELL_WORKBENCH, AEBlockEntities.CELL_WORKBENCH);
    private final ITileDefinition iOPort = tile("io_port", AEBlocks.IO_PORT, AEBlockEntities.IO_PORT);
    private final ITileDefinition condenser = tile("condenser", AEBlocks.CONDENSER, AEBlockEntities.CONDENSER);
    private final ITileDefinition energyAcceptor = tile("energy_acceptor", AEBlocks.ENERGY_ACCEPTOR, AEBlockEntities.ENERGY_ACCEPTOR);
    private final ITileDefinition vibrationChamber = tile("vibration_chamber", AEBlocks.VIBRATION_CHAMBER, AEBlockEntities.VIBRATION_CHAMBER);
    private final ITileDefinition quartzGrowthAccelerator = tile("quartz_growth_accelerator", AEBlocks.GROWTH_ACCELERATOR, AEBlockEntities.GROWTH_ACCELERATOR);
    private final ITileDefinition energyCell = tile("energy_cell", AEBlocks.ENERGY_CELL, AEBlockEntities.ENERGY_CELL);
    private final ITileDefinition energyCellDense = tile("dense_energy_cell", AEBlocks.DENSE_ENERGY_CELL, AEBlockEntities.ENERGY_CELL);
    private final ITileDefinition energyCellCreative = tile("creative_energy_cell", AEBlocks.CREATIVE_ENERGY_CELL, AEBlockEntities.CREATIVE_ENERGY_CELL);
    private final ITileDefinition craftingUnit = tile("crafting_unit", AEBlocks.CRAFTING_UNIT, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingAccelerator = tile("crafting_accelerator", AEBlocks.CRAFTING_ACCELERATOR, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingStorage1k = tile("crafting_storage_1k", AEBlocks.CRAFTING_STORAGE_1K, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingStorage4k = tile("crafting_storage_4k", AEBlocks.CRAFTING_STORAGE_4K, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingStorage16k = tile("crafting_storage_16k", AEBlocks.CRAFTING_STORAGE_16K, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingStorage64k = tile("crafting_storage_64k", AEBlocks.CRAFTING_STORAGE_64K, AEBlockEntities.CRAFTING_UNIT);
    private final ITileDefinition craftingMonitor = tile("crafting_monitor", AEBlocks.CRAFTING_MONITOR, AEBlockEntities.CRAFTING_MONITOR);
    private final ITileDefinition molecularAssembler = tile("molecular_assembler", AEBlocks.MOLECULAR_ASSEMBLER, AEBlockEntities.MOLECULAR_ASSEMBLER);
    private final ITileDefinition lightDetector = tile("light_detector", AEBlocks.LIGHT_DETECTOR, AEBlockEntities.LIGHT_DETECTOR);
    private final ITileDefinition paint = tile("paint", AEBlocks.PAINT, AEBlockEntities.PAINT);

    @Override
    public IBlockDefinition quartzOre() { return this.quartzOre; }
    @Override
    public IBlockDefinition quartzOreCharged() { return this.quartzOreCharged; }
    @Override
    public IBlockDefinition matrixFrame() { return this.matrixFrame; }
    @Override
    public IBlockDefinition quartzBlock() { return this.quartzBlock; }
    @Override
    public IBlockDefinition quartzPillar() { return this.quartzPillar; }
    @Override
    public IBlockDefinition chiseledQuartzBlock() { return this.chiseledQuartzBlock; }
    @Override
    public IBlockDefinition quartzGlass() { return this.quartzGlass; }
    @Override
    public IBlockDefinition quartzVibrantGlass() { return this.quartzVibrantGlass; }
    @Override
    public IBlockDefinition quartzFixture() { return this.quartzFixture; }
    @Override
    public IBlockDefinition fluixBlock() { return this.fluixBlock; }
    @Override
    public IBlockDefinition skyStoneBlock() { return this.skyStoneBlock; }
    @Override
    public IBlockDefinition smoothSkyStoneBlock() { return this.smoothSkyStoneBlock; }
    @Override
    public IBlockDefinition skyStoneBrick() { return this.skyStoneBrick; }
    @Override
    public IBlockDefinition skyStoneSmallBrick() { return this.skyStoneSmallBrick; }
    @Override
    public IBlockDefinition skyStoneChest() { return this.skyStoneChest; }
    @Override
    public IBlockDefinition smoothSkyStoneChest() { return this.smoothSkyStoneChest; }
    @Override
    public IBlockDefinition skyCompass() { return this.skyCompass; }
    @Override
    public IBlockDefinition skyStoneStairs() { return this.skyStoneStairs; }
    @Override
    public IBlockDefinition smoothSkyStoneStairs() { return this.smoothSkyStoneStairs; }
    @Override
    public IBlockDefinition skyStoneBrickStairs() { return this.skyStoneBrickStairs; }
    @Override
    public IBlockDefinition skyStoneSmallBrickStairs() { return this.skyStoneSmallBrickStairs; }
    @Override
    public IBlockDefinition fluixStairs() { return this.fluixStairs; }
    @Override
    public IBlockDefinition quartzStairs() { return this.quartzStairs; }
    @Override
    public IBlockDefinition chiseledQuartzStairs() { return this.chiseledQuartzStairs; }
    @Override
    public IBlockDefinition quartzPillarStairs() { return this.quartzPillarStairs; }
    @Override
    public IBlockDefinition skyStoneSlab() { return this.skyStoneSlab; }
    @Override
    public IBlockDefinition smoothSkyStoneSlab() { return this.smoothSkyStoneSlab; }
    @Override
    public IBlockDefinition skyStoneBrickSlab() { return this.skyStoneBrickSlab; }
    @Override
    public IBlockDefinition skyStoneSmallBrickSlab() { return this.skyStoneSmallBrickSlab; }
    @Override
    public IBlockDefinition fluixSlab() { return this.fluixSlab; }
    @Override
    public IBlockDefinition quartzSlab() { return this.quartzSlab; }
    @Override
    public IBlockDefinition chiseledQuartzSlab() { return this.chiseledQuartzSlab; }
    @Override
    public IBlockDefinition quartzPillarSlab() { return this.quartzPillarSlab; }
    @Override
    public ITileDefinition grindstone() { return this.grindstone; }
    @Override
    public ITileDefinition crank() { return this.crank; }
    @Override
    public ITileDefinition inscriber() { return this.inscriber; }
    @Override
    public ITileDefinition wirelessAccessPoint() { return this.wirelessAccessPoint; }
    @Override
    public ITileDefinition charger() { return this.charger; }
    @Override
    public IBlockDefinition tinyTNT() { return this.tinyTNT; }
    @Override
    public ITileDefinition securityStation() { return this.securityStation; }
    @Override
    public ITileDefinition quantumRing() { return this.quantumRing; }
    @Override
    public ITileDefinition quantumLink() { return this.quantumLink; }
    @Override
    public ITileDefinition spatialPylon() { return this.spatialPylon; }
    @Override
    public ITileDefinition spatialIOPort() { return this.spatialIOPort; }
    @Override
    public ITileDefinition multiPart() { return this.multiPart; }
    @Override
    public ITileDefinition controller() { return this.controller; }
    @Override
    public ITileDefinition drive() { return this.drive; }
    @Override
    public ITileDefinition chest() { return this.chest; }
    @Override
    public ITileDefinition iface() { return this.iface; }
    @Override
    public ITileDefinition fluidIface() { return this.fluidIface; }
    @Override
    public ITileDefinition cellWorkbench() { return this.cellWorkbench; }
    @Override
    public ITileDefinition iOPort() { return this.iOPort; }
    @Override
    public ITileDefinition condenser() { return this.condenser; }
    @Override
    public ITileDefinition energyAcceptor() { return this.energyAcceptor; }
    @Override
    public ITileDefinition vibrationChamber() { return this.vibrationChamber; }
    @Override
    public ITileDefinition quartzGrowthAccelerator() { return this.quartzGrowthAccelerator; }
    @Override
    public ITileDefinition energyCell() { return this.energyCell; }
    @Override
    public ITileDefinition energyCellDense() { return this.energyCellDense; }
    @Override
    public ITileDefinition energyCellCreative() { return this.energyCellCreative; }
    @Override
    public ITileDefinition craftingUnit() { return this.craftingUnit; }
    @Override
    public ITileDefinition craftingAccelerator() { return this.craftingAccelerator; }
    @Override
    public ITileDefinition craftingStorage1k() { return this.craftingStorage1k; }
    @Override
    public ITileDefinition craftingStorage4k() { return this.craftingStorage4k; }
    @Override
    public ITileDefinition craftingStorage16k() { return this.craftingStorage16k; }
    @Override
    public ITileDefinition craftingStorage64k() { return this.craftingStorage64k; }
    @Override
    public ITileDefinition craftingMonitor() { return this.craftingMonitor; }
    @Override
    public ITileDefinition molecularAssembler() { return this.molecularAssembler; }
    @Override
    public ITileDefinition lightDetector() { return this.lightDetector; }
    @Override
    public ITileDefinition paint() { return this.paint; }

    private static IBlockDefinition block(final String identifier,
                                          final ae2.core.definitions.BlockDefinition<?> definition) {
        return new BlockDefinition(identifier, definition);
    }

    private static ITileDefinition tile(final String identifier,
                                        final ae2.core.definitions.BlockDefinition<?> blockDefinition,
                                        final ae2.core.definitions.TileDefinition<?> tileDefinition) {
        return new TileDefinition(identifier, blockDefinition, tileDefinition);
    }
}
