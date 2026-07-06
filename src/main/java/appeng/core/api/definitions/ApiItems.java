package appeng.core.api.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IItems;
import appeng.api.util.AEColoredItemDefinition;
import appeng.core.features.ColoredItemDefinition;
import appeng.core.features.ItemDefinition;
import ae2.core.definitions.AEItems;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;

public final class ApiItems implements IItems {

    private final IItemDefinition certusQuartzAxe = item("certus_quartz_axe", AEItems.CERTUS_QUARTZ_AXE);
    private final IItemDefinition certusQuartzHoe = item("certus_quartz_hoe", AEItems.CERTUS_QUARTZ_HOE);
    private final IItemDefinition certusQuartzShovel = item("certus_quartz_spade", AEItems.CERTUS_QUARTZ_SHOVEL);
    private final IItemDefinition certusQuartzPick = item("certus_quartz_pickaxe", AEItems.CERTUS_QUARTZ_PICK);
    private final IItemDefinition certusQuartzSword = item("certus_quartz_sword", AEItems.CERTUS_QUARTZ_SWORD);
    private final IItemDefinition certusQuartzWrench = item("certus_quartz_wrench", AEItems.CERTUS_QUARTZ_WRENCH);
    private final IItemDefinition certusQuartzKnife = item("certus_quartz_cutting_knife", AEItems.CERTUS_QUARTZ_KNIFE);
    private final IItemDefinition netherQuartzAxe = item("nether_quartz_axe", AEItems.NETHER_QUARTZ_AXE);
    private final IItemDefinition netherQuartzHoe = item("nether_quartz_hoe", AEItems.NETHER_QUARTZ_HOE);
    private final IItemDefinition netherQuartzShovel = item("nether_quartz_spade", AEItems.NETHER_QUARTZ_SHOVEL);
    private final IItemDefinition netherQuartzPick = item("nether_quartz_pickaxe", AEItems.NETHER_QUARTZ_PICK);
    private final IItemDefinition netherQuartzSword = item("nether_quartz_sword", AEItems.NETHER_QUARTZ_SWORD);
    private final IItemDefinition netherQuartzWrench = item("nether_quartz_wrench", AEItems.NETHER_QUARTZ_WRENCH);
    private final IItemDefinition netherQuartzKnife = item("nether_quartz_cutting_knife", AEItems.NETHER_QUARTZ_KNIFE);
    private final IItemDefinition entropyManipulator = item("entropy_manipulator", AEItems.ENTROPY_MANIPULATOR);
    private final IItemDefinition wirelessTerminal = item("wireless_terminal", AEItems.WIRELESS_TERMINAL);
    private final IItemDefinition wirelessCraftingTerminal = item("wireless_crafting_terminal", AEItems.WIRELESS_CRAFTING_TERMINAL);
    private final IItemDefinition wirelessPatternTerminal = item("wireless_pattern_terminal", AEItems.WIRELESS_PATTERN_ENCODING_TERMINAL);
    private final IItemDefinition wirelessInterfaceTerminal = item("wireless_interface_terminal", AEItems.WIRELESS_PATTERN_ACCESS_TERMINAL);
    private final IItemDefinition wirelessFluidTerminal = legacy("wireless_fluid_terminal");
    private final IItemDefinition biometricCard = legacy("biometric_card");
    private final IItemDefinition chargedStaff = item("charged_staff", AEItems.CHARGED_STAFF);
    private final IItemDefinition massCannon = item("matter_cannon", AEItems.MATTER_CANNON);
    private final IItemDefinition memoryCard = item("memory_card", AEItems.MEMORY_CARD);
    private final IItemDefinition networkTool = item("network_tool", AEItems.NETWORK_TOOL);
    private final IItemDefinition portableCell = item("portable_cell", AEItems.PORTABLE_ITEM_CELL1K);
    private final IItemDefinition cellCreative = item("creative_storage_cell", AEItems.CREATIVE_CELL);
    private final IItemDefinition viewCell = item("view_cell", AEItems.VIEW_CELL);
    private final IItemDefinition cell1k = item("storage_cell_1k", AEItems.ITEM_CELL_1K);
    private final IItemDefinition cell4k = item("storage_cell_4k", AEItems.ITEM_CELL_4K);
    private final IItemDefinition cell16k = item("storage_cell_16k", AEItems.ITEM_CELL_16K);
    private final IItemDefinition cell64k = item("storage_cell_64k", AEItems.ITEM_CELL_64K);
    private final IItemDefinition fluidCell1k = item("fluid_storage_cell_1k", AEItems.FLUID_CELL_1K);
    private final IItemDefinition fluidCell4k = item("fluid_storage_cell_4k", AEItems.FLUID_CELL_4K);
    private final IItemDefinition fluidCell16k = item("fluid_storage_cell_16k", AEItems.FLUID_CELL_16K);
    private final IItemDefinition fluidCell64k = item("fluid_storage_cell_64k", AEItems.FLUID_CELL_64K);
    private final IItemDefinition spatialCell2 = item("spatial_storage_cell_2_cubed", AEItems.SPATIAL_CELL2);
    private final IItemDefinition spatialCell16 = item("spatial_storage_cell_16_cubed", AEItems.SPATIAL_CELL16);
    private final IItemDefinition spatialCell128 = item("spatial_storage_cell_128_cubed", AEItems.SPATIAL_CELL128);
    private final IItemDefinition facade = item("facade", AEItems.FACADE);
    private final IItemDefinition crystalSeed = legacy("crystal_seed");
    private final IItemDefinition encodedPattern = item("encoded_pattern", AEItems.PROCESSING_PATTERN);
    private final IItemDefinition colorApplicator = item("color_applicator", AEItems.COLOR_APPLICATOR);
    private final AEColoredItemDefinition coloredPaintBall = new ColoredItemDefinition(AEItems.COLORED_PAINT_BALL);
    private final AEColoredItemDefinition coloredLumenPaintBall = new ColoredItemDefinition(AEItems.COLORED_LUMEN_PAINT_BALL);

