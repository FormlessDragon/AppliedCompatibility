package github.formlessdragon.appcompat.bridge.ae;

import appeng.api.definitions.ITileDefinition;
import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public final class LegacyAeTileDefinition extends LegacyAeBlockDefinition implements ITileDefinition {

    public LegacyAeTileDefinition(final String identifier, final String itemName, final int meta) {
        super(identifier, itemName, meta);
    }

    @Override
    public Optional<? extends Class<? extends TileEntity>> maybeEntity() {
        return Optional.empty();
    }
}
