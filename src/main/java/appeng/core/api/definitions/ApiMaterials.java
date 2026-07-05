package appeng.core.api.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IMaterials;
import appeng.core.features.ItemDefinition;
import ae2.core.definitions.AEItems;
import net.minecraft.init.Items;

public final class ApiMaterials implements IMaterials {

    private final IItemDefinition cell2SpatialPart = item("material.cell.spatial.2", AEItems.SPATIAL_2_CELL_COMPONENT);
    private final IItemDefinition cell16SpatialPart = item("material.cell.spatial.16", AEItems.SPATIAL_16_CELL_COMPONENT);
    private final IItemDefinition cell128SpatialPart = item("material.cell.spatial.128", AEItems.SPATIAL_128_CELL_COMPONENT);
    private final IItemDefinition silicon = item("material.silicon", AEItems.SILICON);
    private final IItemDefinition skyDust = item("material.dust.sky_stone", AEItems.SKY_DUST);
    private final IItemDefinition calcProcessorPress = item("material.press.processor.calculation", AEItems.CALCULATION_PROCESSOR_PRESS);
    private final IItemDefinition engProcessorPress = item("material.press.processor.engineering", AEItems.ENGINEERING_PROCESSOR_PRESS);
    private final IItemDefinition logicProcessorPress = item("material.press.processor.logic", AEItems.LOGIC_PROCESSOR_PRESS);
    private final IItemDefinition calcProcessorPrint = item("material.print.processor.calculation", AEItems.CALCULATION_PROCESSOR_PRINT);
    private final IItemDefinition engProcessorPrint = item("material.print.processor.engineering", AEItems.ENGINEERING_PROCESSOR_PRINT);
    private final IItemDefinition logicProcessorPrint = item("material.print.processor.logic", AEItems.LOGIC_PROCESSOR_PRINT);
    private final IItemDefinition siliconPress = item("material.press.silicon", AEItems.SILICON_PRESS);
    private final IItemDefinition siliconPrint = item("material.print.silicon", AEItems.SILICON_PRINT);
    private final IItemDefinition namePress = item("material.press.name", AEItems.NAME_PRESS);
    private final IItemDefinition logicProcessor = item("material.processor.logic", AEItems.LOGIC_PROCESSOR);
    private final IItemDefinition calcProcessor = item("material.processor.calculation", AEItems.CALCULATION_PROCESSOR);
    private final IItemDefinition engProcessor = item("material.processor.engineering", AEItems.ENGINEERING_PROCESSOR);
    private final IItemDefinition basicCard = item("material.card.basic", AEItems.BASIC_CARD);
    private final IItemDefinition advCard = item("material.card.advanced", AEItems.ADVANCED_CARD);
    private final IItemDefinition purifiedCertusQuartzCrystal = disabled("material.crystal.quartz.certus.purified");
    private final IItemDefinition purifiedNetherQuartzCrystal = new ItemDefinition("material.crystal.quartz.nether.purified", Items.QUARTZ);
    private final IItemDefinition purifiedFluixCrystal = disabled("material.crystal.fluix.purified");
    private final IItemDefinition cell1kPart = item("material.cell.storage.1k", AEItems.CELL_COMPONENT_1K);
    private final IItemDefinition cell4kPart = item("material.cell.storage.4k", AEItems.CELL_COMPONENT_4K);
    private final IItemDefinition cell16kPart = item("material.cell.storage.16k", AEItems.CELL_COMPONENT_16K);
    private final IItemDefinition cell64kPart = item("material.cell.storage.64k", AEItems.CELL_COMPONENT_64K);
    private final IItemDefinition emptyStorageCell = item("material.cell.storage.empty", AEItems.ITEM_CELL_HOUSING);
    private final IItemDefinition cardRedstone = item("material.card.redstone", AEItems.REDSTONE_CARD);
    private final IItemDefinition cardSpeed = item("material.card.acceleration", AEItems.SPEED_CARD);
    private final IItemDefinition cardCapacity = item("material.card.capacity", AEItems.CAPACITY_CARD);
    private final IItemDefinition cardPatternExpansion = item("material.card.pattern.expansion", AEItems.PATTERN_EXPANSION_CARD);
    private final IItemDefinition cardQuantumLink = item("material.card.quantum.link", AEItems.QUANTUM_BRIDGE_CARD);
    private final IItemDefinition cardMagnet = item("material.card.magnet", AEItems.MAGNET_CARD);
    private final IItemDefinition cardFuzzy = item("material.card.fuzzy", AEItems.FUZZY_CARD);
    private final IItemDefinition cardInverter = item("material.card.inverter", AEItems.INVERTER_CARD);
    private final IItemDefinition cardCrafting = item("material.card.crafting", AEItems.CRAFTING_CARD);
    private final IItemDefinition cardSticky = item("material.card.sticky", AEItems.STICKY_CARD);
    private final IItemDefinition enderDust = item("material.dust.ender", AEItems.ENDER_DUST);
    private final IItemDefinition flour = disabled("material.flour");
    private final IItemDefinition goldDust = disabled("material.dust.gold");
    private final IItemDefinition ironDust = disabled("material.dust.iron");
    private final IItemDefinition fluixDust = item("material.dust.fluix", AEItems.FLUIX_DUST);
    private final IItemDefinition certusQuartzDust = item("material.dust.quartz.certus", AEItems.CERTUS_QUARTZ_DUST);
    private final IItemDefinition netherQuartzDust = item("material.dust.quartz.nether", AEItems.QUARTZ_BLEND);
    private final IItemDefinition matterBall = item("material.ammo.matter_ball", AEItems.MATTER_BALL);
    private final IItemDefinition certusQuartzCrystal = item("material.crystal.quartz.certus", AEItems.CERTUS_QUARTZ_CRYSTAL);
    private final IItemDefinition certusQuartzCrystalCharged = item("material.crystal.quartz.certus.charged", AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED);
    private final IItemDefinition fluixCrystal = item("material.crystal.fluix", AEItems.FLUIX_CRYSTAL);
    private final IItemDefinition fluixPearl = item("material.pearl.fluix", AEItems.FLUIX_PEARL);
    private final IItemDefinition woodenGear = disabled("material.gear.wooden");
    private final IItemDefinition wirelessReceiver = item("material.wireless.receiver", AEItems.WIRELESS_RECEIVER);
    private final IItemDefinition wirelessBooster = item("material.wireless.booster", AEItems.WIRELESS_BOOSTER);
    private final IItemDefinition annihilationCore = item("material.core.annihilation", AEItems.ANNIHILATION_CORE);
    private final IItemDefinition formationCore = item("material.core.formation", AEItems.FORMATION_CORE);
    private final IItemDefinition singularity = item("material.singularity", AEItems.SINGULARITY);
    private final IItemDefinition qESingularity = item("material.singularity.entangled.quantum", AEItems.QUANTUM_ENTANGLED_SINGULARITY);
    private final IItemDefinition blankPattern = item("material.pattern.blank", AEItems.BLANK_PATTERN);
    private final IItemDefinition fluidCell1kPart = item("material.cell.storage.1k", AEItems.FLUID_CELL_HOUSING);
    private final IItemDefinition fluidCell4kPart = item("material.cell.storage.4k", AEItems.FLUID_CELL_HOUSING);
    private final IItemDefinition fluidCell16kPart = item("material.cell.storage.16k", AEItems.FLUID_CELL_HOUSING);
    private final IItemDefinition fluidCell64kPart = item("material.cell.storage.64k", AEItems.FLUID_CELL_HOUSING);

