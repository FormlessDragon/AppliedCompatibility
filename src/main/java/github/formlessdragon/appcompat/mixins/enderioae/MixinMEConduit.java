package github.formlessdragon.appcompat.mixins.enderioae;

import ae2.api.networking.GridHelper;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.util.AECableType;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.conduit.me.conduit.MEConduit;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOLegacyGridNode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(value = MEConduit.class, remap = false)
public abstract class MixinMEConduit {

    @Unique
    private static final String appcompat$nodeNbtKey = "appcompat_enderio_me_node";

    @Unique
    private NBTTagCompound appcompat$pendingNodeNbt;

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
        final IInWorldGridNodeHost host = GridHelper.getNodeHost(world, pos);
        return host != null && host.getCableConnectionType(dir.getOpposite()) != AECableType.NONE;
    }

    @Inject(method = "connectionsChanged", at = @At("TAIL"))
    private void appcompat$refreshNewAeConnections(final CallbackInfo ci) {
        final appeng.api.networking.IGridNode legacyNode = ((MEConduit) (Object) this).getGridNode();
        if (legacyNode instanceof EnderIOLegacyGridNode enderIONode) {
            enderIONode.refreshConnections();
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void appcompat$readNewAeNode(final NBTTagCompound nbtRoot, final CallbackInfo ci) {
        this.appcompat$pendingNodeNbt = nbtRoot.hasKey(appcompat$nodeNbtKey, 10) ? nbtRoot.copy() : null;
        final appeng.api.networking.IGridNode legacyNode = ((MEConduit) (Object) this).getGridNode();
        if (legacyNode instanceof EnderIOLegacyGridNode enderIONode && this.appcompat$pendingNodeNbt != null) {
            enderIONode.loadFromNBT(appcompat$nodeNbtKey, this.appcompat$pendingNodeNbt);
            this.appcompat$pendingNodeNbt = null;
        }
    }

    @Inject(method = "setGridNode", at = @At("TAIL"))
    private void appcompat$loadNewAeNode(final appeng.api.networking.IGridNode legacyNode, final CallbackInfo ci) {
        if (legacyNode instanceof EnderIOLegacyGridNode enderIONode && this.appcompat$pendingNodeNbt != null) {
            enderIONode.loadFromNBT(appcompat$nodeNbtKey, this.appcompat$pendingNodeNbt);
            this.appcompat$pendingNodeNbt = null;
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void appcompat$writeNewAeNode(final NBTTagCompound nbtRoot, final CallbackInfo ci) {
        final appeng.api.networking.IGridNode legacyNode = ((MEConduit) (Object) this).getGridNode();
        if (legacyNode instanceof EnderIOLegacyGridNode enderIONode) {
            enderIONode.saveToNBT(appcompat$nodeNbtKey, nbtRoot);
        }
    }
}
