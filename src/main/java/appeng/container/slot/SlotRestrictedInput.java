package appeng.container.slot;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class SlotRestrictedInput extends AppEngSlot {

    public SlotRestrictedInput(final PlacableItemType valid, final IItemHandler inv, final int idx, final int x, final int y, final InventoryPlayer p) {
        super(inv, idx, x, y);
    }

    public enum PlacableItemType {
        STORAGE_CELLS,
        ORE,
        STORAGE_COMPONENTS,
        ENCODED_PATTERN,
        VALID_ENCODED_PATTERN_W_OUTPUT,
        UPGRADES,
        WORKBENCH_CELL,
        FUEL,
        POWERED_TOOL,
        RANGE_BOOSTER,
        QE_SINGULARITY,
        SPATIAL_STORAGE_CELLS,
        TRASH,
        ENCODABLE_ITEM,
        BIOMETRIC_CARD,
        VIEWCELL,
        UPGRADES_ENCODED_PATTERN
    }
}
