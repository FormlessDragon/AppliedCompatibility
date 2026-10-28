package github.formlessdragon.appcompat.mixins.packagingprovider;

import ae2.api.config.Actionable;
import ae2.api.config.PatternProviderInsertionMode;
import ae2.api.config.PowerMultiplier;
import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.KeyCounter;
import ae2.me.helpers.ActionHostEnergySource;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedAutoActionHost;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternContainer;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDetails;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDisplay;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternInventory;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternKind;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternStacks;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternTargets;
import github.formlessdragon.appcompat.mixins.packagedauto.MixinTileBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import thelm.packagedauto.api.IPackageCraftingMachine;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.tile.TilePackager;
import thelm.packagedauto.tile.TileUnpackager;
import thelm.packagingprovider.block.BlockPackagingProvider;
import thelm.packagingprovider.tile.TilePackagingProvider;

import java.util.List;

@Mixin(value = TilePackagingProvider.class, remap = false)
public abstract class MixinTilePackagingProvider extends MixinTileBase implements PackagedPatternContainer {

    @Unique
    private final PackagedPatternInventory appcompat$terminalPatterns =
        new PackagedPatternInventory(this::appcompat$buildPatterns, PackagedPatternDisplay::encode);

    @Override
    public PackagedPatternInventory getTerminalPatternInventory() {
        return this.appcompat$terminalPatterns;
    }

    @Shadow
    public List<IRecipeInfo> recipeList;
    @Shadow
    public IPackagePattern currentPattern;
    @Shadow
    public List<ItemStack> toSend;
    @Shadow
    public EnumFacing sendDirection;
    @Shadow
    public boolean sendOrdered;
    @Shadow
    public boolean powered;
    @Shadow
    public boolean blocking;
    @Shadow
    public boolean provideDirect;
    @Shadow
    public boolean providePackaging;
    @Shadow
    public boolean provideUnpackaging;
    @Shadow
    public int roundRobinIndex;

    @Shadow
    protected abstract boolean validSendTarget(TileEntity tile, EnumFacing facing);

    @Unique
    protected ItemStack getVisualItemStack() {
        return new ItemStack(BlockPackagingProvider.ITEM_INSTANCE);
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return appcompat$mainNode() != null && appcompat$mainNode().isOnline()
            ? this.appcompat$terminalPatterns.getPatterns() : List.of();
    }

    @Unique
    private List<IPatternDetails> appcompat$buildPatterns() {
        final List<IPatternDetails> result = new ObjectArrayList<>();
        if (this.provideDirect) {
            for (final IRecipeInfo recipe : this.recipeList) {
                if (recipe.isCraftable()) {
                    result.add(PackagedPatternDetails.directPattern(recipe));
                }
            }
        }
        if (this.providePackaging) {
            for (final IRecipeInfo recipe : this.recipeList) {
                if (!recipe.isPackageable()) {
                    continue;
                }
                for (final IPackagePattern pattern : recipe.getPatterns()) {
                    result.add(PackagedPatternDetails.packagePattern(pattern));
                }
                for (final IPackagePattern pattern : recipe.getExtraPatterns()) {
                    result.add(PackagedPatternDetails.packagePattern(pattern));
                }
            }
        }
        if (this.provideUnpackaging) {
            for (final IRecipeInfo recipe : this.recipeList) {
                if (recipe.isCraftable()) {
                    result.add(PackagedPatternDetails.recipePattern(recipe));
                }
            }
        }
        return result;
    }

