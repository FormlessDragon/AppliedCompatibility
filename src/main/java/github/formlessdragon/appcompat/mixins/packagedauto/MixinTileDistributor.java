package github.formlessdragon.appcompat.mixins.packagedauto;

import ae2.api.config.Actionable;
import ae2.api.config.PatternProviderInsertionMode;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternStacks;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternTargets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thelm.packagedauto.api.DirectionalGlobalPos;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.block.BlockDistributor;
import thelm.packagedauto.network.packet.PacketBeam;
import thelm.packagedauto.recipe.IRecipeInfoProcessingPositioned;
import thelm.packagedauto.tile.TileDistributor;

import java.util.List;

@Mixin(value = TileDistributor.class, remap = false)
public abstract class MixinTileDistributor extends MixinTileBase {

    @Final
    @Shadow
    public Int2ObjectMap<DirectionalGlobalPos> positions;
    @Final
    @Shadow
    public Int2ObjectMap<ItemStack> pending;

    @Unique
    protected ItemStack getVisualItemStack() {
        return new ItemStack(BlockDistributor.ITEM_INSTANCE);
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE target 插入
     */
    @Overwrite
    public boolean acceptPackage(final IRecipeInfo recipeInfo, final List<ItemStack> stacks, final EnumFacing facing, final boolean blocking) {
        if (isBusy() || !(recipeInfo instanceof IRecipeInfoProcessingPositioned recipe)) {
            return false;
        }
        final Int2ObjectMap<ItemStack> matrix = recipe.getMatrix();
        if (!this.positions.keySet().containsAll(matrix.keySet())) {
            return false;
        }
        for (final Int2ObjectMap.Entry<ItemStack> entry : matrix.int2ObjectEntrySet()) {
            final BlockPos targetPos = this.positions.get(entry.getIntKey()).blockPos();
            if (!this.world.isBlockLoaded(targetPos)) {
                return false;
            }
            final TileEntity tile = this.world.getTileEntity(targetPos);
            if (tile == null) {
                return false;
            }
            final EnumFacing dir = this.positions.get(entry.getIntKey()).direction();
            if (blocking && PackagedPatternTargets.directTargetContainsAnyStack(this, tile, dir)) {
                return false;
            }
            final ItemStack stackRem = PackagedPatternTargets.insertIntoDirectTarget(this, tile, dir, entry.getValue(), Actionable.SIMULATE, PatternProviderInsertionMode.DEFAULT);
            if (!stackRem.isEmpty()) {
                return false;
            }
        }
        for (final Int2ObjectMap.Entry<ItemStack> entry : matrix.int2ObjectEntrySet()) {
            this.pending.put(entry.getIntKey(), entry.getValue().copy());
        }
        distributeItems();
        return true;
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE target 插入
     */
    @Overwrite
    protected void distributeItems() {
        List<Vec3d> deltas = null;
        final ObjectIterator<Int2ObjectMap.Entry<ItemStack>> iterator = Int2ObjectMaps.fastIterator(this.pending);
        while (iterator.hasNext()) {
            final Int2ObjectMap.Entry<ItemStack> entry = iterator.next();
            final int index = entry.getIntKey();
            if (!this.positions.containsKey(index)) {
                ejectItems();
                break;
            }
            final BlockPos targetPos = this.positions.get(index).blockPos();
            if (!this.world.isBlockLoaded(targetPos)) {
                continue;
            }
            final TileEntity tile = this.world.getTileEntity(targetPos);
            if (tile == null) {
                ejectItems();
                break;
            }
            final ItemStack stack = entry.getValue();
            final EnumFacing dir = this.positions.get(index).direction();
            final ItemStack stackRem = PackagedPatternTargets.insertIntoDirectTarget(this, tile, dir, stack, Actionable.MODULATE, PatternProviderInsertionMode.DEFAULT);
            if (PackagedPatternStacks.hasInsertedAny(stack, stackRem)) {
                final Vec3d delta = new Vec3d(targetPos.subtract(this.pos)).add(new Vec3d(dir.getDirectionVec()).scale(0.5));
                if (deltas == null) {
                    deltas = new ObjectArrayList<>();
                }
                deltas.add(delta);
            }
            if (stackRem.isEmpty()) {
                iterator.remove();
            } else {
                entry.setValue(stackRem);
            }
        }
        if (deltas != null && !deltas.isEmpty()) {
            final Vec3d source = new Vec3d(this.pos).add(0.5, 0.5, 0.5);
            PacketBeam.sendBeams(source, deltas, 0x00FFFF, 6, true, this.world.provider.getDimension(), 32);
            markDirty();
        }
    }

    @Shadow
    public abstract boolean isBusy();

    @Shadow
    protected abstract void ejectItems();
}
