package github.formlessdragon.appcompat.common.container.mmce;

import ae2.api.inventories.InternalInventory;
import ae2.api.inventories.PlatformInventoryWrapper;
import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEKeyType;
import ae2.api.stacks.GenericStack;
import ae2.container.AEBaseContainer;
import ae2.container.SlotSemantics;
import ae2.container.guisync.GuiSync;
import ae2.container.slot.FakeSlot;
import ae2.core.definitions.AEItems;
import ae2.util.ConfigInventory;
import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import github.kasuminova.mmce.common.tile.MEFluidInputBus;
import github.kasuminova.mmce.common.util.AEFluidInventoryUpgradeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Collections;
import java.util.Objects;

public class ContainerMEFluidInputBus extends AEBaseContainer {

    private final MEFluidInputBus owner;
    private final MirroredMachineUpgradeInventory upgrades;
    private final ConfigInventory configInventory;
    @GuiSync(40)
    public SyncedFluidList tankFluids = new SyncedFluidList(Collections.emptyList());
    @GuiSync(41)
    public SyncedFluidList configFluids = new SyncedFluidList(Collections.emptyList());
    @GuiSync(42)
    public int capacity = 8000;

    public ContainerMEFluidInputBus(final MEFluidInputBus owner, final EntityPlayer player) {
        super(player.inventory, owner);
        this.owner = owner;
        this.configInventory = ConfigInventory.configStacks(9)
                                              .supportedType(AEKeyType.fluids())
                                              .allowOverstacking(true)
                                              .changeListener(this::pushConfigToOwner)
                                              .build();

        for (int i = 0; i < 9; i++) {
            this.addSlot(new FluidConfigSlot(this.configInventory.createGuiWrapper(), i, 8 + 18 * i, 35), SlotSemantics.CONFIG);
        }

        final PlatformInventoryWrapper upgradesBacking = new PlatformInventoryWrapper(owner.getInventoryByName("upgrades"));
        this.upgrades = new MirroredMachineUpgradeInventory(owner.getVisualItemStack().getItem(), 5, upgradesBacking);
        this.setupUpgrades(this.upgrades);
        for (int i = 0; i < 5; i++) {
            this.getSlot(i + 9).xPos = 187;
            this.getSlot(i + 9).yPos = 8 + 18 * i;
        }

        this.addPlayerInventorySlots(8, 149);

        this.registerClientAction("setConfigFluid", String.class, this::setConfigFluid);
    }

    private static int normalizeFluidAmount(final long amount) {
        if (amount < 1000) {
            return 1000;
        }
        if (amount > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) amount;
    }

    public void sendSetConfigFluid(final int slotNumber, final Fluid fluid) {
        final String fluidName = fluid == null ? "" : FluidRegistry.getFluidName(fluid);
        if (fluidName == null) {
            return;
        }
        sendClientAction("setConfigFluid", slotNumber + ":" + fluidName);
    }

