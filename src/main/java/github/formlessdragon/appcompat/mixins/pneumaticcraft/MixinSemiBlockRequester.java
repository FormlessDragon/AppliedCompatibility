package github.formlessdragon.appcompat.mixins.pneumaticcraft;

import ae2.tile.misc.TileInterface;
import appeng.api.storage.data.IAEItemStack;
import github.formlessdragon.appcompat.bridge.pneumaticcraft.PneumaticCraftRequesterAccess;
import github.formlessdragon.appcompat.bridge.pneumaticcraft.PneumaticCraftRequesterNode;
import me.desht.pneumaticcraft.common.item.Itemss;
import me.desht.pneumaticcraft.common.semiblock.IProvidingInventoryListener;
import me.desht.pneumaticcraft.common.semiblock.SemiBlockLogistics;
import me.desht.pneumaticcraft.common.semiblock.SemiBlockRequester;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Set;

@Mixin(value = SemiBlockRequester.class, remap = false)
public abstract class MixinSemiBlockRequester extends SemiBlockLogistics implements PneumaticCraftRequesterAccess {

    @Shadow
    private boolean aeMode;

    @Shadow
    @Final
    private Set<IProvidingInventoryListener.TileEntityAndFace> providingInventories;

    @Unique
    private PneumaticCraftRequesterNode appcompat$requesterNode;

    @Shadow
    public abstract TileEntity getTileEntity();

    @Invoker("getProvidingItems")
    protected abstract List<IAEItemStack> appcompat$invokeGetProvidingItems();

    @Override
    @Unique
    public List<IAEItemStack> appcompat$getProvidingItems() {
        return this.appcompat$invokeGetProvidingItems();
    }

    @Override
    @Unique
    public IItemHandlerModifiable appcompat$getFilters() {
        return getFilters();
    }

    @Override
    @Unique
    public ItemStack appcompat$getVisualItemStack() {
        return new ItemStack(Itemss.LOGISTICS_FRAME_REQUESTER);
    }

    @Override
    @Unique
    public void appcompat$markRequesterDirty() {
        final TileEntity tile = getTileEntity();
        if (tile != null) {
            tile.markDirty();
        }
    }

    @Unique
    private PneumaticCraftRequesterNode appcompat$getRequesterNode() {
        if (this.appcompat$requesterNode == null) {
            this.appcompat$requesterNode = new PneumaticCraftRequesterNode(this);
        }
        return this.appcompat$requesterNode;
    }

    /**
     * @author AppliedCompatibility
     * @reason The requester was a direct child node of an old AE interface node. Recreate that topology with a new AE
     * managed direct node instead of entering the old IGridHost path.
     */
    @Overwrite
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }
        final PneumaticCraftRequesterNode requesterNode = appcompat$getRequesterNode();
        if (!this.aeMode) {
            requesterNode.disconnect();
            return;
        }
        final TileEntity tile = getTileEntity();
        if (!(tile instanceof TileInterface interfaceTile)) {
            requesterNode.disconnect();
            return;
        }
        if (requesterNode.attach(world, getPos(), interfaceTile.getMainNode().getNode())) {
            if (world.getTotalWorldTime() % 120L == 0L) {
                requesterNode.refresh();
            }
        }
    }

    /**
     * @author AppliedCompatibility
     * @reason Keep mode changes on the new AE node lifecycle and avoid invoking the removed old grid node path.
     */
    @Overwrite
    public void handleGUIButtonPress(final int guiID, final EntityPlayer player) {
        if (guiID == 1) {
            this.aeMode = !this.aeMode;
            if (!this.aeMode && this.appcompat$requesterNode != null) {
                this.appcompat$requesterNode.disconnect();
            }
        }
        super.handleGUIButtonPress(guiID, player);
    }

    /**
     * @author AppliedCompatibility
     * @reason The new direct node owns the requester connection and must be destroyed with the semiblock.
     */
    @Overwrite
    public void invalidate() {
        super.invalidate();
        if (this.appcompat$requesterNode != null) {
            this.appcompat$requesterNode.disconnect();
        }
    }

    /**
     * @author AppliedCompatibility
     * @reason The new AE interface tile is the only valid host for the requester's direct node.
     */
    @Overwrite
    public boolean isPlacedOnInterface() {
        return getTileEntity() instanceof TileInterface;
    }

    /**
     * @author AppliedCompatibility
     * @reason Providing inventories must remain visible after replacing the old grid node with the new direct node.
     */
    @Overwrite
    public void notify(final IProvidingInventoryListener.TileEntityAndFace inventory) {
        if (this.appcompat$requesterNode != null && this.appcompat$requesterNode.isReady()) {
            this.providingInventories.add(inventory);
            this.appcompat$requesterNode.refresh();
        }
    }
}
