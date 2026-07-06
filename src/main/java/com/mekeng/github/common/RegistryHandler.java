package com.mekeng.github.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

public final class RegistryHandler {

    public void item(final String id, final Item item) {
        if (id == null || id.isEmpty() || item == null) {
            throw new IllegalArgumentException("MekanismEnergistics registry item id and item are required");
        }
    }

    public void block(final String id, final Block block, final Class<? extends TileEntity> tileClass) {
        if (id == null || id.isEmpty() || block == null) {
            throw new IllegalArgumentException("MekanismEnergistics registry block id and block are required");
        }
    }
}