    private void setConfigFluid(final String payload) {
        final int sep = payload.indexOf(':');
        if (sep < 0) {
            return;
        }
        final int slot;
        try {
            slot = Integer.parseInt(payload.substring(0, sep));
        } catch (final NumberFormatException e) {
            return;
        }
        final String fluidName = payload.substring(sep + 1);
        final IAEFluidTank config = this.owner.getConfig();
        if (config == null || slot < 0 || slot >= config.getSlots() || slot >= this.inventorySlots.size()
            || !(this.inventorySlots.get(slot) instanceof FluidConfigSlot)) {
            return;
        }
        if (fluidName.isEmpty()) {
            config.setFluidInSlot(slot, null);
            this.configInventory.setStack(slot, null);
            return;
        }
        final Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            return;
        }
        final FluidStack fluidStack = new FluidStack(fluid, 1000);
        config.setFluidInSlot(slot, AEFluidStack.fromFluidStack(fluidStack));
        this.configInventory.setStack(slot, new GenericStack(Objects.requireNonNull(AEFluidKey.of(fluid)), 1));
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            if (this.upgrades != null) {
                this.upgrades.pullFromBacking();
            }
            refreshCapacity();
            this.tankFluids = SyncedFluidList.from(this.owner.getTanks());
            this.configFluids = SyncedFluidList.from(this.owner.getConfig());
            pullConfigFromOwner();
        }
        super.broadcastChanges();
    }

    public MEFluidInputBus getOwner() {
        return this.owner;
    }

    private void pushConfigToOwner() {
        final IAEFluidTank config = this.owner.getConfig();
        if (config == null) {
            return;
        }
        for (int i = 0; i < this.configInventory.size(); i++) {
            final GenericStack stack = this.configInventory.getStack(i);
            if (stack == null || !(stack.what() instanceof AEFluidKey fluidKey)) {
                config.setFluidInSlot(i, null);
                continue;
            }
            final FluidStack fluidStack = fluidKey.toStack(1000);
            config.setFluidInSlot(i, AEFluidStack.fromFluidStack(fluidStack));
        }
    }

    private void pullConfigFromOwner() {
        final IAEFluidTank config = this.owner.getConfig();
        if (config == null) {
            return;
        }
        for (int i = 0; i < config.getSlots(); i++) {
            final IAEFluidStack stack = config.getFluidInSlot(i);
            if (stack instanceof AEFluidStack carrier && carrier.getKey() != null && carrier.getStackSize() > 0) {
                this.configInventory.setStack(i, new GenericStack(carrier.getKey(), 1));
            } else {
                this.configInventory.setStack(i, null);
            }
        }
    }

    private void refreshCapacity() {
        final int installed = this.upgrades == null ? 0 : this.upgrades.getInstalledUpgrades(AEItems.CAPACITY_CARD.item());
        this.capacity = (int) (Math.pow(4, installed + 1) * 2000);
        if (this.owner.getTanks() instanceof AEFluidInventoryUpgradeable upgradeable) {
            upgradeable.setCapacity(this.capacity);
        }
    }

    public static final class FluidConfigSlot extends FakeSlot {

        private FluidConfigSlot(final InternalInventory inventory, final int slotIndex, final int x, final int y) {
            super(inventory, slotIndex, x, y);
            setNotDraggable();
        }

        public static boolean isFluidFilterStack(final ItemStack stack) {
            return toFluidFilterStack(stack).isEmpty() == stack.isEmpty();
        }

        private static ItemStack toFluidFilterStack(final ItemStack stack) {
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
            if (genericStack != null) {
                if (genericStack.what() instanceof AEFluidKey) {
                    return GenericStack.wrapInItemStack(new GenericStack(genericStack.what(), 1));
                }
                return ItemStack.EMPTY;
            }
            final ItemStack single = stack.copy();
            single.setCount(1);
            final FluidStack contained = FluidUtil.getFluidContained(single);
            if (contained == null) {
                return ItemStack.EMPTY;
            }
            final AEFluidKey fluidKey = AEFluidKey.of(contained);
            if (fluidKey == null) {
                return ItemStack.EMPTY;
            }
            return GenericStack.wrapInItemStack(new GenericStack(fluidKey, 1));
        }

        @Override
        public boolean canSetFilterTo(final ItemStack stack) {
            if (stack.isEmpty()) {
                return super.canSetFilterTo(stack);
            }
            final ItemStack filterStack = toFluidFilterStack(stack);
            return !filterStack.isEmpty() && super.canSetFilterTo(filterStack);
        }

        @Override
        public void putStack(final ItemStack stack) {
            if (stack.isEmpty()) {
                super.putStack(ItemStack.EMPTY);
                return;
            }
            final ItemStack filterStack = toFluidFilterStack(stack);
            if (!filterStack.isEmpty()) {
                super.putStack(filterStack);
            }
        }

        @Override
        public void increase(final ItemStack stack) {
            putStack(stack);
        }

        @Override
        public void decrease(final ItemStack stack) {
            if (stack.isEmpty()) {
                putStack(ItemStack.EMPTY);
            } else {
                putStack(stack);
            }
        }

        @Override
        public void setGenericFilter(final GenericStack stack) {
            if (stack != null && stack.what() instanceof AEFluidKey) {
                putStack(GenericStack.wrapInItemStack(new GenericStack(stack.what(), 1)));
            }
        }
    }
}
