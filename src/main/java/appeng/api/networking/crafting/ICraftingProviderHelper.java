package appeng.api.networking.crafting;

import appeng.api.storage.data.IAEItemStack;

public interface ICraftingProviderHelper {

    void addCraftingOption(ICraftingMedium medium, ICraftingPatternDetails api);

    void setEmitable(IAEItemStack what);
}
