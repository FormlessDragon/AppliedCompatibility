package appeng.api.networking.crafting;

import appeng.api.networking.IGridCache;
import appeng.api.storage.data.IAEItemStack;

/**
 * Old AE crafting-grid query surface used by requester hosts to track outstanding item requests.
 */
public interface ICraftingGrid extends IGridCache {

    /**
     * Returns whether the item type is currently requested by any crafting CPU.
     */
    boolean isRequesting(IAEItemStack what);

    /**
     * Returns the total outstanding amount for the item type across crafting CPUs.
     */
    long requesting(IAEItemStack what);
}
