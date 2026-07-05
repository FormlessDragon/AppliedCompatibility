package appeng.core.features;

import appeng.api.util.AEColor;
import appeng.api.util.AEColoredItemDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public final class ColoredItemDefinition implements AEColoredItemDefinition {

    private final ae2.core.definitions.ColoredItemDefinition<?> definition;

    public ColoredItemDefinition(final ae2.core.definitions.ColoredItemDefinition<?> definition) {
        this.definition = definition;
    }

    @Override
    public Block block(final AEColor color) {
        return null;
    }

    @Override
    public Item item(final AEColor color) {
        if (this.definition == null) {
            return null;
        }
        return this.definition.item(toNewColor(color));
    }

    @Override
    public Class<? extends TileEntity> entity(final AEColor color) {
        return null;
    }

    @Override
    public ItemStack stack(final AEColor color, final int stackSize) {
        if (this.definition == null) {
            return ItemStack.EMPTY;
        }
        return this.definition.stack(toNewColor(color), stackSize);
    }

    @Override
    public ItemStack[] allStacks(final int stackSize) {
        final AEColor[] colors = AEColor.values();
        final ItemStack[] stacks = new ItemStack[colors.length];
        for (int i = 0; i < colors.length; i++) {
            stacks[i] = stack(colors[i], stackSize);
        }
        return stacks;
    }

    @Override
    public boolean sameAs(final AEColor color, final ItemStack comparableItem) {
        if (comparableItem == null || comparableItem.isEmpty()) {
            return false;
        }
        final Item item = item(color);
        return item != null && comparableItem.getItem() == item;
    }

    private static ae2.api.util.AEColor toNewColor(final AEColor color) {
        return ae2.api.util.AEColor.valueOf(color.name());
    }
}
