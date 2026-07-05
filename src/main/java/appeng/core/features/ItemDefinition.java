package appeng.core.features;

import appeng.api.definitions.IItemDefinition;
import ae2.util.helpers.ItemComparisonHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class ItemDefinition implements IItemDefinition {

    private final String identifier;
    private final ae2.core.definitions.ItemDefinition<?> definition;
    private final Item item;

    public ItemDefinition(final String registryName, final Item item) {
        this.identifier = requireIdentifier(registryName);
        this.definition = null;
        this.item = item;
    }

    public ItemDefinition(final String registryName, final ae2.core.definitions.ItemDefinition<?> definition) {
        this.identifier = requireIdentifier(registryName);
        this.definition = definition;
        this.item = null;
    }

    public static ItemDefinition disabled(final String registryName) {
        return new ItemDefinition(registryName, (Item) null);
    }

    private static String requireIdentifier(final String registryName) {
        if (registryName == null || registryName.isEmpty()) {
            throw new IllegalArgumentException("AE item definition identifier is required");
        }
        return registryName;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public final Optional<Item> maybeItem() {
        if (this.definition != null) {
            return Optional.ofNullable(this.definition.asItem());
        }
        return Optional.ofNullable(this.item);
    }

    @Override
    public Optional<ItemStack> maybeStack(final int stackSize) {
        if (stackSize <= 0) {
            throw new IllegalArgumentException("Stack size must be positive");
        }
        if (this.definition != null) {
            return Optional.ofNullable(this.definition.stack(stackSize));
        }
        return this.item == null ? Optional.of(new ItemStack(item, stackSize)) : Optional.empty();
    }

    @Override
    public boolean isEnabled() {
        return this.maybeItem().isPresent();
    }

    @Override
    public boolean isSameAs(final ItemStack comparableStack) {
        if (comparableStack == null || comparableStack.isEmpty()) {
            return false;
        }
        if (this.definition != null) {
            return this.definition.is(comparableStack);
        }
        final Optional<ItemStack> stack = this.maybeStack(1);
        return stack.isPresent() && ItemComparisonHelper.isEqualItemType(comparableStack, stack.get());
    }
}
