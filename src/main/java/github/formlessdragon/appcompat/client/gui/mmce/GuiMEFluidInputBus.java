package github.formlessdragon.appcompat.client.gui.mmce;

import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.GenericStack;
import ae2.client.gui.style.FluidBlitter;
import ae2.client.gui.style.GuiStyleManager;
import ae2.integration.modules.hei.GenericIngredientHelper;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEFluidInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEFluidInputBus.FluidConfigSlot;
import github.formlessdragon.appcompat.common.container.mmce.SyncedFluidList;
import github.kasuminova.mmce.common.tile.MEFluidInputBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuiMEFluidInputBus extends GuiMEBusBase<ContainerMEFluidInputBus> {

    private static final int TANKS = 9;

    private final ContainerMEFluidInputBus container;

    public GuiMEFluidInputBus(final MEFluidInputBus te, final EntityPlayer player) {
        super(new ContainerMEFluidInputBus(te, player), player.inventory,
            GuiStyleManager.loadStyleDoc("/screens/appcompat_me_fluid_input_bus.json"));
        this.container = (ContainerMEFluidInputBus) this.inventorySlots;
    }

    private static boolean isFluidIngredient(final Object ingredient) {
        if (ingredient instanceof FluidStack stack) {
            return AEFluidKey.of(stack) != null;
        }
        if (ingredient instanceof ItemStack stack) {
            return FluidConfigSlot.isFluidFilterStack(stack);
        }
        final GenericStack stack = GenericIngredientHelper.ingredientToStack(ingredient);
        return stack != null && stack.what() instanceof AEFluidKey;
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawFluidBars(this.container.tankFluids, 53, 68);
    }

    @Override
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        final Slot slot = this.getSlotUnderMouse();
        if (slot instanceof FluidConfigSlot) {
            final List<String> tooltip = buildConfigFluidTooltip(slot.getSlotIndex());
            if (!tooltip.isEmpty()) {
                this.drawHoveringText(tooltip, mouseX, mouseY);
            }
            return;
        }
        final int relX = mouseX - this.guiLeft;
        final int relY = mouseY - this.guiTop;
        for (int i = 0; i < TANKS; i++) {
            final int x = 8 + 18 * i;
            if (relX >= x && relX < x + 16 && relY >= 53 && relY < 121) {
                final List<String> tooltip = buildFluidTooltip(this.container.tankFluids, i, getCapacity());
                if (!tooltip.isEmpty()) {
                    this.drawHoveringText(tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    private List<String> buildConfigFluidTooltip(final int index) {
        final AEFluidKey key = this.container.configFluids.keyAt(index);
        if (key == null) {
            return Collections.emptyList();
        }
        final List<String> tooltip = new ArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        if (this.playerInventory.getItemStack().isEmpty()) {
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.ae2.Clean"));
        }
        return tooltip;
    }

    private void drawFluidBars(final SyncedFluidList fluids, final int y, final int height) {
        for (int i = 0; i < TANKS; i++) {
            final AEFluidKey key = fluids.keyAt(i);
            final long amount = fluids.amountAt(i);
            if (key == null || amount <= 0) {
                continue;
            }
            final int x = 8 + 18 * i;
            final int capacity = getCapacity();
            final int filledHeight = Math.max(1, (int) Math.round((double) amount * height / capacity));
            FluidBlitter.create(key).dest(x, y + height - filledHeight, 16, filledHeight).blit();
        }
    }

    private List<String> buildFluidTooltip(final SyncedFluidList fluids, final int index, final int capacity) {
        final AEFluidKey key = fluids.keyAt(index);
        if (key == null) {
            return Collections.emptyList();
        }
        final List<String> tooltip = new ArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        tooltip.add(fluids.amountAt(index) + " / " + capacity + " mB");
        return tooltip;
    }

    private int getCapacity() {
        return Math.max(1, this.container.capacity);
    }

    @Override
    public Collection<? extends Slot> getHEISlots(final Object ingredient) {
        if (!isFluidIngredient(ingredient)) {
            return Collections.emptyList();
        }
        final List<Slot> slots = new ArrayList<>();
        for (final Slot slot : this.container.inventorySlots) {
            if (slot instanceof FluidConfigSlot) {
                slots.add(slot);
            }
        }
        return slots;
    }
}
