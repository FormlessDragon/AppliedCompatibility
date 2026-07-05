package appeng.api.definitions;

import net.minecraft.item.ItemStack;

public interface IComparableDefinition {

    boolean isSameAs(ItemStack comparableStack);
}
