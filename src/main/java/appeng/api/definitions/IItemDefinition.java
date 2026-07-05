package appeng.api.definitions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface IItemDefinition extends IComparableDefinition {

    String identifier();

    Optional<Item> maybeItem();

    Optional<ItemStack> maybeStack(int stackSize);

    boolean isEnabled();

    default boolean isSameAs(final ItemStack stack) {
        return stack != null && this.maybeItem().filter(item -> !stack.isEmpty() && stack.getItem() == item).isPresent();
    }
}
