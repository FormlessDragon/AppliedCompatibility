package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.util.AEColor;
import ae2.core.definitions.AEBlocks;
import ae2.core.definitions.AEItems;
import ae2.core.definitions.AEParts;
import ae2.core.definitions.BlockDefinition;
import ae2.core.definitions.ColoredItemDefinition;
import ae2.core.definitions.ItemDefinition;
import com.google.common.collect.ImmutableMap;
import com.gripe.megacells.definition.MEGABlocks;
import com.gripe.megacells.definition.MEGAItems;
import github.formlessdragon.appcompat.AppliedCompatibility;
import github.formlessdragon.appcompat.common.item.ItemLegacyAeError;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.ITileDefinition;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.ramidzkh.mekae2.item.AMItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;

public final class LegacyAeItemMappings {

    private static final String AE = AppliedCompatibility.LEGACY_AE_MOD_ID + ':';
    private static final String AEADDITIONS = "ae2additions:";
    private static final String EXTRACELLS = "extracells:";
    private static final String EXTRACPUS = "extracpus:";
    private static final String MEGACELLS = "megacells";
    private static final String MEKENG = "mekeng";
    private static final String NAE2 = "nae2:";
    public static final SupplierItemStack ERROR = ItemLegacyAeError::create;
    private static final Map<String, Int2ObjectMap<SupplierItemStack>> MAPPINGS;

    static {
        final var m = new ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>>();
        reg(m);
        MAPPINGS = m.build();
    }

    private LegacyAeItemMappings() {
    }

    public static SupplierItemStack get(final String itemName, final int meta) {
        final Int2ObjectMap<SupplierItemStack> byMeta = MAPPINGS.get(normalizeItemName(itemName));
        if (byMeta == null) {
            return null;
        }
        return byMeta.get(meta);
    }

    public static ItemStack mappedStackOrNull(final String itemName, final int meta, final int stackSize) {
        if (stackSize <= 0) {
            throw new IllegalArgumentException("Stack size must be positive");
        }
        final SupplierItemStack supplier = get(itemName, meta);
        if (supplier == null) {
            return null;
        }
        final ItemStack stack = supplier.get(itemName, meta);
        if (stack.isEmpty()) {
            throw new IllegalStateException("Legacy AE item mapping returned an empty stack for "
                + normalizeItemName(itemName) + " @" + meta);
        }
        stack.setCount(stackSize);
        return stack;
    }

    public static ItemStack mappedSpecStackOrNull(final String itemSpec, final int stackSize) {
        if (itemSpec == null) {
            return null;
        }
        String spec = itemSpec.trim();
        if (spec.isEmpty()) {
            return null;
        }
        if (spec.startsWith("item:")) {
            spec = spec.substring("item:".length()).trim();
        } else if (spec.startsWith("block:") || spec.startsWith("oredict:")) {
            return null;
        }
        if (spec.isEmpty() || spec.charAt(0) == '+' || spec.charAt(0) == '-') {
            return null;
        }

        final int firstColon = spec.indexOf(':');
        if (firstColon < 0) {
            return null;
        }
        final int metaColon = spec.indexOf(':', firstColon + 1);
        if (metaColon < 0) {
            return mappedStackOrNull(spec, 0, stackSize);
        }

        final String itemName = spec.substring(0, metaColon);
        final String metaString = spec.substring(metaColon + 1).trim();
        final int meta;
        if ("*".equals(metaString)) {
            meta = 32767;
        } else {
            try {
                meta = Integer.parseInt(metaString);
            } catch (final NumberFormatException ignored) {
                return null;
            }
        }
        return mappedStackOrNull(itemName, meta, stackSize);
    }

    public static ItemStack stack(final String itemName, final int meta, final int stackSize) {
        if (stackSize <= 0) {
            throw new IllegalArgumentException("Stack size must be positive");
        }
        final SupplierItemStack supplier = get(itemName, meta);
        if (supplier == null) {
            final ItemStack error = ERROR.get(itemName, meta);
            error.setCount(stackSize);
            return error;
        }
        final ItemStack stack = supplier.get(itemName, meta);
        if (stack.isEmpty()) {
            throw new IllegalStateException("Legacy AE item mapping returned an empty stack for "
                + normalizeItemName(itemName) + " @" + meta);
        }
        stack.setCount(stackSize);
        return stack;
    }

    public static IItemDefinition itemDefinition(final String itemName) {
        return itemDefinition(itemName, 0);
    }

    public static IItemDefinition itemDefinition(final String itemName, final int meta) {
        return new LegacyAeItemDefinition(normalizeItemName(itemName), itemName, meta);
    }

    public static IItemDefinition itemDefinition(final String identifier, final String itemName, final int meta) {
        return new LegacyAeItemDefinition(identifier, itemName, meta);
    }

    public static IBlockDefinition blockDefinition(final String itemName) {
        return blockDefinition(itemName, 0);
    }

    public static IBlockDefinition blockDefinition(final String itemName, final int meta) {
        return new LegacyAeBlockDefinition(normalizeItemName(itemName), itemName, meta);
    }

    public static IBlockDefinition blockDefinition(final String identifier, final String itemName, final int meta) {
        return new LegacyAeBlockDefinition(identifier, itemName, meta);
    }

    public static ITileDefinition tileDefinition(final String itemName) {
        return tileDefinition(itemName, 0);
    }

    public static ITileDefinition tileDefinition(final String itemName, final int meta) {
        return new LegacyAeTileDefinition(normalizeItemName(itemName), itemName, meta);
    }

    public static ITileDefinition tileDefinition(final String identifier, final String itemName, final int meta) {
        return new LegacyAeTileDefinition(identifier, itemName, meta);
    }

    private static void reg(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regMaterials(b);
        regParts(b);
        regItems(b);
        regBlocks(b);
        regBetterP2P(b);
        regMekanismEnergistics(b);
        regNae2(b);
        regExtraCells(b);
        regAeAdditions(b);
        regExtraCpus(b);
    }

