package appeng.core.features;

import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Optional;

public class BlockDefinition extends ItemDefinition implements IBlockDefinition {

    private final ae2.core.definitions.BlockDefinition<?> definition;
    private final Block block;
    private final ItemBlock itemBlock;

    public BlockDefinition(final String registryName, final Block block, final ItemBlock item) {
        super(registryName, item);
        this.definition = null;
        this.block = block;
        this.itemBlock = item;
    }

    public BlockDefinition(final String registryName, final ae2.core.definitions.BlockDefinition<?> definition) {
        super(registryName, definition == null ? null : definition.asItem());
        this.definition = definition;
        this.block = null;
        this.itemBlock = null;
    }

    public static BlockDefinition disabled(final String registryName) {
        return new BlockDefinition(registryName, null, null);
    }

    @Override
    public final Optional<Block> maybeBlock() {
        if (this.definition != null) {
            return Optional.ofNullable(this.definition.block());
        }
        return Optional.ofNullable(this.block);
    }

    @Override
    public final Optional<ItemBlock> maybeItemBlock() {
        if (this.definition != null) {
            return Optional.ofNullable(this.definition.item());
        }
        return Optional.ofNullable(this.itemBlock);
    }

    @Override
    public Optional<ItemStack> maybeStack(final int stackSize) {
        if (stackSize <= 0) {
            throw new IllegalArgumentException("Stack size must be positive");
        }
        if (this.definition != null) {
            final ItemStack stack = this.definition.stack(stackSize);
            return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
        }
        return this.maybeBlock().map(block -> new ItemStack(block, stackSize));
    }

    @Override
    public final boolean isSameAs(final IBlockAccess world, final BlockPos pos) {
        return world != null && pos != null
            && this.maybeBlock().filter(block -> world.getBlockState(pos).getBlock() == block).isPresent();
    }
}
