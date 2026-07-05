package github.formlessdragon.appcompat.mixins.enderioae;

import ae2.api.networking.GridHelper;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.conduit.me.conduit.MEConduit;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(value = MEConduit.class, remap = false)
public abstract class MixinMEConduit {

    /**
     * @author AppliedCompatibility
     * @reason EnderIO's original check only understands old AE IPartHost/IGridHost. New AE exposes in-world nodes
     * through AECapabilities.IN_WORLD_GRID_NODE_HOST, so the conduit must query GridHelper instead.
     */
    @Overwrite
    public boolean canConnectToExternal(@Nonnull final EnumFacing dir, final boolean ignoreDisabled) {
        final MEConduit conduit = (MEConduit) (Object) this;
        if (!ignoreDisabled && conduit.getConnectionMode(dir) == ConnectionMode.DISABLED) {
            return false;
        }
        final World world = conduit.getBundle().getBundleworld();
        final BlockPos pos = conduit.getBundle().getLocation().offset(dir);
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileConduitBundle) {
            return false;
        }
        return GridHelper.getExposedNode(world, pos, dir.getOpposite()) != null;
    }
}
