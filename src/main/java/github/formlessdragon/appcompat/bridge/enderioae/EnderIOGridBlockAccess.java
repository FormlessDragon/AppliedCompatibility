package github.formlessdragon.appcompat.bridge.enderioae;

import appeng.api.networking.GridFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Exposes the EnderIO ME conduit grid block data required to create a new AE managed node.
 */
public interface EnderIOGridBlockAccess {

    /**
     * @return the item used by AE network UIs to identify this conduit node.
     */
    ItemStack appcompat$visualItemStack();

    /**
     * @return old AE flags requested by the EnderIO grid block.
     */
    EnumSet<GridFlags> appcompat$legacyFlags();

    /**
     * @return conduit sides currently allowed to expose this node to in-world AE connections.
     */
    EnumSet<EnumFacing> appcompat$connectableSides();

    /**
     * @return the world containing the conduit bundle that owns this grid block.
     */
    World appcompat$world();

    /**
     * @return the position of the conduit bundle that owns this grid block.
     */
    BlockPos appcompat$pos();

    /**
     * Called when the new AE node changes grid state and EnderIO should refresh conduit state.
     */
    void appcompat$gridChanged();
}
