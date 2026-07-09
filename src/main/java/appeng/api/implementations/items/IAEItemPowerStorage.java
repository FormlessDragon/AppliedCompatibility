package appeng.api.implementations.items;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import net.minecraft.item.ItemStack;

/**
 * Old AE item power storage ABI implemented by legacy powered items.
 */
public interface IAEItemPowerStorage {

    /**
     * Inserts AE power into this item and returns the amount that could not be stored.
     */
    double injectAEPower(ItemStack stack, double amount, Actionable mode);

    /**
     * Extracts AE power from this item and returns the amount extracted.
     */
    double extractAEPower(ItemStack stack, double amount, Actionable mode);

    /**
     * Returns this item's current maximum stored AE.
     */
    double getAEMaxPower(ItemStack stack);

    /**
     * Returns this item's currently stored AE.
     */
    double getAECurrentPower(ItemStack stack);

    /**
     * Returns whether networks may read from or write to this item.
     */
    AccessRestriction getPowerFlow(ItemStack stack);
}
