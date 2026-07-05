package appeng.api.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface AEColoredItemDefinition {

    Block block(AEColor color);

    Item item(AEColor color);

    Class<? extends TileEntity> entity(AEColor color);

    ItemStack stack(AEColor color, int stackSize);

    ItemStack[] allStacks(int stackSize);

    boolean sameAs(AEColor color, ItemStack comparableItem);
}