    @Override
    public IItemDefinition certusQuartzAxe() {
        return this.certusQuartzAxe;
    }

    @Override
    public IItemDefinition certusQuartzHoe() {
        return this.certusQuartzHoe;
    }

    @Override
    public IItemDefinition certusQuartzShovel() {
        return this.certusQuartzShovel;
    }

    @Override
    public IItemDefinition certusQuartzPick() {
        return this.certusQuartzPick;
    }

    @Override
    public IItemDefinition certusQuartzSword() {
        return this.certusQuartzSword;
    }

    @Override
    public IItemDefinition certusQuartzWrench() {
        return this.certusQuartzWrench;
    }

    @Override
    public IItemDefinition certusQuartzKnife() {
        return this.certusQuartzKnife;
    }

    @Override
    public IItemDefinition netherQuartzAxe() {
        return this.netherQuartzAxe;
    }

    @Override
    public IItemDefinition netherQuartzHoe() {
        return this.netherQuartzHoe;
    }

    @Override
    public IItemDefinition netherQuartzShovel() {
        return this.netherQuartzShovel;
    }

    @Override
    public IItemDefinition netherQuartzPick() {
        return this.netherQuartzPick;
    }

    @Override
    public IItemDefinition netherQuartzSword() {
        return this.netherQuartzSword;
    }

    @Override
    public IItemDefinition netherQuartzWrench() {
        return this.netherQuartzWrench;
    }

    @Override
    public IItemDefinition netherQuartzKnife() {
        return this.netherQuartzKnife;
    }

    @Override
    public IItemDefinition entropyManipulator() {
        return this.entropyManipulator;
    }

    @Override
    public IItemDefinition wirelessTerminal() {
        return this.wirelessTerminal;
    }

    @Override
    public IItemDefinition wirelessCraftingTerminal() {
        return this.wirelessCraftingTerminal;
    }

    @Override
    public IItemDefinition wirelessPatternTerminal() {
        return this.wirelessPatternTerminal;
    }

    @Override
    public IItemDefinition wirelessInterfaceTerminal() {
        return this.wirelessInterfaceTerminal;
    }

    @Override
    public IItemDefinition wirelessFluidTerminal() {
        return this.wirelessFluidTerminal;
    }

    @Override
    public IItemDefinition biometricCard() {
        return this.biometricCard;
    }

    @Override
    public IItemDefinition chargedStaff() {
        return this.chargedStaff;
    }

    @Override
    public IItemDefinition massCannon() {
        return this.massCannon;
    }

    @Override
    public IItemDefinition memoryCard() {
        return this.memoryCard;
    }

    @Override
    public IItemDefinition networkTool() {
        return this.networkTool;
    }

    @Override
    public IItemDefinition portableCell() {
        return this.portableCell;
    }

    @Override
    public IItemDefinition cellCreative() {
        return this.cellCreative;
    }

    @Override
    public IItemDefinition viewCell() {
        return this.viewCell;
    }

    @Override
    public IItemDefinition cell1k() {
        return this.cell1k;
    }

    @Override
    public IItemDefinition cell4k() {
        return this.cell4k;
    }

    @Override
    public IItemDefinition cell16k() {
        return this.cell16k;
    }

    @Override
    public IItemDefinition cell64k() {
        return this.cell64k;
    }

    @Override
    public IItemDefinition fluidCell1k() {
        return this.fluidCell1k;
    }

    @Override
    public IItemDefinition fluidCell4k() {
        return this.fluidCell4k;
    }

    @Override
    public IItemDefinition fluidCell16k() {
        return this.fluidCell16k;
    }

    @Override
    public IItemDefinition fluidCell64k() {
        return this.fluidCell64k;
    }

    @Override
    public IItemDefinition spatialCell2() {
        return this.spatialCell2;
    }

    @Override
    public IItemDefinition spatialCell16() {
        return this.spatialCell16;
    }

    @Override
    public IItemDefinition spatialCell128() {
        return this.spatialCell128;
    }

    @Override
    public IItemDefinition facade() {
        return this.facade;
    }

    @Override
    public IItemDefinition crystalSeed() {
        return this.crystalSeed;
    }

    @Override
    public IItemDefinition encodedPattern() {
        return this.encodedPattern;
    }

    @Override
    public IItemDefinition colorApplicator() {
        return this.colorApplicator;
    }

    @Override
    public AEColoredItemDefinition coloredPaintBall() {
        return this.coloredPaintBall;
    }

    @Override
    public AEColoredItemDefinition coloredLumenPaintBall() {
        return this.coloredLumenPaintBall;
    }

    private static IItemDefinition item(final String identifier,
                                        final ae2.core.definitions.ItemDefinition<?> definition) {
        return new ItemDefinition(identifier, definition);
    }

    private static IItemDefinition legacy(final String identifier) {
        return LegacyAeItemMappings.itemDefinition(identifier, identifier, 0);
    }
}
