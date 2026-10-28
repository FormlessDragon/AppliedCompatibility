package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.implementations.blockentities.PatternContainerGroup;
import ae2.api.networking.IGrid;
import ae2.api.networking.crafting.ICraftingProvider;
import ae2.api.stacks.AEItemKey;
import ae2.helpers.patternprovider.PatternContainer;
import ae2.text.TextComponentItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thelm.packagedauto.PackagedAuto;
import thelm.packagedauto.tile.TileBase;

import java.util.List;

public interface PackagedPatternContainer extends PatternContainer, ICraftingProvider, PackagedAutoNodeAccess {

    @Override
    PackagedPatternInventory getTerminalPatternInventory();

    @Override
    default @Nullable IGrid getGrid() {
        final var node = appcompat$mainNode();
        return node == null ? null : node.getGrid();
    }

    @Override
    default boolean isReadOnly() {
        return true;
    }

    @Override
    default boolean containsPattern(final AEItemKey pattern) {
        return getTerminalPatternInventory().contains(pattern);
    }

    @Override
    default long getTerminalSortOrder() {
        return ((TileBase) this).getPos().toLong();
    }

    @Override
    default PatternContainerGroup getTerminalGroup() {
        final TileBase tile = (TileBase) this;
        final ItemStack icon = new ItemStack(tile.getBlockType());
        return new PatternContainerGroup(AEItemKey.of(icon),
            tile.hasCustomName() ? tile.getDisplayName() : TextComponentItemStack.of(icon), List.of());
    }

    @Override
    default void openTerminalPatternContainerGui(final EntityPlayer player) {
        final TileBase tile = (TileBase) this;
        final var pos = tile.getPos();
        player.openGui(PackagedAuto.MOD_ID, 0, tile.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }
}