    private static void regMaterials(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        final Int2ObjectMap<SupplierItemStack> m = new Int2ObjectOpenHashMap<>();
        m.put(0, item(AEItems.CERTUS_QUARTZ_CRYSTAL));
        m.put(1, item(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED));
        m.put(2, item(AEItems.CERTUS_QUARTZ_DUST));
        m.put(3, item(AEItems.QUARTZ_BLEND));
        m.put(4, ERROR);
        m.put(5, item(AEItems.SILICON));
        m.put(6, item(AEItems.MATTER_BALL));
        m.put(7, item(AEItems.FLUIX_CRYSTAL));
        m.put(8, item(AEItems.FLUIX_DUST));
        m.put(9, item(AEItems.FLUIX_PEARL));
        m.put(10, ERROR);
        m.put(11, ERROR);
        m.put(12, ERROR);
        m.put(13, item(AEItems.CALCULATION_PROCESSOR_PRESS));
        m.put(14, item(AEItems.ENGINEERING_PROCESSOR_PRESS));
        m.put(15, item(AEItems.LOGIC_PROCESSOR_PRESS));
        m.put(16, item(AEItems.CALCULATION_PROCESSOR_PRINT));
        m.put(17, item(AEItems.ENGINEERING_PROCESSOR_PRINT));
        m.put(18, item(AEItems.LOGIC_PROCESSOR_PRINT));
        m.put(19, item(AEItems.SILICON_PRESS));
        m.put(20, item(AEItems.SILICON_PRINT));
        m.put(21, item(AEItems.NAME_PRESS));
        m.put(22, item(AEItems.LOGIC_PROCESSOR));
        m.put(23, item(AEItems.CALCULATION_PROCESSOR));
        m.put(24, item(AEItems.ENGINEERING_PROCESSOR));
        m.put(25, item(AEItems.BASIC_CARD));
        m.put(26, item(AEItems.REDSTONE_CARD));
        m.put(27, item(AEItems.CAPACITY_CARD));
        m.put(28, item(AEItems.ADVANCED_CARD));
        m.put(29, item(AEItems.FUZZY_CARD));
        m.put(30, item(AEItems.SPEED_CARD));
        m.put(31, item(AEItems.INVERTER_CARD));
        m.put(32, item(AEItems.SPATIAL_2_CELL_COMPONENT));
        m.put(33, item(AEItems.SPATIAL_16_CELL_COMPONENT));
        m.put(34, item(AEItems.SPATIAL_128_CELL_COMPONENT));
        m.put(35, item(AEItems.CELL_COMPONENT_1K));
        m.put(36, item(AEItems.CELL_COMPONENT_4K));
        m.put(37, item(AEItems.CELL_COMPONENT_16K));
        m.put(38, item(AEItems.CELL_COMPONENT_64K));
        m.put(39, item(AEItems.ITEM_CELL_HOUSING));
        m.put(40, ERROR);
        m.put(41, item(AEItems.WIRELESS_RECEIVER));
        m.put(42, item(AEItems.WIRELESS_BOOSTER));
        m.put(43, item(AEItems.FORMATION_CORE));
        m.put(44, item(AEItems.ANNIHILATION_CORE));
        m.put(45, item(AEItems.SKY_DUST));
        m.put(46, item(AEItems.ENDER_DUST));
        m.put(47, item(AEItems.SINGULARITY));
        m.put(48, item(AEItems.QUANTUM_ENTANGLED_SINGULARITY));
        m.put(49, ERROR);
        m.put(51, ERROR);
        m.put(52, item(AEItems.BLANK_PATTERN));
        m.put(53, item(AEItems.CRAFTING_CARD));
        m.put(54, item(AEItems.CELL_COMPONENT_1K));
        m.put(55, item(AEItems.CELL_COMPONENT_4K));
        m.put(56, item(AEItems.CELL_COMPONENT_16K));
        m.put(57, item(AEItems.CELL_COMPONENT_64K));
        m.put(58, item(AEItems.PATTERN_EXPANSION_CARD));
        m.put(59, item(AEItems.QUANTUM_BRIDGE_CARD));
        m.put(60, item(AEItems.MAGNET_CARD));
        m.put(61, item(AEItems.STICKY_CARD));
        b.put(AE + "material", readonly(m));
    }

    private static void regParts(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        final Int2ObjectMap<SupplierItemStack> m = new Int2ObjectOpenHashMap<>();
        regColored(m, 0, AEParts.GLASS_CABLE);
        regColored(m, 20, AEParts.COVERED_CABLE);
        regColored(m, 40, AEParts.SMART_CABLE);
        regColored(m, 60, AEParts.SMART_DENSE_CABLE);
        regColored(m, 500, AEParts.COVERED_DENSE_CABLE);
        m.put(80, part(AEParts.TOGGLE_BUS));
        m.put(100, part(AEParts.INVERTED_TOGGLE_BUS));
        m.put(120, part(AEParts.CABLE_ANCHOR));
        m.put(140, part(AEParts.QUARTZ_FIBER));
        m.put(160, part(AEParts.MONITOR));
        m.put(180, part(AEParts.SEMI_DARK_MONITOR));
        m.put(200, part(AEParts.DARK_MONITOR));
        m.put(220, part(AEParts.STORAGE_BUS));
        m.put(221, part(AEParts.STORAGE_BUS));
        m.put(222, part(AEParts.OD_STORAGE_BUS));
        m.put(240, part(AEParts.IMPORT_BUS));
        m.put(241, part(AEParts.IMPORT_BUS));
        m.put(260, part(AEParts.EXPORT_BUS));
        m.put(261, part(AEParts.EXPORT_BUS));
        m.put(280, part(AEParts.LEVEL_EMITTER));
        m.put(281, part(AEParts.LEVEL_EMITTER));
        m.put(300, part(AEParts.ANNIHILATION_PLANE));
        m.put(301, ERROR);
        m.put(302, part(AEParts.ANNIHILATION_PLANE));
        m.put(320, part(AEParts.FORMATION_PLANE));
        m.put(321, part(AEParts.FORMATION_PLANE));
        m.put(340, part(AEParts.PATTERN_ENCODING_TERMINAL));
        m.put(341, part(AEParts.PATTERN_ENCODING_TERMINAL));
        m.put(360, part(AEParts.CRAFTING_TERMINAL));
        m.put(380, part(AEParts.TERMINAL));
        m.put(400, part(AEParts.STORAGE_MONITOR));
        m.put(420, part(AEParts.CONVERSION_MONITOR));
        m.put(440, part(AEParts.INTERFACE));
        m.put(441, part(AEParts.INTERFACE));
        m.put(460, part(AEParts.ME_P2P_TUNNEL));
        m.put(461, part(AEParts.REDSTONE_P2P_TUNNEL));
        m.put(462, part(AEParts.ITEM_P2P_TUNNEL));
        m.put(463, part(AEParts.FLUID_P2P_TUNNEL));
        m.put(465, part(AEParts.IC2_P2P_TUNNEL));
        m.put(467, part(AEParts.LIGHT_P2P_TUNNEL));
        m.put(469, part(AEParts.FE_P2P_TUNNEL));
        m.put(470, ERROR);
        m.put(480, part(AEParts.PATTERN_ACCESS_TERMINAL));
        m.put(520, part(AEParts.TERMINAL));
        m.put(521, part(AEParts.PATTERN_ACCESS_TERMINAL));
        m.put(522, part(AEParts.PATTERN_ACCESS_TERMINAL));
        b.put(AE + "part", readonly(m));
    }

