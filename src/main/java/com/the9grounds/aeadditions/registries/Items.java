package com.the9grounds.aeadditions.registries;

import com.the9grounds.aeadditions.AEAdditions;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.item.Item;

public final class Items {

    public static final Item CELL_COMPONENT_1024k = item("cell_component_1024");
    public static final Item CELL_COMPONENT_4096k = item("cell_component_4096");
    public static final Item CELL_COMPONENT_16384k = item("cell_component_16384");
    public static final Item CELL_COMPONENT_65536k = item("cell_component_65536");
    public static final Item SUPER_CELL_COMPONENT_1k = item("super_cell_component_1k");
    public static final Item SUPER_CELL_COMPONENT_4k = item("super_cell_component_4k");
    public static final Item SUPER_CELL_COMPONENT_16k = item("super_cell_component_16k");
    public static final Item SUPER_CELL_COMPONENT_64k = item("super_cell_component_64k");
    public static final Item SUPER_CELL_COMPONENT_256k = item("super_cell_component_256k");
    public static final Item SUPER_CELL_COMPONENT_1024k = item("super_cell_component_1024k");
    public static final Item SUPER_CELL_COMPONENT_4096k = item("super_cell_component_4096k");
    public static final Item SUPER_CELL_COMPONENT_16M = item("super_cell_component_16m");
    public static final Item SUPER_CELL_COMPONENT_65M = item("super_cell_component_65m");
    public static final Item ITEM_STORAGE_CELL_1024k = item("item_storage_cell_1024");
    public static final Item ITEM_STORAGE_CELL_4096k = item("item_storage_cell_4096");
    public static final Item ITEM_STORAGE_CELL_16384k = item("item_storage_cell_16384");
    public static final Item ITEM_STORAGE_CELL_65536k = item("item_storage_cell_65536");
    public static final Item FLUID_STORAGE_CELL_1024k = item("fluid_storage_cell_1024");
    public static final Item FLUID_STORAGE_CELL_4096k = item("fluid_storage_cell_4096");
    public static final Item FLUID_STORAGE_CELL_16384k = item("fluid_storage_cell_16384");
    public static final Item CHEMICAL_STORAGE_CELL_1024k = item("chemical_storage_cell_1024");
    public static final Item CHEMICAL_STORAGE_CELL_4096k = item("chemical_storage_cell_4096");
    public static final Item CHEMICAL_STORAGE_CELL_16384k = item("chemical_storage_cell_16384");
    public static final Item SUPER_CELL_HOUSING = item("super_cell_housing");
    public static final Item SUPER_CELL_1k = item("super_cell_1k");
    public static final Item SUPER_CELL_4k = item("super_cell_4k");
    public static final Item SUPER_CELL_16k = item("super_cell_16k");
    public static final Item SUPER_CELL_64k = item("super_cell_64k");
    public static final Item SUPER_CELL_256k = item("super_cell_256k");
    public static final Item SUPER_CELL_1024k = item("super_cell_1024k");
    public static final Item SUPER_CELL_4096k = item("super_cell_4096k");
    public static final Item SUPER_CELL_16M = item("super_cell_16m");
    public static final Item SUPER_CELL_65M = item("super_cell_65m");
    public static final Item DISK_FLUID_HOUSING = item("disk_fluid_housing");
    public static final Item DISK_CHEMICAL_HOUSING = item("disk_chemical_housing");
    public static final Item DISK_1024k = item("disk_item_1024k");
    public static final Item DISK_4096k = item("disk_item_4096k");
    public static final Item DISK_16384k = item("disk_item_16384k");
    public static final Item DISK_65536k = item("disk_item_65536k");

    private Items() {
    }

    public static void init() {
    }

    private static Item item(final String id) {
        return LegacyAeItemMappings.stack(AEAdditions.ID + ':' + id, 0, 1).getItem();
    }
}
