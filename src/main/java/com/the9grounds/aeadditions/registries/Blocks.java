package com.the9grounds.aeadditions.registries;

import com.the9grounds.aeadditions.AEAdditions;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class Blocks {

    public static final Block BLOCK_CRAFTING_STORAGE_1024k = block("1024k_crafting_storage");
    public static final Block BLOCK_CRAFTING_STORAGE_4096k = block("4096k_crafting_storage");
    public static final Block BLOCK_CRAFTING_STORAGE_16384k = block("16384k_crafting_storage");
    public static final Block BLOCK_CRAFTING_STORAGE_65536k = block("65536k_crafting_storage");
    public static final Block BLOCK_ME_WIRELESS_TRANSCEIVER = block("me_wireless_transceiver");

    private Blocks() {
    }

    public static void init() {
    }

    private static Block block(final String id) {
        final ItemStack stack = LegacyAeItemMappings.stack(AEAdditions.ID + ':' + id, 0, 1);
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock();
        }
        return net.minecraft.init.Blocks.BARRIER;
    }
}
