package github.formlessdragon.appcompat.mixins.packagedauto;

import ae2.api.AECapabilities;
import ae2.api.implementations.IPowerChannelState;
import ae2.api.networking.GridFlags;
import ae2.api.networking.GridHelper;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.crafting.ICraftingProvider;
import ae2.api.util.AECableType;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoNodeListener;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoNodeOwner;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thelm.packagedauto.energy.EnergyStorage;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedauto.tile.TileBase;

@Mixin(value = TileBase.class, remap = false)
public abstract class MixinTileBase extends TileEntity implements PackagedAutoNodeOwner, IInWorldGridNodeHost, IPowerChannelState {

    @Unique
    private IManagedGridNode appcompat$mainNode;
    @Unique
    private appeng.api.networking.IGridNode appcompat$legacyNode;

    @Shadow
    public abstract void syncTile(boolean rerender);

    @Shadow
    public abstract EnergyStorage getEnergyStorage();

    @Shadow
    public abstract InventoryTileBase getInventory();

    @Unique
    @SuppressWarnings("AddedMixinMembersNamePattern")
    protected ItemStack getVisualItemStack() {
        return ItemStack.EMPTY;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void appcompat$initNode(final CallbackInfo ci) {
        appcompat$mainNode = GridHelper.createManagedNode(this, PackagedAutoNodeListener.INSTANCE)
                                       .setVisualRepresentation(getVisualItemStack())
                                       .setInWorldNode(true)
                                       .setIdlePowerUsage(1.0D)
                                       .setFlags(GridFlags.REQUIRE_CHANNEL)
                                       .setTagName("appcompat_packagedauto");
        if (this instanceof ICraftingProvider craftingProvider) {
            appcompat$mainNode.addService(ICraftingProvider.class, craftingProvider);
        }
    }

    @Override
    public IManagedGridNode appcompat$mainNode() {
        return appcompat$mainNode;
    }

    @Override
    public appeng.api.networking.IGridNode appcompat$legacyNode() {
        return this.appcompat$legacyNode;
    }

    @Override
    public void appcompat$setLegacyNode(final appeng.api.networking.IGridNode node) {
        this.appcompat$legacyNode = node;
    }

    @Override
    public void appcompat$loadLegacyNode(final NBTTagCompound data) {
        appcompat$mainNode().loadFromNBT(data);
    }

    @Override
    public void appcompat$saveLegacyNode(final NBTTagCompound data) {
        appcompat$mainNode().saveToNBT(data);
    }

    @Override
    public void appcompat$createLegacyNode() {
        if (appcompat$mainNode() != null && !appcompat$mainNode().isReady() && this.world != null && !this.world.isRemote) {
            final IManagedGridNode node = appcompat$mainNode();
            GridHelper.onFirstTick((TileBase) (Object) this, tile -> {
                if (!node.isReady()) {
                    node.create(tile.getWorld(), tile.getPos());
                }
            });
        }
    }

    @Override
    public void appcompat$destroyLegacyNode() {
        if (appcompat$mainNode() != null) {
            appcompat$mainNode().destroy();
        }
    }

    @Override
    public void appcompat$requestCraftingUpdate() {
        if (this instanceof PackagedPatternContainer container) {
            container.getTerminalPatternInventory().invalidate();
        }
        if (appcompat$mainNode() != null && appcompat$mainNode().getNode() != null && appcompat$mainNode().isReady() && appcompat$mainNode().hasGridBooted()) {
            ICraftingProvider.requestUpdate(appcompat$mainNode());
        }
    }

    @Override
    public void appcompat$nodeChanged() {
        syncTile(true);
        appcompat$requestCraftingUpdate();
    }

    @Override
    public void appcompat$nodeSaveChanges() {
        markDirty();
    }

    @Override
    public IGridNode getGridNode(final EnumFacing dir) {
        return appcompat$mainNode() == null ? null : appcompat$mainNode().getNode();
    }

    @Override
    public AECableType getCableConnectionType(final EnumFacing dir) {
        return AECableType.SMART;
    }

    @Override
    public boolean isPowered() {
        return appcompat$mainNode() != null && appcompat$mainNode().isPowered();
    }

    @Override
    public boolean isActive() {
        return appcompat$mainNode() != null && appcompat$mainNode().isActive();
    }

    @Inject(method = "hasCapability", at = @At("HEAD"), cancellable = true)
    private void appcompat$hasAeCapability(final Capability<?> capability, final EnumFacing from,
                                           final CallbackInfoReturnable<Boolean> cir) {
        if (capability == AECapabilities.IN_WORLD_GRID_NODE_HOST) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true)
    private <T> void appcompat$getAeCapability(final Capability<T> capability, final EnumFacing facing,
                                               final CallbackInfoReturnable<T> cir) {
        if (capability == AECapabilities.IN_WORLD_GRID_NODE_HOST) {
            cir.setReturnValue(AECapabilities.IN_WORLD_GRID_NODE_HOST.cast(this));
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void appcompat$readNode(final NBTTagCompound nbt, final CallbackInfo ci) {
        if (nbt.hasKey("Node")) {
            appcompat$mainNode().loadFromNBT(nbt.getCompoundTag("Node"));
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void appcompat$writeNode(final NBTTagCompound nbt, final CallbackInfoReturnable<NBTTagCompound> cir) {
        final NBTTagCompound nodeTag = new NBTTagCompound();
        appcompat$mainNode().saveToNBT(nodeTag);
        nbt.setTag("Node", nodeTag);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        appcompat$destroyLegacyNode();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        appcompat$destroyLegacyNode();
    }
}
