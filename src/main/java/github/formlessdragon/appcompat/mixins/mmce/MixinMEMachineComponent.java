package github.formlessdragon.appcompat.mixins.mmce;

import ae2.api.implementations.IPowerChannelState;
import ae2.api.networking.GridFlags;
import ae2.api.networking.GridHelper;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IGridNodeListener;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.crafting.ICraftingProvider;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.util.AECableType;
import ae2.me.helpers.IGridConnectedTile;
import ae2.me.helpers.TileNodeListener;
import github.kasuminova.mmce.common.tile.base.MEMachineComponent;
import hellfirepvp.modularmachinery.common.tiles.base.TileColorableMachineComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MEMachineComponent.class, remap = false, priority = 999)
public abstract class MixinMEMachineComponent extends TileColorableMachineComponent
    implements IGridConnectedTile, IInWorldGridNodeHost, IPowerChannelState,
    github.formlessdragon.appcompat.bridge.ae.MMCEGridNodeAccess {

    @Unique
    protected IManagedGridNode appcompat$mainNode;

    @Shadow
    public abstract ItemStack getVisualItemStack();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void appcompat$initNode(final CallbackInfo ci) {
        if (this instanceof github.formlessdragon.appcompat.bridge.ae.MMCEProxyNodeHost extension) {
            final IManagedGridNode shared = extension.appcompat$getLegacyProxyNode();
            if (shared != null) {
                this.appcompat$mainNode = shared;
                return;
            }
        }
        final IGridConnectedTile owner = this;
        this.appcompat$mainNode = GridHelper.createManagedNode(owner, TileNodeListener.INSTANCE)
                                            .setVisualRepresentation(getVisualItemStack())
                                            .setInWorldNode(true)
                                            .setIdlePowerUsage(1.0D)
                                            .setFlags(GridFlags.REQUIRE_CHANNEL)
                                            .setTagName("appcompat_proxy");

        if (this instanceof IGridTickable tickable) {
            this.appcompat$mainNode.addService(IGridTickable.class, tickable);
        }
        if (this instanceof ICraftingProvider craftingProvider) {
            this.appcompat$mainNode.addService(ICraftingProvider.class, craftingProvider);
        }
    }

    @Override
    @Unique
    public IManagedGridNode getMainNode() {
        return this.appcompat$mainNode;
    }

    @Override
    @Unique
    public void appcompat$setMainNode(final IManagedGridNode node) {
        java.util.Objects.requireNonNull(node, "node");
        if (this.appcompat$mainNode != node) {
            if (this.appcompat$mainNode != null) {
                this.appcompat$mainNode.destroy();
            }
            this.appcompat$mainNode = node;
        }
    }

    @Override
    @Unique
    public void saveChanges() {
        markDirty();
    }

    @Override
    @Unique
    public void onMainNodeStateChanged(final IGridNodeListener.State reason) {
        markForUpdate();
    }

    @Override
    @Unique
    public IGridNode getGridNode(final EnumFacing dir) {
        return this.appcompat$mainNode == null ? null : this.appcompat$mainNode.getNode();
    }

    @Override
    @Unique
    public AECableType getCableConnectionType(final EnumFacing dir) {
        return AECableType.SMART;
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"))
    private void appcompat$writeNode(final NBTTagCompound compound, final CallbackInfo ci) {
        if (this.appcompat$mainNode != null) {
            this.appcompat$mainNode.saveToNBT(compound);
        }
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void appcompat$readNode(final NBTTagCompound compound, final CallbackInfo ci) {
        if (!world.isRemote && this.appcompat$mainNode != null) {
            this.appcompat$mainNode.loadFromNBT(compound);
        }
    }

    @Inject(method = "validate", at = @At("TAIL"))
    private void appcompat$createNode(final CallbackInfo ci) {
        GridHelper.onFirstTick((MEMachineComponent) (Object) this, tile -> {
            final IManagedGridNode node = this.appcompat$mainNode;
            if (node != null && !node.isReady()) {
                node.create(tile.getWorld(), tile.getPos());
                onMainNodeStateChanged(IGridNodeListener.State.GRID_BOOT);
            }
        });
    }

    @Inject(method = "invalidate", at = @At("TAIL"))
    private void appcompat$destroyOnInvalidate(final CallbackInfo ci) {
        if (this.appcompat$mainNode != null) {
            this.appcompat$mainNode.destroy();
        }
    }

    @Inject(method = "onChunkUnload", at = @At("TAIL"))
    private void appcompat$destroyOnUnload(final CallbackInfo ci) {
        if (this.appcompat$mainNode != null) {
            this.appcompat$mainNode.destroy();
        }
    }

    /**
     * @author circulation
     * @reason 覆写到新实现
     */
    @Overwrite
    public boolean isPowered() {
        return this.appcompat$mainNode != null && this.appcompat$mainNode.isPowered();
    }

    /**
     * @author circulation
     * @reason 覆写到新实现
     */
    @Overwrite
    public boolean isActive() {
        return this.appcompat$mainNode != null && this.appcompat$mainNode.isActive();
    }

    /**
     * @author circulation
     * @reason 覆写到新实现
     */
    @Overwrite
    public void notifyNeighbors() {
        if (this.appcompat$mainNode != null && this.appcompat$mainNode.isActive()) {
            final var grid = this.appcompat$mainNode.getGrid();
            if (grid != null) {
                grid.getTickManager().wakeDevice(this.appcompat$mainNode.getNode());
            }
            final World world = getWorld();
            if (world != null) {
                world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
            }
        }
    }
}
