package github.formlessdragon.appcompat.mixins.mmce;

import ae2.api.crafting.IPatternDetails;
import ae2.api.crafting.PatternDetailsHelper;
import ae2.api.implementations.blockentities.PatternContainerGroup;
import ae2.api.inventories.InternalInventory;
import ae2.api.inventories.PlatformInventoryWrapper;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNodeListener;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.crafting.ICraftingProvider;
import ae2.api.networking.security.IActionSource;
import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.KeyCounter;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.api.util.ICustomName;
import ae2.helpers.patternprovider.PatternContainer;
import ae2.me.helpers.ActionHostEnergySource;
import ae2.text.TextComponentItemStack;
import ae2.util.inv.SupplierInternalInventory;
import appeng.api.networking.events.MENetworkEvent;
import appeng.tile.inventory.AppEngInternalInventory;
import github.formlessdragon.appcompat.bridge.mmce.PatternProviderCacheExtensions;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.machine.MachineComponent;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "SynchronizeOnNonFinalField", "deprecation"})
@Mixin(value = MEPatternProvider.class, remap = false, priority = 999)
public abstract class MixinMEPatternProvider extends MixinMEMachineComponent implements ICraftingProvider, PatternContainer, ICustomName {

    @Unique
    private final InternalInventory appcompat$terminalPatternInventory = new SupplierInternalInventory<>(
        () -> new PlatformInventoryWrapper(getPatterns()));
    @Unique
    private final IPatternDetails[] appcompat$details = new IPatternDetails[MEPatternProvider.PATTERNS];
    @Unique
    private final ItemStack[] appcompat$decodedFrom = new ItemStack[MEPatternProvider.PATTERNS];
    @Shadow
    public boolean handlerDirty;
    @Shadow
    protected boolean shouldReturnItems;
    @Shadow
    protected volatile boolean machineCompleted;
    @Shadow
    @Final
    protected InfItemFluidHandler handler;
    @Shadow
    @Final
    protected List<MachineComponent<?>> combinationComponents;
    @Shadow
    private String machineName;
    @Unique
    private boolean ac_v;

    @Shadow
    public abstract AppEngInternalInventory getPatterns();

    @Shadow
    public abstract InfItemFluidHandler getInfHandler();

    @Shadow
    public abstract boolean isBusy();

    @Shadow
    public abstract void markChunkDirty();

    @Shadow
    public abstract boolean hasCustomInventoryName();

    @Shadow
    public abstract String getCustomInventoryName();

    @Override
    public boolean isVisibleInTerminal() {
        return ac_v;
    }

    @Override
    public boolean canModifyTerminalVisibility() {
        return true;
    }

    @Override
    public void setTerminalVisibility(boolean visible) {
        ac_v = visible;
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        final IManagedGridNode node = this.getMainNode();
        final List<IPatternDetails> result = new ArrayList<>();
        if (node == null || !node.isActive()) {
            return result;
        }
        final AppEngInternalInventory patterns = getPatterns();
        for (int slot = 0; slot < patterns.getSlots(); slot++) {
            final ItemStack stack = patterns.getStackInSlot(slot);
            final IPatternDetails detail = appcompat$decode(slot, stack);
            if (detail != null) {
                result.add(detail);
            }
        }
        return result;
    }

    @Unique
    private IPatternDetails appcompat$decode(final int slot, final ItemStack stack) {
        final ItemStack cached = this.appcompat$decodedFrom[slot];
        if (stack.isEmpty()) {
            this.appcompat$decodedFrom[slot] = ItemStack.EMPTY;
            this.appcompat$details[slot] = null;
            return null;
        }
        if (cached != null && ItemStack.areItemStacksEqual(cached, stack) && this.appcompat$details[slot] != null) {
            return this.appcompat$details[slot];
        }
        final AEItemKey key = AEItemKey.of(stack);
        final IPatternDetails detail = key == null ? null : PatternDetailsHelper.decodePattern(key, getWorld());
        this.appcompat$decodedFrom[slot] = stack.copy();
        this.appcompat$details[slot] = detail;
        return detail;
    }

    @Override
    public void onMainNodeStateChanged(final IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        appcompat$requestPatternUpdate();
    }