    @Override
    public boolean pushPattern(final IPatternDetails patternDetails, final KeyCounter[] inputHolder, final int multiplier) {
        if (multiplier != 1 || !appcompat$mainNode().isActive() || isBusy()
            || !(patternDetails instanceof PackagedPatternDetails pattern)
            || !this.appcompat$terminalPatterns.containsCraftingPattern(pattern.getDefinition())) {
            return false;
        }
        if (pattern.kind() == PackagedPatternKind.PACKAGE) {
            final double request = TilePackager.energyReq * 2.0D;
            if (!appcompat$extractPower(request, Actionable.SIMULATE)) {
                return false;
            }
            appcompat$extractPower(request, Actionable.MODULATE);
            this.currentPattern = pattern.packagePattern();
            return true;
        }
        final IRecipeInfo recipe = pattern.recipe();
        final double request = TilePackager.energyReq * 2.0D + TileUnpackager.energyUsage;
        if (!appcompat$extractPower(request, Actionable.SIMULATE)) {
            return false;
        }
        if (recipe.getRecipeType().hasMachine()) {
            for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
                final EnumFacing facing = EnumFacing.VALUES[(sideIndex + this.roundRobinIndex) % 6];
                final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
                if (tile instanceof IPackageCraftingMachine machine && !machine.isBusy() && machine.acceptPackage(recipe, appcompat$copyStacks(recipe.getInputs()), facing.getOpposite(), this.blocking)) {
                    appcompat$extractPower(request, Actionable.MODULATE);
                    this.roundRobinIndex = (this.roundRobinIndex + 1) % 6;
                    return true;
                }
            }
            return false;
        }
        final List<ItemStack> stacks = appcompat$copyStacks(recipe.getInputs());
        for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            final EnumFacing facing = EnumFacing.VALUES[(sideIndex + this.roundRobinIndex) % 6];
            final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
            if (!validSendTarget(tile, facing.getOpposite())) {
                continue;
            }
            if (tile == null || this.blocking && PackagedPatternTargets.containsAnyStack(this, this, facing)) {
                continue;
            }
            boolean acceptsAll = true;
            for (final ItemStack stack : stacks) {
                final ItemStack stackRem = PackagedPatternTargets.insertIntoTarget(this, this, facing, stack, Actionable.SIMULATE, PatternProviderInsertionMode.DEFAULT);
                acceptsAll &= PackagedPatternStacks.hasInsertedAny(stack, stackRem);
            }
            if (acceptsAll) {
                appcompat$extractPower(request, Actionable.MODULATE);
                this.sendDirection = facing;
                this.toSend.addAll(stacks);
                this.sendOrdered = recipe.getRecipeType().isOrdered();
                this.roundRobinIndex = (this.roundRobinIndex + 1) % 6;
                sendUnpackaging();
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean appcompat$extractPower(final double request, final Actionable mode) {
        final ActionHostEnergySource energy = new ActionHostEnergySource(new PackagedAutoActionHost(this));
        return request - energy.extractAEPower(request, mode, PowerMultiplier.CONFIG) <= 0.0001D;
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
        return this.powered || this.currentPattern != null || !this.toSend.isEmpty();
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE storage insert
     */
    @Overwrite
    protected void sendPackaging() {
        if (this.currentPattern == null || !appcompat$mainNode().isActive()) {
            return;
        }
        final ItemStack remaining = PackagedPatternTargets.insertIntoNetwork(this, this.currentPattern.getOutput());
        if (remaining.isEmpty()) {
            this.currentPattern = null;
        }
    }

    /**
     * @author circulation
     * @reason 覆写到新 AE storage insert
     */
    @Overwrite
    protected void sendUnpackaging() {
        if (this.toSend.isEmpty()) {
            return;
        }
        if (this.sendDirection != null) {
            final TileEntity tile = this.world.getTileEntity(this.pos.offset(this.sendDirection));
            if (!validSendTarget(tile, this.sendDirection.getOpposite())) {
                this.sendDirection = null;
                return;
            }
            this.toSend.replaceAll(itemStack -> PackagedPatternTargets.insertIntoTarget(this, this, this.sendDirection, itemStack, Actionable.MODULATE, PatternProviderInsertionMode.DEFAULT));
            this.toSend.removeIf(ItemStack::isEmpty);
            markDirty();
            return;
        }
        if (!appcompat$mainNode().isActive()) {
            return;
        }
        for (int i = 0; i < this.toSend.size(); ++i) {
            final ItemStack itemStack = this.toSend.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            this.toSend.set(i, PackagedPatternTargets.insertIntoNetwork(this, itemStack));
        }
        this.toSend.removeIf(ItemStack::isEmpty);
        markDirty();
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
}
