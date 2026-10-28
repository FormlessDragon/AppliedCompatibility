package github.formlessdragon.appcompat.mixins.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import ae2.api.stacks.KeyCounter;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoNodeAccess;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternContainer;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDetails;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDisplay;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternInventory;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternStacks;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.block.BlockPackager;
import thelm.packagedauto.inventory.InventoryTileBase;
import thelm.packagedauto.tile.TilePackager;
import thelm.packagedauto.tile.TilePackagerExtension;

import java.util.List;

@Mixin(value = TilePackager.class, remap = false)
public abstract class MixinTilePackager extends MixinTileBase implements PackagedPatternContainer {

    @Unique
    private final PackagedPatternInventory appcompat$terminalPatterns =
        new PackagedPatternInventory(this::appcompat$buildPatterns, PackagedPatternDisplay::encode);

    @Override
    public PackagedPatternInventory getTerminalPatternInventory() {
        return this.appcompat$terminalPatterns;
    }

    @Shadow
    public List<IPackagePattern> patternList;
    @Shadow
    public IPackagePattern currentPattern;
    @Shadow
    public boolean lockPattern;

    @Shadow
    public abstract boolean canPushPattern();

    @Unique
    protected ItemStack getVisualItemStack() {
        return new ItemStack(BlockPackager.ITEM_INSTANCE);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return appcompat$mainNode() != null && appcompat$mainNode().isOnline()
            ? this.appcompat$terminalPatterns.getPatterns() : List.of();
    }

    @Unique
    private List<IPatternDetails> appcompat$buildPatterns() {
        final List<IPatternDetails> result = new ObjectArrayList<>();
        for (final IPackagePattern pattern : this.patternList) {
            result.add(PackagedPatternDetails.packagePattern(pattern));
        }
        return result;
    }

    @Override
    public boolean pushPattern(final IPatternDetails patternDetails, final KeyCounter[] inputHolder, final int multiplier) {
        if (multiplier != 1 || !appcompat$mainNode().isActive()
            || !(patternDetails instanceof PackagedPatternDetails pattern) || pattern.packagePattern() == null
            || !this.appcompat$terminalPatterns.containsCraftingPattern(pattern.getDefinition())) {
            return false;
        }
        final ItemStack outputStack = pattern.packagePattern().getOutput();
        if (canPushPattern()) {
            final ItemStack slotStack = this.getInventory().getStackInSlot(9);
            if (slotStack.isEmpty() || slotStack.isItemEqual(outputStack) && ItemStack.areItemStackShareTagsEqual(slotStack, outputStack) && slotStack.getCount() + 1 <= outputStack.getMaxStackSize()) {
                this.currentPattern = pattern.packagePattern();
                this.lockPattern = true;
                appcompat$fillInputs(this.getInventory(), inputHolder);
                return true;
            }
        }
        for (final BlockPos posP : BlockPos.getAllInBoxMutable(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            final TileEntity te = this.world.getTileEntity(posP);
            if (te instanceof TilePackagerExtension extension
                && extension.packager == (Object) this
                && extension.hostHelper.isActive()
                && this.appcompat$mainNode().getGrid() == ((PackagedAutoNodeAccess) extension).appcompat$mainNode().getGrid()
                && extension.canPushPattern()) {
                final ItemStack slotStack = extension.getInventory().getStackInSlot(9);
                if (slotStack.isEmpty() || slotStack.isItemEqual(outputStack) && ItemStack.areItemStackShareTagsEqual(slotStack, outputStack) && slotStack.getCount() + 1 <= outputStack.getMaxStackSize()) {
                    extension.currentPattern = pattern.packagePattern();
                    extension.lockPattern = true;
                    appcompat$fillInputs(extension.getInventory(), inputHolder);
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private void appcompat$fillInputs(final InventoryTileBase target, final KeyCounter[] inputHolder) {
        int slot = 0;
        for (final KeyCounter counter : inputHolder) {
            for (final Object2LongMap.Entry<AEKey> entry : counter) {
                if (slot < 9) {
                    target.setInventorySlotContents(slot++, PackagedPatternStacks.toItemStack(new GenericStack(entry.getKey(), entry.getLongValue())));
                }
            }
            counter.clear();
        }
        while (slot < 9) {
            target.setInventorySlotContents(slot++, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canMergePatternPush(final IPatternDetails patternDetails) {
        return false;
    }

    @Override
    public int getMaxPatternPushMultiplier(final IPatternDetails patternDetails, final int maxMultiplier) {
        return 0;
    }

    @Override
    public boolean isBusy() {
        if (canPushPattern()) {
            return false;
        }
        for (final BlockPos posP : BlockPos.getAllInBoxMutable(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            final TileEntity te = this.world.getTileEntity(posP);
            if (te instanceof TilePackagerExtension extension
                && extension.packager == (Object) this
                && this.appcompat$mainNode().getGrid() == ((PackagedAutoNodeAccess) extension).appcompat$mainNode().getGrid()
                && extension.canPushPattern()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE crafting provider
     */
    @Overwrite
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
    }
}