    private static void regItems(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, "certus_quartz_axe", item(AEItems.CERTUS_QUARTZ_AXE));
        regSingle(b, "certus_quartz_hoe", item(AEItems.CERTUS_QUARTZ_HOE));
        regSingle(b, "certus_quartz_spade", item(AEItems.CERTUS_QUARTZ_SHOVEL));
        regSingle(b, "certus_quartz_pickaxe", item(AEItems.CERTUS_QUARTZ_PICK));
        regSingle(b, "certus_quartz_sword", item(AEItems.CERTUS_QUARTZ_SWORD));
        regSingle(b, "certus_quartz_wrench", item(AEItems.CERTUS_QUARTZ_WRENCH));
        regSingle(b, "certus_quartz_cutting_knife", item(AEItems.CERTUS_QUARTZ_KNIFE));
        regSingle(b, "nether_quartz_axe", item(AEItems.NETHER_QUARTZ_AXE));
        regSingle(b, "nether_quartz_hoe", item(AEItems.NETHER_QUARTZ_HOE));
        regSingle(b, "nether_quartz_spade", item(AEItems.NETHER_QUARTZ_SHOVEL));
        regSingle(b, "nether_quartz_pickaxe", item(AEItems.NETHER_QUARTZ_PICK));
        regSingle(b, "nether_quartz_sword", item(AEItems.NETHER_QUARTZ_SWORD));
        regSingle(b, "nether_quartz_wrench", item(AEItems.NETHER_QUARTZ_WRENCH));
        regSingle(b, "nether_quartz_cutting_knife", item(AEItems.NETHER_QUARTZ_KNIFE));
        regSingle(b, "entropy_manipulator", item(AEItems.ENTROPY_MANIPULATOR));
        regSingle(b, "wireless_terminal", item(AEItems.WIRELESS_TERMINAL));
        regSingle(b, "wireless_crafting_terminal", item(AEItems.WIRELESS_CRAFTING_TERMINAL));
        regSingle(b, "wireless_pattern_terminal", item(AEItems.WIRELESS_PATTERN_ENCODING_TERMINAL));
        regSingle(b, "wireless_interface_terminal", item(AEItems.WIRELESS_PATTERN_ACCESS_TERMINAL));
        regSingle(b, "wireless_fluid_terminal", ERROR);
        regSingle(b, "biometric_card", ERROR);
        regSingle(b, "charged_staff", item(AEItems.CHARGED_STAFF));
        regSingle(b, "matter_cannon", item(AEItems.MATTER_CANNON));
        regSingle(b, "memory_card", item(AEItems.MEMORY_CARD));
        regSingle(b, "network_tool", item(AEItems.NETWORK_TOOL));
        regSingle(b, "portable_cell", item(AEItems.PORTABLE_ITEM_CELL1K));
        regSingle(b, "creative_storage_cell", item(AEItems.CREATIVE_CELL));
        regSingle(b, "view_cell", item(AEItems.VIEW_CELL));
        regSingle(b, "storage_cell_1k", item(AEItems.ITEM_CELL_1K));
        regSingle(b, "storage_cell_4k", item(AEItems.ITEM_CELL_4K));
        regSingle(b, "storage_cell_16k", item(AEItems.ITEM_CELL_16K));
        regSingle(b, "storage_cell_64k", item(AEItems.ITEM_CELL_64K));
        regSingle(b, "fluid_storage_cell_1k", item(AEItems.FLUID_CELL_1K));
        regSingle(b, "fluid_storage_cell_4k", item(AEItems.FLUID_CELL_4K));
        regSingle(b, "fluid_storage_cell_16k", item(AEItems.FLUID_CELL_16K));
        regSingle(b, "fluid_storage_cell_64k", item(AEItems.FLUID_CELL_64K));
        regSingle(b, "spatial_storage_cell_2_cubed", item(AEItems.SPATIAL_CELL2));
        regSingle(b, "spatial_storage_cell_16_cubed", item(AEItems.SPATIAL_CELL16));
        regSingle(b, "spatial_storage_cell_128_cubed", item(AEItems.SPATIAL_CELL128));
        regSingle(b, "facade", item(AEItems.FACADE));
        regSingle(b, "crystal_seed", ERROR);
        regSingle(b, "encoded_pattern", item(AEItems.PROCESSING_PATTERN));
        regSingle(b, "color_applicator", item(AEItems.COLOR_APPLICATOR));
        regPaintBalls(b);
    }

    private static void regBlocks(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, "quartz_ore", ERROR);
        regSingle(b, "charged_quartz_ore", ERROR);
        regSingle(b, "matrix_frame", block(AEBlocks.MATRIX_FRAME));
        regSingle(b, "quartz_block", block(AEBlocks.QUARTZ_BLOCK));
        regSingle(b, "quartz_pillar", block(AEBlocks.QUARTZ_PILLAR));
        regSingle(b, "chiseled_quartz_block", block(AEBlocks.CHISELED_QUARTZ_BLOCK));
        regSingle(b, "quartz_glass", block(AEBlocks.QUARTZ_GLASS));
        regSingle(b, "quartz_vibrant_glass", block(AEBlocks.QUARTZ_VIBRANT_GLASS));
        regSingle(b, "quartz_fixture", block(AEBlocks.QUARTZ_FIXTURE));
        regSingle(b, "fluix_block", block(AEBlocks.FLUIX_BLOCK));
        regSingle(b, "sky_stone_block", block(AEBlocks.SKY_STONE_BLOCK));
        regSingle(b, "smooth_sky_stone_block", block(AEBlocks.SMOOTH_SKY_STONE_BLOCK));
        regSingle(b, "sky_stone_brick", block(AEBlocks.SKY_STONE_BRICK));
        regSingle(b, "sky_stone_small_brick", block(AEBlocks.SKY_STONE_SMALL_BRICK));
        regSingle(b, "sky_stone_chest", block(AEBlocks.SKY_STONE_CHEST));
        regSingle(b, "smooth_sky_stone_chest", block(AEBlocks.SMOOTH_SKY_STONE_CHEST));
        regSingle(b, "sky_compass", ERROR);
        regSingle(b, "sky_stone_stairs", block(AEBlocks.SKY_STONE_STAIRS));
        regSingle(b, "smooth_sky_stone_stairs", block(AEBlocks.SMOOTH_SKY_STONE_STAIRS));
        regSingle(b, "sky_stone_brick_stairs", block(AEBlocks.SKY_STONE_BRICK_STAIRS));
        regSingle(b, "sky_stone_small_brick_stairs", block(AEBlocks.SKY_STONE_SMALL_BRICK_STAIRS));
        regSingle(b, "fluix_stairs", block(AEBlocks.FLUIX_STAIRS));
        regSingle(b, "quartz_stairs", block(AEBlocks.QUARTZ_STAIRS));
        regSingle(b, "chiseled_quartz_stairs", block(AEBlocks.CHISELED_QUARTZ_STAIRS));
        regSingle(b, "quartz_pillar_stairs", block(AEBlocks.QUARTZ_PILLAR_STAIRS));
        regSingle(b, "sky_stone_slab", block(AEBlocks.SKY_STONE_SLAB));
        regSingle(b, "smooth_sky_stone_slab", block(AEBlocks.SMOOTH_SKY_STONE_SLAB));
        regSingle(b, "sky_stone_brick_slab", block(AEBlocks.SKY_STONE_BRICK_SLAB));
        regSingle(b, "sky_stone_small_brick_slab", block(AEBlocks.SKY_STONE_SMALL_BRICK_SLAB));
        regSingle(b, "fluix_slab", block(AEBlocks.FLUIX_SLAB));
        regSingle(b, "quartz_slab", block(AEBlocks.QUARTZ_SLAB));
        regSingle(b, "chiseled_quartz_slab", block(AEBlocks.CHISELED_QUARTZ_SLAB));
        regSingle(b, "quartz_pillar_slab", block(AEBlocks.QUARTZ_PILLAR_SLAB));
        regSingle(b, "grindstone", ERROR);
        regSingle(b, "crank", block(AEBlocks.CRANK));
        regSingle(b, "inscriber", block(AEBlocks.INSCRIBER));
        regSingle(b, "wireless_access_point", block(AEBlocks.WIRELESS_ACCESS_POINT));
        regSingle(b, "charger", block(AEBlocks.CHARGER));
        regSingle(b, "tiny_tnt", block(AEBlocks.TINY_TNT));
        regSingle(b, "security_station", ERROR);
        regSingle(b, "quantum_ring", block(AEBlocks.QUANTUM_RING));
        regSingle(b, "quantum_link", block(AEBlocks.QUANTUM_LINK));
        regSingle(b, "spatial_pylon", block(AEBlocks.SPATIAL_PYLON));
        regSingle(b, "spatial_io_port", block(AEBlocks.SPATIAL_IO_PORT));
        regSingle(b, "controller", block(AEBlocks.CONTROLLER));
        regSingle(b, "drive", block(AEBlocks.DRIVE));
        regSingle(b, "chest", block(AEBlocks.ME_CHEST));
        regSingle(b, "interface", block(AEBlocks.INTERFACE));
        regSingle(b, "fluid_interface", block(AEBlocks.INTERFACE));
        regSingle(b, "cell_workbench", block(AEBlocks.CELL_WORKBENCH));
        regSingle(b, "io_port", block(AEBlocks.IO_PORT));
        regSingle(b, "condenser", block(AEBlocks.CONDENSER));
        regSingle(b, "energy_acceptor", block(AEBlocks.ENERGY_ACCEPTOR));
        regSingle(b, "vibration_chamber", block(AEBlocks.VIBRATION_CHAMBER));
        regSingle(b, "quartz_growth_accelerator", block(AEBlocks.GROWTH_ACCELERATOR));
        regSingle(b, "energy_cell", block(AEBlocks.ENERGY_CELL));
        regSingle(b, "dense_energy_cell", block(AEBlocks.DENSE_ENERGY_CELL));
        regSingle(b, "creative_energy_cell", block(AEBlocks.CREATIVE_ENERGY_CELL));
        regSingle(b, "crafting_unit", block(AEBlocks.CRAFTING_UNIT));
        regSingle(b, "crafting_accelerator", block(AEBlocks.CRAFTING_ACCELERATOR));
        regSingle(b, "crafting_storage_1k", block(AEBlocks.CRAFTING_STORAGE_1K));
        regSingle(b, "crafting_storage_4k", block(AEBlocks.CRAFTING_STORAGE_4K));
        regSingle(b, "crafting_storage_16k", block(AEBlocks.CRAFTING_STORAGE_16K));
        regSingle(b, "crafting_storage_64k", block(AEBlocks.CRAFTING_STORAGE_64K));
        regSingle(b, "crafting_monitor", block(AEBlocks.CRAFTING_MONITOR));
        regSingle(b, "molecular_assembler", block(AEBlocks.MOLECULAR_ASSEMBLER));
        regSingle(b, "light_detector", block(AEBlocks.LIGHT_DETECTOR));
        regSingle(b, "paint", block(AEBlocks.PAINT));
    }

    private static void regPaintBalls(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        final Int2ObjectMap<SupplierItemStack> m = new Int2ObjectOpenHashMap<>();
        final AEColor[] colors = AEColor.values();
        for (int i = 0; i < colors.length; i++) {
            final AEColor color = colors[i];
            if (color == AEColor.TRANSPARENT) {
                m.put(i, ERROR);
                m.put(i + 20, ERROR);
                continue;
            }
            m.put(i, colored(AEItems.COLORED_PAINT_BALL, color));
            m.put(i + 20, colored(AEItems.COLORED_LUMEN_PAINT_BALL, color));
        }
        b.put(AE + "paint_ball", readonly(m));
    }

    private static void regNae2(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        final Int2ObjectMap<SupplierItemStack> material = new Int2ObjectOpenHashMap<>();
        material.put(0, ERROR);
        material.put(1, item(AEItems.CELL_COMPONENT_256K));
        material.put(2, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        material.put(3, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        material.put(4, aeAdditionsOr("cell_component_16384", megaComponent("16m")));
        material.put(5, item(AEItems.CELL_COMPONENT_256K));
        material.put(6, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        material.put(7, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        material.put(8, aeAdditionsOr("cell_component_16384", megaComponent("16m")));
        material.put(9, item(AEItems.CELL_COMPONENT_256K));
        material.put(10, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        material.put(11, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        material.put(12, aeAdditionsOr("cell_component_16384", megaComponent("16m")));
        b.put(NAE2 + "material", readonly(material));

        regSingle(b, NAE2, "storage_cell_256k", item(AEItems.ITEM_CELL_256K));
        regSingle(b, NAE2, "storage_cell_1024k", aeAdditionsOr("item_storage_cell_1024", megaItemCell("1m")));
        regSingle(b, NAE2, "storage_cell_4096k", aeAdditionsOr("item_storage_cell_4096", megaItemCell("4m")));
        regSingle(b, NAE2, "storage_cell_16384k", aeAdditionsOr("item_storage_cell_16384", megaItemCell("16m")));
        regSingle(b, NAE2, "storage_cell_fluid_256k", item(AEItems.FLUID_CELL_256K));
        regSingle(b, NAE2, "storage_cell_fluid_1024k", aeAdditionsOr("fluid_storage_cell_1024", megaFluidCell("1m")));
        regSingle(b, NAE2, "storage_cell_fluid_4096k", aeAdditionsOr("fluid_storage_cell_4096", megaFluidCell("4m")));
        regSingle(b, NAE2, "storage_cell_fluid_16384k", aeAdditionsOr("fluid_storage_cell_16384", megaFluidCell("16m")));
        regSingle(b, NAE2, "storage_cell_gas_256k", mekengGasCell("256k"));
        regSingle(b, NAE2, "storage_cell_gas_1024k", ERROR);
        regSingle(b, NAE2, "storage_cell_gas_4096k", ERROR);
        regSingle(b, NAE2, "storage_cell_gas_16384k", ERROR);
        regSingle(b, NAE2, "storage_cell_void", ERROR);
        regSingle(b, NAE2, "fluid_storage_cell_void", ERROR);
        regSingle(b, NAE2, "gas_storage_cell_void", ERROR);
        regSingle(b, NAE2, "virtual_pattern", ERROR);
        regSingle(b, NAE2, "pattern_multiplier", item(AEItems.PATTERN_MODIFIER));

        final Int2ObjectMap<SupplierItemStack> parts = new Int2ObjectOpenHashMap<>();
        parts.put(0, part(AEParts.BEAM_FORMER));
        parts.put(1, ERROR);
        parts.put(2, ERROR);
        b.put(NAE2 + "part", readonly(parts));

        final Int2ObjectMap<SupplierItemStack> upgrades = new Int2ObjectOpenHashMap<>();
        upgrades.put(0, item(AEItems.SPEED_CARD));
        upgrades.put(1, ERROR);
        upgrades.put(2, ERROR);
        b.put(NAE2 + "upgrade", readonly(upgrades));

        regSingle(b, NAE2, "reconstruction_chamber", ERROR);
        regSingle(b, NAE2, "storage_crafting_256k", block(AEBlocks.CRAFTING_STORAGE_256K));
        regSingle(b, NAE2, "storage_crafting_1024k", aeAdditionsOr("1024k_crafting_storage", megaCraftingStorage("1m")));
        regSingle(b, NAE2, "storage_crafting_4096k", aeAdditionsOr("4096k_crafting_storage", megaCraftingStorage("4m")));
        regSingle(b, NAE2, "storage_crafting_16384k", aeAdditionsOr("16384k_crafting_storage", megaCraftingStorage("16m")));
        regSingle(b, NAE2, "coprocessor_4x", block(AEBlocks.CRAFTING_ACCELERATOR_4X));
        regSingle(b, NAE2, "coprocessor_16x", megaCraftingAccelerator());
        regSingle(b, NAE2, "coprocessor_64x", ERROR);
        regSingle(b, NAE2, "exposer", ERROR);
    }

    private static void regBetterP2P(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, "betterp2p:", "advanced_memory_card", item(AEItems.ADVANCED_MEMORY_CARD));
    }

    private static void regMekanismEnergistics(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, "mekeng:", "dummy_gas", ERROR);
        regSingle(b, "mekeng:", "gas_core_1k", item(AEItems.CELL_COMPONENT_1K));
        regSingle(b, "mekeng:", "gas_core_4k", item(AEItems.CELL_COMPONENT_4K));
        regSingle(b, "mekeng:", "gas_core_16k", item(AEItems.CELL_COMPONENT_16K));
        regSingle(b, "mekeng:", "gas_core_64k", item(AEItems.CELL_COMPONENT_64K));
        regSingle(b, "mekeng:", "gas_cell_1k", mekengGasCell("1k"));
        regSingle(b, "mekeng:", "gas_cell_4k", mekengGasCell("4k"));
        regSingle(b, "mekeng:", "gas_cell_16k", mekengGasCell("16k"));
        regSingle(b, "mekeng:", "gas_cell_64k", mekengGasCell("64k"));
        regSingle(b, "mekeng:", "portable_gas_cell", mekengPortableGasCell());
        regSingle(b, "mekeng:", "gas_terminal", ERROR);
        regSingle(b, "mekeng:", "gas_import_bus", ERROR);
        regSingle(b, "mekeng:", "gas_export_bus", ERROR);
        regSingle(b, "mekeng:", "gas_interface_part", ERROR);
        regSingle(b, "mekeng:", "gas_storage_bus", ERROR);
        regSingle(b, "mekeng:", "gas_level_emitter", ERROR);
        regSingle(b, "mekeng:", "gas_interface_terminal", ERROR);
        regSingle(b, "mekeng:", "wireless_gas_terminal", ERROR);
        regSingle(b, "mekeng:", "gas_p2p", registry("mekeng:gas_p2p_tunnel"));
        regSingle(b, "mekeng:", "gas_storage_monitor", ERROR);
        regSingle(b, "mekeng:", "gas_conversion_monitor", ERROR);
        regSingle(b, "mekeng:", "gas_interface", ERROR);
    }

    private static void regExtraCells(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        final Int2ObjectMap<SupplierItemStack> parts = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < 20; i++) {
            parts.put(i, ERROR);
        }
        b.put(EXTRACELLS + "part.base", readonly(parts));

        final Int2ObjectMap<SupplierItemStack> component = new Int2ObjectOpenHashMap<>();
        component.put(0, item(AEItems.CELL_COMPONENT_256K));
        component.put(1, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        component.put(2, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        component.put(3, aeAdditionsOr("cell_component_16384", megaComponent("16m")));
        component.put(4, item(AEItems.CELL_COMPONENT_1K));
        component.put(5, item(AEItems.CELL_COMPONENT_4K));
        component.put(6, item(AEItems.CELL_COMPONENT_16K));
        component.put(7, item(AEItems.CELL_COMPONENT_64K));
        component.put(8, item(AEItems.CELL_COMPONENT_256K));
        component.put(9, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        component.put(10, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        component.put(11, item(AEItems.CELL_COMPONENT_1K));
        component.put(12, item(AEItems.CELL_COMPONENT_4K));
        component.put(13, item(AEItems.CELL_COMPONENT_16K));
        component.put(14, item(AEItems.CELL_COMPONENT_64K));
        component.put(15, item(AEItems.CELL_COMPONENT_256K));
        component.put(16, aeAdditionsOr("cell_component_1024", megaComponent("1m")));
        component.put(17, aeAdditionsOr("cell_component_4096", megaComponent("4m")));
        b.put(EXTRACELLS + "storage.component", readonly(component));

        final Int2ObjectMap<SupplierItemStack> itemCells = new Int2ObjectOpenHashMap<>();
        itemCells.put(0, item(AEItems.ITEM_CELL_256K));
        itemCells.put(1, aeAdditionsOr("item_storage_cell_1024", megaItemCell("1m")));
        itemCells.put(2, aeAdditionsOr("item_storage_cell_4096", megaItemCell("4m")));
        itemCells.put(3, aeAdditionsOr("item_storage_cell_16384", megaItemCell("16m")));
        itemCells.put(4, ERROR);
        b.put(EXTRACELLS + "storage.physical", readonly(itemCells));

        final Int2ObjectMap<SupplierItemStack> fluidCells = new Int2ObjectOpenHashMap<>();
        fluidCells.put(0, item(AEItems.FLUID_CELL_1K));
        fluidCells.put(1, item(AEItems.FLUID_CELL_4K));
        fluidCells.put(2, item(AEItems.FLUID_CELL_16K));
        fluidCells.put(3, item(AEItems.FLUID_CELL_64K));
        fluidCells.put(4, item(AEItems.FLUID_CELL_256K));
        fluidCells.put(5, aeAdditionsOr("fluid_storage_cell_1024", megaFluidCell("1m")));
        fluidCells.put(6, aeAdditionsOr("fluid_storage_cell_4096", megaFluidCell("4m")));
        b.put(EXTRACELLS + "storage.fluid", readonly(fluidCells));

        final Int2ObjectMap<SupplierItemStack> gasCells = new Int2ObjectOpenHashMap<>();
        gasCells.put(0, mekengGasCell("1k"));
        gasCells.put(1, mekengGasCell("4k"));
        gasCells.put(2, mekengGasCell("16k"));
        gasCells.put(3, mekengGasCell("64k"));
        gasCells.put(4, mekengGasCell("256k"));
        gasCells.put(5, ERROR);
        gasCells.put(6, ERROR);
        b.put(EXTRACELLS + "storage.gas", readonly(gasCells));

        regSingle(b, EXTRACELLS, "storage.casing", item(AEItems.ITEM_CELL_HOUSING));
        regSingle(b, EXTRACELLS, "pattern.fluid", ERROR);
        regSingle(b, EXTRACELLS, "terminal.fluid.wireless", ERROR);
        regSingle(b, EXTRACELLS, "fluid.item", ERROR);
        regSingle(b, EXTRACELLS, "storage.fluid.portable", item(AEItems.PORTABLE_FLUID_CELL1K));
        regSingle(b, EXTRACELLS, "storage.gas.portable", mekengPortableGasCell());
        regSingle(b, EXTRACELLS, "pattern.crafting", ERROR);
        regSingle(b, EXTRACELLS, "terminal.gas.wireless", ERROR);
        regSingle(b, EXTRACELLS, "terminal.universal.wireless", ERROR);
        regSingle(b, EXTRACELLS, "oc.upgrade", ERROR);
        regSingle(b, EXTRACELLS, "gas.item", ERROR);
        regSingle(b, EXTRACELLS, "certustank", ERROR);
        regSingle(b, EXTRACELLS, "walrus", ERROR);
        regSingle(b, EXTRACELLS, "fluidcrafter", ERROR);
        regSingle(b, EXTRACELLS, "ecbaseblock", ERROR);
        regSingle(b, EXTRACELLS, "fluidfiller", ERROR);
        regSingle(b, EXTRACELLS, "hardmedrive", ERROR);
        regSingle(b, EXTRACELLS, "vibrantchamberfluid", ERROR);
    }

    private static void regAeAdditions(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, AEADDITIONS, "cell_component_1024", aeAdditions("cell_component_1024"));
        regSingle(b, AEADDITIONS, "cell_component_4096", aeAdditions("cell_component_4096"));
        regSingle(b, AEADDITIONS, "cell_component_16384", aeAdditions("cell_component_16384"));
        regSingle(b, AEADDITIONS, "cell_component_65536", aeAdditions("cell_component_65536"));
        regSingle(b, AEADDITIONS, "item_storage_cell_1024", aeAdditions("item_storage_cell_1024"));
        regSingle(b, AEADDITIONS, "item_storage_cell_4096", aeAdditions("item_storage_cell_4096"));
        regSingle(b, AEADDITIONS, "item_storage_cell_16384", aeAdditions("item_storage_cell_16384"));
        regSingle(b, AEADDITIONS, "item_storage_cell_65536", aeAdditions("item_storage_cell_65536"));
        regSingle(b, AEADDITIONS, "fluid_storage_cell_1024", aeAdditions("fluid_storage_cell_1024"));
        regSingle(b, AEADDITIONS, "fluid_storage_cell_4096", aeAdditions("fluid_storage_cell_4096"));
        regSingle(b, AEADDITIONS, "fluid_storage_cell_16384", aeAdditions("fluid_storage_cell_16384"));
        regSingle(b, AEADDITIONS, "chemical_storage_cell_1024", aeAdditions("chemical_storage_cell_1024"));
        regSingle(b, AEADDITIONS, "chemical_storage_cell_4096", aeAdditions("chemical_storage_cell_4096"));
        regSingle(b, AEADDITIONS, "chemical_storage_cell_16384", aeAdditions("chemical_storage_cell_16384"));
        regSingle(b, AEADDITIONS, "1024k_crafting_storage", aeAdditions("1024k_crafting_storage"));
        regSingle(b, AEADDITIONS, "4096k_crafting_storage", aeAdditions("4096k_crafting_storage"));
        regSingle(b, AEADDITIONS, "16384k_crafting_storage", aeAdditions("16384k_crafting_storage"));
        regSingle(b, AEADDITIONS, "65536k_crafting_storage", aeAdditions("65536k_crafting_storage"));
        regSingle(b, AEADDITIONS, "me_wireless_transceiver", aeAdditions("me_wireless_transceiver"));
        regSingle(b, AEADDITIONS, "disk_fluid_housing", aeAdditions("disk_fluid_housing"));
        regSingle(b, AEADDITIONS, "disk_chemical_housing", aeAdditions("disk_chemical_housing"));
        regSingle(b, AEADDITIONS, "disk_item_1024k", aeAdditions("disk_item_1024k"));
        regSingle(b, AEADDITIONS, "disk_item_4096k", aeAdditions("disk_item_4096k"));
        regSingle(b, AEADDITIONS, "disk_item_16384k", aeAdditions("disk_item_16384k"));
        regSingle(b, AEADDITIONS, "disk_item_65536k", aeAdditions("disk_item_65536k"));
        regSingle(b, AEADDITIONS, "disk_fluid_1k", aeAdditions("disk_fluid_1k"));
        regSingle(b, AEADDITIONS, "disk_fluid_4k", aeAdditions("disk_fluid_4k"));
        regSingle(b, AEADDITIONS, "disk_fluid_16k", aeAdditions("disk_fluid_16k"));
        regSingle(b, AEADDITIONS, "disk_fluid_64k", aeAdditions("disk_fluid_64k"));
        regSingle(b, AEADDITIONS, "disk_fluid_256k", aeAdditions("disk_fluid_256k"));
        regSingle(b, AEADDITIONS, "disk_fluid_1024k", aeAdditions("disk_fluid_1024k"));
        regSingle(b, AEADDITIONS, "disk_fluid_4096k", aeAdditions("disk_fluid_4096k"));
        regSingle(b, AEADDITIONS, "disk_fluid_16384k", aeAdditions("disk_fluid_16384k"));
        regSingle(b, AEADDITIONS, "disk_fluid_65536k", aeAdditions("disk_fluid_65536k"));
        regSingle(b, AEADDITIONS, "disk_chemical_1k", aeAdditions("disk_chemical_1k"));
        regSingle(b, AEADDITIONS, "disk_chemical_4k", aeAdditions("disk_chemical_4k"));
        regSingle(b, AEADDITIONS, "disk_chemical_16k", aeAdditions("disk_chemical_16k"));
        regSingle(b, AEADDITIONS, "disk_chemical_64k", aeAdditions("disk_chemical_64k"));
        regSingle(b, AEADDITIONS, "disk_chemical_256k", aeAdditions("disk_chemical_256k"));
        regSingle(b, AEADDITIONS, "disk_chemical_1024k", aeAdditions("disk_chemical_1024k"));
        regSingle(b, AEADDITIONS, "disk_chemical_4096k", aeAdditions("disk_chemical_4096k"));
        regSingle(b, AEADDITIONS, "disk_chemical_16384k", aeAdditions("disk_chemical_16384k"));
        regSingle(b, AEADDITIONS, "disk_chemical_65536k", aeAdditions("disk_chemical_65536k"));
        regSingle(b, AEADDITIONS, "super_cell_housing", aeAdditions("super_cell_housing"));
        regSingle(b, AEADDITIONS, "super_cell_component_1k", aeAdditions("super_cell_component_1k"));
        regSingle(b, AEADDITIONS, "super_cell_component_4k", aeAdditions("super_cell_component_4k"));
        regSingle(b, AEADDITIONS, "super_cell_component_16k", aeAdditions("super_cell_component_16k"));
        regSingle(b, AEADDITIONS, "super_cell_component_64k", aeAdditions("super_cell_component_64k"));
        regSingle(b, AEADDITIONS, "super_cell_component_256k", aeAdditions("super_cell_component_256k"));
        regSingle(b, AEADDITIONS, "super_cell_component_1024k", aeAdditions("super_cell_component_1024k"));
        regSingle(b, AEADDITIONS, "super_cell_component_4096k", aeAdditions("super_cell_component_4096k"));
        regSingle(b, AEADDITIONS, "super_cell_component_16m", aeAdditions("super_cell_component_16m"));
        regSingle(b, AEADDITIONS, "super_cell_component_65m", aeAdditions("super_cell_component_65m"));
        regSingle(b, AEADDITIONS, "super_cell_1k", aeAdditions("super_cell_1k"));
        regSingle(b, AEADDITIONS, "super_cell_4k", aeAdditions("super_cell_4k"));
        regSingle(b, AEADDITIONS, "super_cell_16k", aeAdditions("super_cell_16k"));
        regSingle(b, AEADDITIONS, "super_cell_64k", aeAdditions("super_cell_64k"));
        regSingle(b, AEADDITIONS, "super_cell_256k", aeAdditions("super_cell_256k"));
        regSingle(b, AEADDITIONS, "super_cell_1024k", aeAdditions("super_cell_1024k"));
        regSingle(b, AEADDITIONS, "super_cell_4096k", aeAdditions("super_cell_4096k"));
        regSingle(b, AEADDITIONS, "super_cell_16m", aeAdditions("super_cell_16m"));
        regSingle(b, AEADDITIONS, "super_cell_65m", aeAdditions("super_cell_65m"));
    }

    private static void regExtraCpus(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b) {
        regSingle(b, EXTRACPUS, "crafting_storage_256k", block(AEBlocks.CRAFTING_STORAGE_256K));
        regSingle(b, EXTRACPUS, "crafting_storage_1024k",
            aeAdditionsOr("1024k_crafting_storage", megaCraftingStorage("1m")));
        regSingle(b, EXTRACPUS, "crafting_storage_4096k",
            aeAdditionsOr("4096k_crafting_storage", megaCraftingStorage("4m")));
        regSingle(b, EXTRACPUS, "crafting_storage_16384k",
            aeAdditionsOr("16384k_crafting_storage", megaCraftingStorage("16m")));
    }

    private static void regColored(final Int2ObjectMap<SupplierItemStack> m, final int baseMeta,
                                   final ColoredItemDefinition<?> definition) {
        final AEColor[] colors = AEColor.values();
        for (int i = 0; i < colors.length; i++) {
            m.put(baseMeta + i, colored(definition, colors[i]));
        }
    }

    private static void regSingle(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b,
                                   final String oldItemName, final SupplierItemStack supplier) {
        regSingle(b, AE, oldItemName, supplier);
    }

    private static void regSingle(final ImmutableMap.Builder<String, Int2ObjectMap<SupplierItemStack>> b,
                                   final String oldModPrefix, final String oldItemName,
                                   final SupplierItemStack supplier) {
        final Int2ObjectMap<SupplierItemStack> m = new Int2ObjectOpenHashMap<>();
        m.put(0, supplier);
        b.put(oldModPrefix + oldItemName, readonly(m));
    }

    private static Int2ObjectMap<SupplierItemStack> readonly(final Int2ObjectMap<SupplierItemStack> m) {
        return Int2ObjectMaps.unmodifiable(m);
    }

    private static SupplierItemStack item(final ItemDefinition<?> definition) {
        return (oldName, oldMeta) -> requireStack(definition.stack(), oldName, oldMeta);
    }

    private static SupplierItemStack part(final ItemDefinition<?> definition) {
        return item(definition);
    }

    private static SupplierItemStack block(final BlockDefinition<?> definition) {
        return (oldName, oldMeta) -> requireStack(definition.stack(), oldName, oldMeta);
    }

    private static SupplierItemStack megaCraftingStorage(final String tier) {
        if (Loader.isModLoaded(MEGACELLS)) {
            return megaCraftingStorageLoaded(tier);
        }
        return registry("megacells:" + tier + "_crafting_storage");
    }

    @Optional.Method(modid = MEGACELLS)
    private static SupplierItemStack megaCraftingStorageLoaded(final String tier) {
        return switch (tier) {
            case "1m" -> block(MEGABlocks.CRAFTING_STORAGE_1M);
            case "4m" -> block(MEGABlocks.CRAFTING_STORAGE_4M);
            case "16m" -> block(MEGABlocks.CRAFTING_STORAGE_16M);
            case "64m" -> block(MEGABlocks.CRAFTING_STORAGE_64M);
            case "256m" -> block(MEGABlocks.CRAFTING_STORAGE_256M);
            default -> throw new IllegalArgumentException("Unknown MEGACells crafting storage tier " + tier);
        };
    }

    private static SupplierItemStack megaCraftingAccelerator() {
        if (Loader.isModLoaded(MEGACELLS)) {
            return megaCraftingAcceleratorLoaded();
        }
        return registry("megacells:mega_crafting_accelerator");
    }

    @Optional.Method(modid = MEGACELLS)
    private static SupplierItemStack megaCraftingAcceleratorLoaded() {
        return block(MEGABlocks.CRAFTING_ACCELERATOR);
    }

    private static SupplierItemStack colored(final ColoredItemDefinition<?> definition, final AEColor color) {
        return (oldName, oldMeta) -> requireStack(definition.stack(color), oldName, oldMeta);
    }

    private static SupplierItemStack registry(final String id) {
        return registryOr(id, ERROR);
    }

    private static SupplierItemStack registryOr(final String id, final SupplierItemStack fallback) {
        return (oldName, oldMeta) -> {
            final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) {
                return fallback.get(oldName, oldMeta);
            }
            return new ItemStack(item);
        };
    }

    private static SupplierItemStack aeAdditions(final String id) {
        return registry(AEADDITIONS + id);
    }

    private static SupplierItemStack aeAdditionsOr(final String id, final SupplierItemStack fallback) {
        return registryOr(AEADDITIONS + id, fallback);
    }

    private static SupplierItemStack megaComponent(final String tier) {
        if (Loader.isModLoaded(MEGACELLS)) {
            return megaComponentLoaded(tier);
        }
        return registry("megacells:cell_component_" + tier);
    }

    @Optional.Method(modid = MEGACELLS)
    private static SupplierItemStack megaComponentLoaded(final String tier) {
        return switch (tier) {
            case "1m" -> item(MEGAItems.CELL_COMPONENT_1M);
            case "4m" -> item(MEGAItems.CELL_COMPONENT_4M);
            case "16m" -> item(MEGAItems.CELL_COMPONENT_16M);
            case "64m" -> item(MEGAItems.CELL_COMPONENT_64M);
            case "256m" -> item(MEGAItems.CELL_COMPONENT_256M);
            default -> throw new IllegalArgumentException("Unknown MEGACells component tier " + tier);
        };
    }

    private static SupplierItemStack megaItemCell(final String tier) {
        if (Loader.isModLoaded(MEGACELLS)) {
            return megaItemCellLoaded(tier);
        }
        return registry("megacells:item_storage_cell_" + tier);
    }

    @Optional.Method(modid = MEGACELLS)
    private static SupplierItemStack megaItemCellLoaded(final String tier) {
        return switch (tier) {
            case "1m" -> item(MEGAItems.ITEM_CELL_1M);
            case "4m" -> item(MEGAItems.ITEM_CELL_4M);
            case "16m" -> item(MEGAItems.ITEM_CELL_16M);
            case "64m" -> item(MEGAItems.ITEM_CELL_64M);
            case "256m" -> item(MEGAItems.ITEM_CELL_256M);
            default -> throw new IllegalArgumentException("Unknown MEGACells item cell tier " + tier);
        };
    }

    private static SupplierItemStack megaFluidCell(final String tier) {
        if (Loader.isModLoaded(MEGACELLS)) {
            return megaFluidCellLoaded(tier);
        }
        return registry("megacells:fluid_storage_cell_" + tier);
    }

    @Optional.Method(modid = MEGACELLS)
    private static SupplierItemStack megaFluidCellLoaded(final String tier) {
        return switch (tier) {
            case "1m" -> item(MEGAItems.FLUID_CELL_1M);
            case "4m" -> item(MEGAItems.FLUID_CELL_4M);
            case "16m" -> item(MEGAItems.FLUID_CELL_16M);
            case "64m" -> item(MEGAItems.FLUID_CELL_64M);
            case "256m" -> item(MEGAItems.FLUID_CELL_256M);
            default -> throw new IllegalArgumentException("Unknown MEGACells fluid cell tier " + tier);
        };
    }

    private static SupplierItemStack mekengGasCell(final String tier) {
        if (Loader.isModLoaded(MEKENG)) {
            return mekengGasCellLoaded(tier);
        }
        return registry("mekeng:gas_storage_cell_" + tier);
    }

    @Optional.Method(modid = MEKENG)
    private static SupplierItemStack mekengGasCellLoaded(final String tier) {
        return switch (tier) {
            case "1k" -> item(AMItems.GAS_CELL_1K);
            case "4k" -> item(AMItems.GAS_CELL_4K);
            case "16k" -> item(AMItems.GAS_CELL_16K);
            case "64k" -> item(AMItems.GAS_CELL_64K);
            case "256k" -> item(AMItems.GAS_CELL_256K);
            default -> throw new IllegalArgumentException("Unknown Applied Mekanistics gas cell tier " + tier);
        };
    }

    private static SupplierItemStack mekengPortableGasCell() {
        if (Loader.isModLoaded(MEKENG)) {
            return mekengPortableGasCellLoaded();
        }
        return registry("mekeng:portable_gas_cell_1k");
    }

    @Optional.Method(modid = MEKENG)
    private static SupplierItemStack mekengPortableGasCellLoaded() {
        return item(AMItems.PORTABLE_GAS_CELL_1K);
    }

    private static ItemStack requireStack(final ItemStack stack, final String oldName, final int oldMeta) {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Legacy AE item mapping produced an empty stack for "
                + normalizeItemName(oldName) + " @" + oldMeta);
        }
        return stack;
    }

    private static String normalizeItemName(final String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Legacy AE item name is required");
        }
        if (itemName.indexOf(':') >= 0) {
            return itemName;
        }
        return AE + itemName;
    }

    @FunctionalInterface
    public interface SupplierItemStack {

        ItemStack get(String oldName, int oldMeta);
    }
}
