package github.formlessdragon.appcompat.bridge.pneumaticcraft;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;

/**
 * Exposes the requester state that the new AE node services need without exposing PneumaticCraft implementation
 * details to the bridge.
 */
public interface PneumaticCraftRequesterAccess {

    /**
     * Returns the inventories currently announced to this requester as providing items.
     */
    List<IAEItemStack> appcompat$getProvidingItems();

    /**
     * Returns the requester filter inventory whose entries mirror outstanding AE crafting requests.
     */
    IItemHandlerModifiable appcompat$getFilters();

    /**
     * Returns the item used to represent this requester node in AE network UIs.
     */
    ItemStack appcompat$getVisualItemStack();

    /**
     * Persists requester state after the managed node changes persistent state.
     */
    void appcompat$markRequesterDirty();
}