    @Override
    public IItemDefinition cell2SpatialPart() { return this.cell2SpatialPart; }
    @Override
    public IItemDefinition cell16SpatialPart() { return this.cell16SpatialPart; }
    @Override
    public IItemDefinition cell128SpatialPart() { return this.cell128SpatialPart; }
    @Override
    public IItemDefinition silicon() { return this.silicon; }
    @Override
    public IItemDefinition skyDust() { return this.skyDust; }
    @Override
    public IItemDefinition calcProcessorPress() { return this.calcProcessorPress; }
    @Override
    public IItemDefinition engProcessorPress() { return this.engProcessorPress; }
    @Override
    public IItemDefinition logicProcessorPress() { return this.logicProcessorPress; }
    @Override
    public IItemDefinition calcProcessorPrint() { return this.calcProcessorPrint; }
    @Override
    public IItemDefinition engProcessorPrint() { return this.engProcessorPrint; }
    @Override
    public IItemDefinition logicProcessorPrint() { return this.logicProcessorPrint; }
    @Override
    public IItemDefinition siliconPress() { return this.siliconPress; }
    @Override
    public IItemDefinition siliconPrint() { return this.siliconPrint; }
    @Override
    public IItemDefinition namePress() { return this.namePress; }
    @Override
    public IItemDefinition logicProcessor() { return this.logicProcessor; }
    @Override
    public IItemDefinition calcProcessor() { return this.calcProcessor; }
    @Override
    public IItemDefinition engProcessor() { return this.engProcessor; }
    @Override
    public IItemDefinition basicCard() { return this.basicCard; }
    @Override
    public IItemDefinition advCard() { return this.advCard; }
    @Override
    public IItemDefinition purifiedCertusQuartzCrystal() { return this.purifiedCertusQuartzCrystal; }
    @Override
    public IItemDefinition purifiedNetherQuartzCrystal() { return this.purifiedNetherQuartzCrystal; }
    @Override
    public IItemDefinition purifiedFluixCrystal() { return this.purifiedFluixCrystal; }
    @Override
    public IItemDefinition cell1kPart() { return this.cell1kPart; }
    @Override
    public IItemDefinition cell4kPart() { return this.cell4kPart; }
    @Override
    public IItemDefinition cell16kPart() { return this.cell16kPart; }
    @Override
    public IItemDefinition cell64kPart() { return this.cell64kPart; }
    @Override
    public IItemDefinition emptyStorageCell() { return this.emptyStorageCell; }
    @Override
    public IItemDefinition cardRedstone() { return this.cardRedstone; }
    @Override
    public IItemDefinition cardSpeed() { return this.cardSpeed; }
    @Override
    public IItemDefinition cardCapacity() { return this.cardCapacity; }
    @Override
    public IItemDefinition cardPatternExpansion() { return this.cardPatternExpansion; }
    @Override
    public IItemDefinition cardQuantumLink() { return this.cardQuantumLink; }
    @Override
    public IItemDefinition cardMagnet() { return this.cardMagnet; }
    @Override
    public IItemDefinition cardFuzzy() { return this.cardFuzzy; }
    @Override
    public IItemDefinition cardInverter() { return this.cardInverter; }
    @Override
    public IItemDefinition cardCrafting() { return this.cardCrafting; }
    @Override
    public IItemDefinition cardSticky() { return this.cardSticky; }
    @Override
    public IItemDefinition enderDust() { return this.enderDust; }
    @Override
    public IItemDefinition flour() { return this.flour; }
    @Override
    public IItemDefinition goldDust() { return this.goldDust; }
    @Override
    public IItemDefinition ironDust() { return this.ironDust; }
    @Override
    public IItemDefinition fluixDust() { return this.fluixDust; }
    @Override
    public IItemDefinition certusQuartzDust() { return this.certusQuartzDust; }
    @Override
    public IItemDefinition netherQuartzDust() { return this.netherQuartzDust; }
    @Override
    public IItemDefinition matterBall() { return this.matterBall; }
    @Override
    public IItemDefinition certusQuartzCrystal() { return this.certusQuartzCrystal; }
    @Override
    public IItemDefinition certusQuartzCrystalCharged() { return this.certusQuartzCrystalCharged; }
    @Override
    public IItemDefinition fluixCrystal() { return this.fluixCrystal; }
    @Override
    public IItemDefinition fluixPearl() { return this.fluixPearl; }
    @Override
    public IItemDefinition woodenGear() { return this.woodenGear; }
    @Override
    public IItemDefinition wirelessReceiver() { return this.wirelessReceiver; }
    @Override
    public IItemDefinition wirelessBooster() { return this.wirelessBooster; }
    @Override
    public IItemDefinition annihilationCore() { return this.annihilationCore; }
    @Override
    public IItemDefinition formationCore() { return this.formationCore; }
    @Override
    public IItemDefinition singularity() { return this.singularity; }
    @Override
    public IItemDefinition qESingularity() { return this.qESingularity; }
    @Override
    public IItemDefinition blankPattern() { return this.blankPattern; }
    @Override
    public IItemDefinition fluidCell1kPart() { return this.fluidCell1kPart; }
    @Override
    public IItemDefinition fluidCell4kPart() { return this.fluidCell4kPart; }
    @Override
    public IItemDefinition fluidCell16kPart() { return this.fluidCell16kPart; }
    @Override
    public IItemDefinition fluidCell64kPart() { return this.fluidCell64kPart; }

    private static IItemDefinition item(final String identifier,
                                        final ae2.core.definitions.ItemDefinition<?> definition) {
        return new ItemDefinition(identifier, definition);
    }

    private static IItemDefinition disabled(final String identifier) {
        return ItemDefinition.disabled(identifier);
    }
}
