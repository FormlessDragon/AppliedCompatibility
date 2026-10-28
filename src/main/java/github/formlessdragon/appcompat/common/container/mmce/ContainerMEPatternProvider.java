package github.formlessdragon.appcompat.common.container.mmce;

import github.formlessdragon.appcompat.bridge.mmce.MMCEEnums;
import ae2.api.inventories.PlatformInventoryWrapper;
import ae2.api.stacks.GenericStack;
import ae2.container.AEBaseContainer;
import ae2.container.SlotSemantics;
import ae2.container.guisync.GuiSync;
import ae2.container.guisync.PacketWritable;
import ae2.container.slot.AppEngSlot;
import ae2.container.slot.RestrictedInputSlot;
import appeng.fluids.util.AEFluidStack;
import github.formlessdragon.appcompat.bridge.mmce.PatternProviderCacheExtensions;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import github.kasuminova.mmce.common.util.AEFluidInventoryUpgradeable;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import hellfirepvp.modularmachinery.common.machine.MachineComponent;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Collections;
import java.util.List;

public class ContainerMEPatternProvider extends AEBaseContainer {

    private final MEPatternProvider owner;

    @GuiSync(40)
    public int workMode;

    @GuiSync(41)
    public SyncedFluidList subFluid = new SyncedFluidList(Collections.emptyList());

    @GuiSync(42)
    public PatternStackList stackList = new PatternStackList(Collections.emptyList());

