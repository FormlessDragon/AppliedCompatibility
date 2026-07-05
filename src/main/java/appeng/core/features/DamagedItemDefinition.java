package appeng.core.features;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public final class DamagedItemDefinition extends ItemDefinition {

    private final int damage;

    public DamagedItemDefinition(final String identifier, final Item item, final int damage) {
        super(identifier, item);
        this.damage = damage;
    }

    @Override
    public Optional<ItemStack> maybeStack(final int stackSize) {
        if (stackSize <= 0) {
            throw new IllegalArgumentException("Stack size must be positive");
        }
        return this.maybeItem().map(item -> new ItemStack(item, stackSize, this.damage));
    }

    @Override
    public boolean isSameAs(final ItemStack comparableStack) {
        return comparableStack != null
            && !comparableStack.isEmpty()
            && this.maybeItem().filter(item -> comparableStack.getItem() == item).isPresent()
            && comparableStack.getItemDamage() == this.damage;
    }
}
