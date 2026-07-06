package dev.rlnt.extracpus.setup;

import appeng.api.definitions.IBlockDefinition;
import dev.rlnt.extracpus.Constants;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;

public final class ModBlocks {

    public static final IBlockDefinition CRAFTING_STORAGE_256K =
        block(Constants.STORAGE_256K_ID);
    public static final IBlockDefinition CRAFTING_STORAGE_1024K =
        block(Constants.STORAGE_1024K_ID);
    public static final IBlockDefinition CRAFTING_STORAGE_4096K =
        block(Constants.STORAGE_4096K_ID);
    public static final IBlockDefinition CRAFTING_STORAGE_16384K =
        block(Constants.STORAGE_16384K_ID);

    private ModBlocks() {
    }

    public static void init() {
    }

    private static IBlockDefinition block(final String id) {
        return LegacyAeItemMappings.blockDefinition(Constants.MOD_ID + ':' + id);
    }
}