    public ContainerMEPatternProvider(final MEPatternProvider owner, final EntityPlayer player) {
        super(player.inventory, owner);
        this.owner = owner;

        final PlatformInventoryWrapper patterns = new PlatformInventoryWrapper(owner.getPatterns());
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patterns, (row * 9) + col, 8 + (col * 18), 28 + (row * 18)), SlotSemantics.ENCODED_PATTERN);
            }
        }

        final PlatformInventoryWrapper subItemHandler = new PlatformInventoryWrapper(owner.getSubItemHandler());
        for (int i = 0; i < 2; i++) {
            this.addSlot(new AppEngSlot(subItemHandler, i, 181 + (i * 18), 172), SlotSemantics.STORAGE);
        }

        this.addPlayerInventorySlots(8, 114);

        this.registerClientAction("cycleWorkMode", this::cycleWorkMode);
        this.registerClientAction("returnItems", this::returnItems);
        this.registerClientAction("fluidAction", String.class, this::fluidAction);

        refreshSyncedState();
    }

    private static void emptyHeldContainerIntoTank(final EntityPlayer player, final AEFluidInventoryUpgradeable tank) {
        final ItemStack held = player.inventory.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        final ItemStack single = held.copy();
        single.setCount(1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(single);
        if (handler == null) {
            return;
        }
        final int capacity = tank.getTankProperties()[0].getCapacity();
        final FluidStack maxCanDrain = handler.drain(capacity, false);
        if (maxCanDrain == null || maxCanDrain.amount <= 0) {
            return;
        }
        final int accepted = tank.fill(maxCanDrain, false);
        if (accepted <= 0) {
            return;
        }
        final FluidStack drained = handler.drain(accepted, true);
        if (drained == null || drained.amount <= 0) {
            return;
        }
        tank.fill(drained, true);
        handleConvertedContainer(player, handler.getContainer());
    }

    private static void fillHeldContainerFromTank(final EntityPlayer player, final AEFluidInventoryUpgradeable tank) {
        final ItemStack held = player.inventory.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        final AEFluidStack stored = (AEFluidStack) tank.getFluidInSlot(0);
        if (stored == null || stored.getFluidStack() == null || stored.getStackSize() <= 0) {
            return;
        }
        final ItemStack single = held.copy();
        single.setCount(1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(single);
        if (handler == null) {
            return;
        }
        final FluidStack request = stored.copy().getFluidStack();
        request.amount = Integer.MAX_VALUE;
        final int amountAllowed = handler.fill(request, false);
        if (amountAllowed <= 0) {
            return;
        }
        final FluidStack extractable = tank.drain(request.copy(), false);
        if (extractable == null || extractable.amount <= 0) {
            return;
        }
        final int maxCanFill = handler.fill(extractable, false);
        if (maxCanFill <= 0) {
            return;
        }
        final FluidStack extracted = tank.drain(maxCanFill, true);
        if (extracted == null || extracted.amount <= 0) {
            return;
        }
        handler.fill(extracted, true);
        handleConvertedContainer(player, handler.getContainer());
    }

    private static void handleConvertedContainer(final EntityPlayer player, final ItemStack converted) {
        final ItemStack held = player.inventory.getItemStack();
        if (held.getCount() == 1) {
            player.inventory.setItemStack(converted.copy());
            return;
        }
        held.shrink(1);
        player.inventory.setItemStack(held);
        if (converted.isEmpty()) {
            return;
        }
        final ItemStack remainder = converted.copy();
        if (!player.inventory.addItemStackToInventory(remainder)) {
            player.dropItem(remainder, false);
        }
    }

    private static void appendHandlerStacks(final List<GenericStack> stacks, final InfItemFluidHandler handler) {
        for (final ItemStack stack : handler.getItemStackList()) {
            if (!stack.isEmpty()) {
                stacks.add(GenericStack.fromItemStack(stack));
            }
        }
        for (final FluidStack stack : handler.getFluidStackList()) {
            if (stack != null) {
                stacks.add(GenericStack.fromFluidStack(stack));
            }
        }
        PatternProviderCacheExtensions.appendStacks(stacks, handler);
    }

    private void cycleWorkMode() {
        this.owner.setWorkMode(MMCEEnums.nextWorkMode(this.owner.getWorkMode()));
    }

    private void returnItems() {
        this.owner.returnItemsScheduled();
    }

    private void fluidAction(final String action) {
        final EntityPlayer player = getPlayerInventory().player;
        final AEFluidInventoryUpgradeable tank = this.owner.getSubFluidHandler();
        if (tank == null) {
            return;
        }
        final ItemStack held = player.inventory.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        if (action.equals("empty")) {
            emptyHeldContainerIntoTank(player, tank);
        } else if (action.equals("fill")) {
            fillHeldContainerFromTank(player, tank);
        }
    }

    public void sendCycleWorkMode() {
        sendClientAction("cycleWorkMode");
    }

    public void sendReturnItems() {
        sendClientAction("returnItems");
    }

    public void sendFluidAction(final boolean fill) {
        sendClientAction("fluidAction", fill ? "fill" : "empty");
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            refreshSyncedState();
        }
        super.broadcastChanges();
    }

    public MEPatternProvider getOwner() {
        return this.owner;
    }

    private void refreshSyncedState() {
        this.workMode = this.owner.getWorkMode().ordinal();
        this.subFluid = SyncedFluidList.from(this.owner.getSubFluidHandler());
        this.stackList = new PatternStackList(snapshotStacks());
    }

    private List<GenericStack> snapshotStacks() {
        final List<GenericStack> stacks = new ObjectArrayList<>();
        appendHandlerStacks(stacks, this.owner.getInfHandler());
        for (final MachineComponent<?> component : this.owner.getCombinationComponents()) {
            final Object provider = component.getContainerProvider();
            if (!(provider instanceof InfItemFluidHandler handler)) {
                throw new IllegalStateException("Unexpected pattern provider cache type: " + provider);
            }
            appendHandlerStacks(stacks, handler);
        }
        return stacks;
    }

    public record PatternStackList(List<GenericStack> stacks) implements PacketWritable {

        public PatternStackList(final ByteBuf data) {
            this(readStacks(data));
        }

        private static List<GenericStack> readStacks(final ByteBuf data) {
            final PacketBuffer buffer = new PacketBuffer(data);
            final int size = buffer.readVarInt();
            final List<GenericStack> stacks = new ObjectArrayList<>(size);
            for (int i = 0; i < size; i++) {
                stacks.add(GenericStack.readBuffer(buffer));
            }
            return stacks;
        }

        @Override
        public void writeToPacket(final ByteBuf data) {
            final PacketBuffer buffer = new PacketBuffer(data);
            buffer.writeVarInt(this.stacks.size());
            for (final GenericStack stack : this.stacks) {
                GenericStack.writeBuffer(stack, buffer);
            }
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof PatternStackList(List<GenericStack> stacks1) && this.stacks.equals(stacks1);
        }

    }
}
