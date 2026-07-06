package com.the9grounds.aeadditions.registries;

import com.the9grounds.aeadditions.AEAdditions;
import net.minecraft.util.ResourceLocation;

public final class Ids {

    public static final ResourceLocation CELL_COMPONENT_1024 = id("cell_component_1024");
    public static final ResourceLocation CELL_COMPONENT_4096 = id("cell_component_4096");
    public static final ResourceLocation CELL_COMPONENT_16384 = id("cell_component_16384");
    public static final ResourceLocation CELL_COMPONENT_65536 = id("cell_component_65536");
    public static final ResourceLocation ITEM_STORAGE_CELL_1024 = id("item_storage_cell_1024");
    public static final ResourceLocation ITEM_STORAGE_CELL_4096 = id("item_storage_cell_4096");
    public static final ResourceLocation ITEM_STORAGE_CELL_16384 = id("item_storage_cell_16384");
    public static final ResourceLocation ITEM_STORAGE_CELL_65536 = id("item_storage_cell_65536");
    public static final ResourceLocation FLUID_STORAGE_CELL_1024 = id("fluid_storage_cell_1024");
    public static final ResourceLocation FLUID_STORAGE_CELL_4096 = id("fluid_storage_cell_4096");
    public static final ResourceLocation FLUID_STORAGE_CELL_16384 = id("fluid_storage_cell_16384");
    public static final ResourceLocation CHEMICAL_STORAGE_CELL_1024 = id("chemical_storage_cell_1024");
    public static final ResourceLocation CHEMICAL_STORAGE_CELL_4096 = id("chemical_storage_cell_4096");
    public static final ResourceLocation CHEMICAL_STORAGE_CELL_16384 = id("chemical_storage_cell_16384");
    public static final ResourceLocation DISK_FLUID_HOUSING = id("disk_fluid_housing");
    public static final ResourceLocation DISK_CHEMICAL_HOUSING = id("disk_chemical_housing");
    public static final ResourceLocation DISK_1024k = id("disk_item_1024k");
    public static final ResourceLocation DISK_4096k = id("disk_item_4096k");
    public static final ResourceLocation DISK_16384k = id("disk_item_16384k");
    public static final ResourceLocation DISK_65536k = id("disk_item_65536k");
    public static final ResourceLocation CRAFTING_STORAGE_1024k = id("1024k_crafting_storage");
    public static final ResourceLocation CRAFTING_STORAGE_4096k = id("4096k_crafting_storage");
    public static final ResourceLocation CRAFTING_STORAGE_16384k = id("16384k_crafting_storage");
    public static final ResourceLocation CRAFTING_STORAGE_65536k = id("65536k_crafting_storage");

    private Ids() {
    }

    private static ResourceLocation id(final String id) {
        return new ResourceLocation(AEAdditions.ID, id);
    }
}