    @Unique
    private void appcompat$clearPatternCache() {
        Arrays.fill(this.appcompat$decodedFrom, null);
        Arrays.fill(this.appcompat$details, null);
    }

    @Override
    public boolean pushPattern(final IPatternDetails patternDetails, final KeyCounter[] inputHolder, final int multiplier) {
        if (!canMergePatternPush(patternDetails)) {
            return false;
        }
        final InfItemFluidHandler handler = getInfHandler();
        if (handler == null) {
            return false;
        }
        for (final KeyCounter counter : inputHolder) {
            for (final Object2LongMap.Entry<AEKey> entry : counter) {
                final AEKey what = entry.getKey();
                final long amount = entry.getLongValue();
                if (amount <= 0) {
                    continue;
                }
                appcompat$pushKey(handler, what, amount);
            }
            counter.clear();
        }
        return true;
    }

    @Unique
    private void appcompat$pushKey(final InfItemFluidHandler handler, final AEKey what, final long amount) {
        if (what instanceof AEItemKey itemKey) {
            long remaining = amount;
            final int maxStack = Math.max(1, itemKey.toStack(1).getMaxStackSize());
            while (remaining > 0) {
                final int batch = (int) Math.min(remaining, maxStack);
                final ItemStack stack = itemKey.toStack(batch);
                handler.appendItem(stack);
                remaining -= batch;
            }
        } else if (what instanceof AEFluidKey fluidKey) {
            long remaining = amount;
            while (remaining > 0) {
                final int batch = (int) Math.min(remaining, Integer.MAX_VALUE);
                final FluidStack fluidStack = fluidKey.toStack(batch);
                final int filled = handler.fill(fluidStack, true);
                if (filled <= 0) {
                    break;
                }
                remaining -= filled;
            }
        } else {
            PatternProviderCacheExtensions.push(handler, what, amount);
        }
    }

    @Override
    public boolean canMergePatternPush(final IPatternDetails patternDetails) {
        if (patternDetails == null || isBusy()) {
            return false;
        }
        final IManagedGridNode node = this.getMainNode();
        return node != null && node.isActive() && node.isPowered();
    }

    @Override
    public int getMaxPatternPushMultiplier(final IPatternDetails patternDetails, final int maxMultiplier) {
        if (!canMergePatternPush(patternDetails) || maxMultiplier <= 0) {
            return 0;
        }
        return maxMultiplier;
    }

    @Unique
    private void appcompat$requestPatternUpdate() {
        final IManagedGridNode node = this.getMainNode();
        if (node != null && node.getNode() != null && node.isReady() && node.hasGridBooted()) {
            ICraftingProvider.requestUpdate(node);
        }
    }

    @Override
    public IGrid getGrid() {
        final IManagedGridNode node = this.getMainNode();
        return node == null ? null : node.getGrid();
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        return this.appcompat$terminalPatternInventory;
    }

