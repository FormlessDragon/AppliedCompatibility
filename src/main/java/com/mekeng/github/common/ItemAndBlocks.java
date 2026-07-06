package com.mekeng.github.common;

import com.mekeng.github.MekEng;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class ItemAndBlocks {

    public static final CreativeTabs TAB = new CreativeTabs(MekEng.MODID) {
        @Nonnull
        @Override
        public ItemStack createIcon() {
            return new ItemStack(GAS_CELL_64k);
        }
    };

    public static final Item DUMMY_GAS = item("dummy_gas");
    public static final Item GAS_CELL_CORE_1k = item("gas_core_1k");
    public static final Item GAS_CELL_CORE_4k = item("gas_core_4k");
    public static final Item GAS_CELL_CORE_16k = item("gas_core_16k");
    public static final Item GAS_CELL_CORE_64k = item("gas_core_64k");
    public static final Item GAS_CELL_1k = item("gas_cell_1k");
    public static final Item GAS_CELL_4k = item("gas_cell_4k");
    public static final Item GAS_CELL_16k = item("gas_cell_16k");
    public static final Item GAS_CELL_64k = item("gas_cell_64k");
    public static final Item PORTABLE_GAS_CELL = item("portable_gas_cell");
    public static final Item GAS_TERMINAL = item("gas_terminal");
    public static final Item GAS_IMPORT_BUS = item("gas_import_bus");
    public static final Item GAS_EXPORT_BUS = item("gas_export_bus");
    public static final Block GAS_INTERFACE = block("gas_interface");
    public static final Item GAS_INTERFACE_PART = item("gas_interface_part");
    public static final Item GAS_STORAGE_BUS = item("gas_storage_bus");
    public static final Item GAS_LEVEL_EMITTER = item("gas_level_emitter");
    public static final Item GAS_INTERFACE_TERMINAL = item("gas_interface_terminal");
    public static final Item WIRELESS_GAS_TERMINAL = item("wireless_gas_terminal");
    public static final Item GAS_P2P = item("gas_p2p");
    public static final Item GAS_STORAGE_MONITOR = item("gas_storage_monitor");
    public static final Item GAS_CONVERSION_MONITOR = item("gas_conversion_monitor");

    private ItemAndBlocks() {
    }

    public static void init(final RegistryHandler regHandler) {
        if (regHandler == null) {
            throw new IllegalArgumentException("MekanismEnergistics registry handler is required");
        }
    }

    private static Item item(final String id) {
        return LegacyAeItemMappings.stack(MekEng.MODID + ':' + id, 0, 1).getItem();
    }

    private static Block block(final String id) {
        final ItemStack stack = LegacyAeItemMappings.stack(MekEng.MODID + ':' + id, 0, 1);
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock();
        }
        return Blocks.BARRIER;
    }
}
