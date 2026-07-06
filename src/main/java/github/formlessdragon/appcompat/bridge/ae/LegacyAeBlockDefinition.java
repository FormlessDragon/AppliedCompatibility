package github.formlessdragon.appcompat.bridge.ae;

import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Optional;

public class LegacyAeBlockDefinition extends LegacyAeItemDefinition implements IBlockDefinition {

    public LegacyAeBlockDefinition(final String identifier, final String itemName, final int meta) {
        super(identifier, itemName, meta);
    }

    @Override
    public Optional<Block> maybeBlock() {
        final ItemStack stack = this.stack(1);
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return Optional.of(itemBlock.getBlock());
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemBlock> maybeItemBlock() {
        final ItemStack stack = this.stack(1);
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return Optional.of(itemBlock);
        }
        return Optional.empty();
    }

    @Override
    public boolean isSameAs(final IBlockAccess world, final BlockPos pos) {
        return world != null
            && pos != null
            && this.maybeBlock().filter(block -> world.getBlockState(pos).getBlock() == block).isPresent();
    }
}
