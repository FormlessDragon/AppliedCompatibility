package com.projecturanus.betterp2p.item;

import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.item.Item;

public final class ModItems {

    public static final Item ADVANCED_MEMORY_CARD =
        LegacyAeItemMappings.stack("betterp2p:advanced_memory_card", 0, 1).getItem();
    public static final Item advancedMemoryCard = ADVANCED_MEMORY_CARD;

    private ModItems() {
    }
}
