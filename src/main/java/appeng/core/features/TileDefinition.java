package appeng.core.features;

import appeng.api.definitions.ITileDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public final class TileDefinition extends BlockDefinition implements ITileDefinition {

    private final ae2.core.definitions.TileDefinition<?> tileDefinition;
    private final Class<? extends TileEntity> tileClass;

    public TileDefinition(final String registryName, final Block block, final ItemBlock item,
                          final Class<? extends TileEntity> tileClass) {
        super(registryName, block, item);
        this.tileDefinition = null;
        this.tileClass = tileClass;
    }

    public TileDefinition(final String registryName, final ae2.core.definitions.BlockDefinition<?> blockDefinition,
                          final ae2.core.definitions.TileDefinition<?> tileDefinition) {
        super(registryName, blockDefinition);
        this.tileDefinition = tileDefinition;
        this.tileClass = null;
    }

    public static TileDefinition disabled(final String registryName) {
        return new TileDefinition(registryName, null, null, null);
    }

    @Override
    public Optional<? extends Class<? extends TileEntity>> maybeEntity() {
        if (this.tileDefinition != null) {
            return Optional.of(this.tileDefinition.tileClass());
        }
        return Optional.ofNullable(this.tileClass);
    }
}
