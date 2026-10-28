package github.formlessdragon.appcompat.mixins.packagedauto;

import ae2.api.config.Actionable;
import ae2.api.config.PatternProviderInsertionMode;
import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.KeyCounter;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternContainer;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDetails;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDisplay;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternInventory;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternKind;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternStacks;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternTargets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.block.BlockUnpackager;
import thelm.packagedauto.tile.TileUnpackager;

import java.util.List;

@Mixin(value = TileUnpackager.class, remap = false)
public abstract class MixinTileUnpackager extends MixinTileBase implements PackagedPatternContainer {

    @Unique
    private final PackagedPatternInventory appcompat$terminalPatterns =
        new PackagedPatternInventory(this::appcompat$buildPatterns, PackagedPatternDisplay::encode);

    @Override
    public PackagedPatternInventory getTerminalPatternInventory() {
        return this.appcompat$terminalPatterns;
    }

    @Shadow
    public static int energyUsage;
    @Shadow
    public List<IRecipeInfo> recipeList;
    @Final
    @Shadow
    public TileUnpackager.PackageTracker[] trackers;
    @Shadow
    public int trackerCount;
    @Shadow
    public boolean powered;
    @Shadow
    public boolean blocking;
    @Shadow
    public int roundRobinIndex;

    @Shadow
    protected abstract boolean validSendTarget(TileEntity tile, EnumFacing facing);

    @Unique
    protected ItemStack getVisualItemStack() {
        return new ItemStack(BlockUnpackager.ITEM_INSTANCE);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return appcompat$mainNode() != null && appcompat$mainNode().isOnline()
            ? this.appcompat$terminalPatterns.getPatterns() : List.of();
    }

    @Unique
    private List<IPatternDetails> appcompat$buildPatterns() {
        final List<IPatternDetails> result = new ObjectArrayList<>();
        for (final IRecipeInfo recipe : this.recipeList) {
            if (!recipe.getOutputs().isEmpty()) {
                result.add(PackagedPatternDetails.recipePattern(recipe));
            }
        }
        return result;
    }

    @Override
    public boolean pushPattern(final IPatternDetails patternDetails, final KeyCounter[] inputHolder, final int multiplier) {
        if (multiplier != 1 || !appcompat$mainNode().isActive() || isBusy()
            || !(patternDetails instanceof PackagedPatternDetails pattern) || pattern.kind() != PackagedPatternKind.RECIPE
            || !this.appcompat$terminalPatterns.containsCraftingPattern(pattern.getDefinition())) {
            return false;
        }
        final int energyReq = energyUsage * pattern.recipe().getPatterns().size();
        if (getEnergyStorage().getEnergyStored() < energyReq) {
            return false;
        }
        final TileUnpackager.PackageTracker tracker = appcompat$findEmptyTracker();
        if (tracker == null) {
            return false;
        }
        tracker.fillRecipe(pattern.recipe());
        getEnergyStorage().extractEnergy(energyReq, false);
        for (final KeyCounter counter : inputHolder) {
            counter.clear();
        }
        return true;
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
        return appcompat$findEmptyTracker() == null;
    }

    /**
     * @author circulation
     * @reason 覆写外部输出到新 AE target 插入
     */
    @Overwrite
    protected void emptyTrackers() {
        for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            final EnumFacing facing = EnumFacing.VALUES[(sideIndex + this.roundRobinIndex) % 6];
            final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
            if (tile instanceof IPackageCraftingMachine machine) {
                for (final TileUnpackager.PackageTracker tracker : this.trackers) {
                    if (tracker.isFilled() && tracker.recipe != null && tracker.recipe.getRecipeType().hasMachine()) {
                        if (!machine.isBusy() && machine.acceptPackage(tracker.recipe, appcompat$copyStacks(tracker.recipe.getInputs()), facing.getOpposite(), this.blocking)) {
                            tracker.clearRecipe();
                            this.roundRobinIndex = (this.roundRobinIndex + 1) % 6;
                            markDirty();
                            break;
                        }
                    }
                }
            }
        }
        if (!this.powered) {
            for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
                final EnumFacing facing = EnumFacing.VALUES[(sideIndex + this.roundRobinIndex) % 6];
                final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
                if (!validSendTarget(tile, facing.getOpposite())) {
                    continue;
                }
                if (tile == null || this.blocking && PackagedPatternTargets.containsAnyStack(this, this, facing)) {
                    continue;
                }
                final TileUnpackager.PackageTracker trackerToEmpty = appcompat$findUnassignedNonMachineTracker();
                if (trackerToEmpty == null) {
                    continue;
                }
                if (trackerToEmpty.toSend.isEmpty()) {
                    trackerToEmpty.setupToSend();
                }
                boolean acceptsAll = true;
                for (final ItemStack stack : trackerToEmpty.toSend) {
                    final ItemStack stackRem = PackagedPatternTargets.insertIntoTarget(this, this, facing, stack, Actionable.SIMULATE, PatternProviderInsertionMode.DEFAULT);
                    acceptsAll &= PackagedPatternStacks.hasInsertedAny(stack, stackRem);
                }
                trackerToEmpty.toSend.removeIf(ItemStack::isEmpty);
                if (acceptsAll) {
                    trackerToEmpty.facing = facing;
                    this.roundRobinIndex = (this.roundRobinIndex + 1) % 6;
                }
                markDirty();
            }
        }
        for (final EnumFacing facing : EnumFacing.VALUES) {
            final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
            final TileUnpackager.PackageTracker trackerToEmpty = appcompat$findTrackerFacing(facing);
            if (trackerToEmpty == null) {
                continue;
            }
            if (trackerToEmpty.toSend.isEmpty()) {
                trackerToEmpty.setupToSend();
            }
            if (!validSendTarget(tile, facing.getOpposite())) {
                trackerToEmpty.facing = null;
                continue;
            }
            trackerToEmpty.toSend.replaceAll(itemStack -> PackagedPatternTargets.insertIntoTarget(this, this, facing, itemStack, Actionable.MODULATE, PatternProviderInsertionMode.DEFAULT));
            trackerToEmpty.toSend.removeIf(ItemStack::isEmpty);
            if (trackerToEmpty.toSend.isEmpty()) {
                trackerToEmpty.clearRecipe();
            }
            markDirty();
        }
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE crafting provider
     */
    @Overwrite
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
    }

    @Unique
    private List<ItemStack> appcompat$copyStacks(final List<ItemStack> stacks) {
        final List<ItemStack> result = new ObjectArrayList<>();
        for (final ItemStack stack : stacks) {
            result.add(stack.copy());
        }
        return result;
    }

    @Unique
    private TileUnpackager.PackageTracker appcompat$findEmptyTracker() {
        for (int i = 0; i < this.trackerCount; i++) {
            final TileUnpackager.PackageTracker tracker = this.trackers[i];
            if (tracker.isEmpty()) {
                return tracker;
            }
        }
        return null;
    }

    @Unique
    private TileUnpackager.PackageTracker appcompat$findUnassignedNonMachineTracker() {
        for (final TileUnpackager.PackageTracker tracker : this.trackers) {
            if (tracker.isFilled() && tracker.facing == null && tracker.recipe != null && !tracker.recipe.getRecipeType().hasMachine()) {
                return tracker;
            }
        }
        return null;
    }

    @Unique
    private TileUnpackager.PackageTracker appcompat$findTrackerFacing(final EnumFacing facing) {
        for (final TileUnpackager.PackageTracker tracker : this.trackers) {
            if (tracker.facing == facing) {
                return tracker;
            }
        }
        return null;
    }
}
