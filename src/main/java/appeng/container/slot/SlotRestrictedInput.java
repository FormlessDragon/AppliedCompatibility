package appeng.container.slot;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class SlotRestrictedInput extends AppEngSlot {

    private final PlacableItemType valid;

    public SlotRestrictedInput(final PlacableItemType valid, final IItemHandler inv, final int idx, final int x, final int y, final InventoryPlayer p) {
        super(inv, idx, x, y);
        this.valid = valid;
    }

    @Override
    public boolean isItemValid(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (valid == PlacableItemType.ENCODED_PATTERN || valid == PlacableItemType.VALID_ENCODED_PATTERN_W_OUTPUT) {
            return ae2.api.crafting.PatternDetailsHelper.isEncodedPattern(stack) && super.isItemValid(stack);
        }
        return super.isItemValid(stack);
    }

    @Override public int getSlotStackLimit() {
        return valid == PlacableItemType.ENCODED_PATTERN || valid == PlacableItemType.VALID_ENCODED_PATTERN_W_OUTPUT
            ? 1 : super.getSlotStackLimit();
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
