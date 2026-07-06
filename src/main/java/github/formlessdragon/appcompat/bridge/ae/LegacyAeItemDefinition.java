package github.formlessdragon.appcompat.bridge.ae;

import ae2.util.helpers.ItemComparisonHelper;
import appeng.api.definitions.IItemDefinition;
import github.formlessdragon.appcompat.common.item.AppCompatItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class LegacyAeItemDefinition implements IItemDefinition {

    private final String identifier;
    private final String itemName;
    private final int meta;

    public LegacyAeItemDefinition(final String identifier, final String itemName, final int meta) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Legacy AE definition identifier is required");
        }
        this.identifier = identifier;
        this.itemName = itemName;
        this.meta = meta;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public Optional<Item> maybeItem() {
        final ItemStack stack = this.stack(1);
        if (stack.isEmpty() || stack.getItem() == AppCompatItems.ERROR_ITEM) {
            return Optional.empty();
        }
        return Optional.of(stack.getItem());
    }

    @Override
    public Optional<ItemStack> maybeStack(final int stackSize) {
        return Optional.of(this.stack(stackSize));
    }

    @Override
    public boolean isEnabled() {
        return !this.stack(1).isEmpty();
    }

    @Override
    public boolean isSameAs(final ItemStack comparableStack) {
        return comparableStack != null
            && !comparableStack.isEmpty()
            && ItemComparisonHelper.isEqualItemType(comparableStack, this.stack(1));
    }

    protected ItemStack stack(final int stackSize) {
        return LegacyAeItemMappings.stack(this.itemName, this.meta, stackSize);
    }
}