    @Override
    public boolean containsPattern(final AEItemKey pattern) {
        if (pattern == null) {
            return false;
        }
        final AppEngInternalInventory patterns = getPatterns();
        for (int slot = 0; slot < patterns.getSlots(); slot++) {
            final AEItemKey key = AEItemKey.of(patterns.getStackInSlot(slot));
            if (pattern.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getTerminalSortOrder() {
        return ((long) getPos().getZ() << 24) ^ ((long) getPos().getX() << 8) ^ getPos().getY();
    }

    @Override
    public void openTerminalPatternContainerGui(final EntityPlayer player) {
        player.openGui(ModularMachinery.MODID, CommonProxy.GuiType.ME_PATTERN_PROVIDER.ordinal(), player.world, getPos().getX(), getPos().getY(), getPos().getZ());
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        final ItemStack iconStack = getVisualItemStack();
        final AEItemKey icon = AEItemKey.of(iconStack);
        final ITextComponent name;
        if (hasCustomName()) {
            name = new TextComponentString(getCustomName());
        } else if (machineName != null) {
            if (I18n.canTranslate(getMachineName())) {
                name = new TextComponentTranslation(getMachineName());
            } else {
                name = new TextComponentString(getMachineName());
            }
        } else {
            name = TextComponentItemStack.of(iconStack);
        }
        return new PatternContainerGroup(icon, name, Collections.emptyList());
    }

    /**
     * @author circulation
     * @reason 覆写到新实现
     */
    @Overwrite
    private void returnItems() {
        final IManagedGridNode node = this.getMainNode();
        if (!this.shouldReturnItems || node == null || !node.isActive() || !node.isPowered()) {
            return;
        }
        this.shouldReturnItems = false;
        this.machineCompleted = true;
        synchronized (this.handler) {
            returnItem(this.handler);
        }
        for (final MachineComponent<?> component : this.combinationComponents) {
            final Object provider = component.getContainerProvider();
            if (!(provider instanceof InfItemFluidHandler infHandler)) {
                throw new IllegalStateException("Unexpected pattern provider cache type: " + provider);
            }
            synchronized (infHandler) {
                returnItem(infHandler);
            }
        }
        this.handlerDirty = true;
        markChunkDirty();
    }

    /**
     * @author circulation
     * @reason 覆写到新实现
     */
    @Overwrite
    private void returnItem(final InfItemFluidHandler infHandler) {
        final IManagedGridNode node = this.getMainNode();
        final IGrid grid = node == null ? null : node.getGrid();
        if (grid == null) {
            return;
        }
        final MEStorage net = grid.getStorageService().getInventory();
        final ActionHostEnergySource energy = new ActionHostEnergySource(this);
        final IActionSource source = IActionSource.ofMachine(this);

        final List<ItemStack> itemStacks = infHandler.getItemStackList();
        for (int i = 0; i < itemStacks.size(); i++) {
            final ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            final AEItemKey key = AEItemKey.of(stack);
            if (key == null) {
                continue;
            }
            final long inserted = StorageHelper.poweredInsert(energy, net, key, stack.getCount(), source);
            final int remaining = stack.getCount() - (int) inserted;
            itemStacks.set(i, remaining <= 0 ? ItemStack.EMPTY : key.toStack(remaining));
        }

        final List<FluidStack> fluidStacks = infHandler.getFluidStackList();
        for (int i = 0; i < fluidStacks.size(); i++) {
            final FluidStack stack = fluidStacks.get(i);
            if (stack == null) {
                continue;
            }
            final AEFluidKey key = AEFluidKey.of(stack);
            if (key == null) {
                continue;
            }
            final long inserted = StorageHelper.poweredInsert(energy, net, key, stack.amount, source);
            final long remaining = stack.amount - inserted;
            fluidStacks.set(i, remaining <= 0 ? null : key.toStack((int) Math.min(remaining, Integer.MAX_VALUE)));
        }

        PatternProviderCacheExtensions.returnStacks(grid, this, infHandler);
    }

    @Redirect(
        method = {"notifyNeighbors", "refreshPatterns", "onChangeInventory"},
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/IGrid;postEvent(Lappeng/api/networking/events/MENetworkEvent;)Lappeng/api/networking/events/MENetworkEvent;",
            remap = false
        ),
        require = 0
    )
    private MENetworkEvent appcompat$redirectPostEvent(final appeng.api.networking.IGrid instance, final MENetworkEvent meNetworkEvent) {
        appcompat$requestPatternUpdate();
        return meNetworkEvent;
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void appcompat$readPatternNBT(final NBTTagCompound compound, final CallbackInfo ci) {
        appcompat$clearPatternCache();
        appcompat$requestPatternUpdate();
    }

    @Inject(method = "validate", at = @At("TAIL"))
    private void appcompat$validatePatternProvider(final CallbackInfo ci) {
        appcompat$requestPatternUpdate();
    }

    public boolean hasCustomName() {
        return hasCustomInventoryName();
    }

    @Nullable
    public String getCustomName() {
        return getCustomInventoryName();
    }

    @Shadow
    public abstract void setCustomName(@Nullable String customName);

    @Shadow
    public abstract void saveChanges();

    @Shadow
    public abstract String getMachineName();

    @Override
    public boolean canEditTerminalName() {
        return true;
    }

    @Override
    public void setTerminalCustomName(@Nullable String name) {
        setCustomName(name);
        saveChanges();
    }
}
