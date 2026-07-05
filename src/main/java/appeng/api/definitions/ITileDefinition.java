package appeng.api.definitions;

import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public interface ITileDefinition extends IBlockDefinition {

    Optional<? extends Class<? extends TileEntity>> maybeEntity();
}
