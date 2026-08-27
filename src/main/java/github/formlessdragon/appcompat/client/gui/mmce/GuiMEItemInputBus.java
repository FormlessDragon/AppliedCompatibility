package github.formlessdragon.appcompat.client.gui.mmce;

import ae2.api.behaviors.EmptyingAction;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AmountFormat;
import ae2.api.stacks.GenericStack;
import ae2.client.gui.style.GuiStyleManager;
import ae2.container.slot.AppEngSlot;
import ae2.container.slot.FakeSlotFilterSupport;
import ae2.integration.modules.hei.GenericIngredientHelper;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus.ItemConfigSlot;
import github.kasuminova.mmce.common.tile.MEItemInputBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiMEItemInputBus extends GuiMEBusBase<ContainerMEItemInputBus> {

    private final ContainerMEItemInputBus container;

    public GuiMEItemInputBus(final MEItemInputBus te, final EntityPlayer player) {
        super(new ContainerMEItemInputBus(te, player), player.inventory,
            GuiStyleManager.loadStyleDoc("/screens/appcompat_me_item_input_bus.json"));
        this.container = (ContainerMEItemInputBus) this.inventorySlots;
    }

    private static String formatItemAmount(final ItemStack stack) {
        final AEItemKey key = AEItemKey.of(stack);
        if (key == null) {
            return Integer.toString(stack.getCount());
        }
        return key.formatAmount(stack.getCount(), AmountFormat.FULL);
    }

    private static List<String> getScrollActionInfo() {
        final List<String> tooltip = new ArrayList<>();
        tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action"));
        final boolean shift = isShiftKeyDown();
        final boolean ctrl = isCtrlKeyDown();
        if (shift && ctrl) {
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.multiply", "SHIFT + CTRL"));
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.divide", "SHIFT + CTRL"));
            return tooltip;
        }
        if (ctrl) {
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.increase", "CTRL", 100));
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.decrease", "CTRL", 100));
            return tooltip;
        }
        if (shift) {
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.increase", "SHIFT", 10));
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.decrease", "SHIFT", 10));
            return tooltip;
        }
        tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.increase.normal"));
        tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.inv_action.decrease.normal"));
        return tooltip;
    }

    public static ItemStack toItemFilterStack(final ItemConfigSlot slot, final Object ingredient) {
        if (ingredient instanceof ItemStack stack) {
            return toItemFilterStack(slot, stack.copy(), true);
        }

        final GenericStack stack = GenericIngredientHelper.ingredientToStack(ingredient);
        if (stack != null) {
            final ItemStack wrapped = GenericStack.wrapInItemStack(stack);
            if (wrapped.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return toItemFilterStack(slot, wrapped, false);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack toItemFilterStack(final ItemConfigSlot slot, final ItemStack stack, final boolean allowPreferredItemStack) {
        final ItemStack directFilter = ItemConfigSlot.getItemFilterStack(stack);
        if (!directFilter.isEmpty() && slot.canSetFilterTo(directFilter)) {
            return directFilter;
        }
        if (!allowPreferredItemStack) {
            return ItemStack.EMPTY;
        }
        final ItemStack preferred = FakeSlotFilterSupport.getPreferredFilterStack(slot, stack);
        final ItemStack preferredFilter = ItemConfigSlot.getItemFilterStack(preferred);
        if (!preferredFilter.isEmpty() && slot.canSetFilterTo(preferredFilter)) {
            return preferredFilter;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            final int mx = Mouse.getEventX() * this.width / this.mc.displayWidth;
            final int my = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            onMouseWheelEvent(mx, my, wheel > 0 ? 1 : -1);
        }
    }

    private void onMouseWheelEvent(final int x, final int y, final int wheel) {
        final Slot slot = findSlot(x, y);
        if (!(slot instanceof ItemConfigSlot)) {
            return;
        }
        final ItemStack stack = slot.getStack();
        if (stack.isEmpty()) {
            return;
        }
        final int target = getUpdatedCount(wheel > 0, stack.getCount());
        if (target > 0 && target <= slot.getSlotStackLimit()) {
            this.container.sendSetConfigCount(slot.slotNumber, target);
        }
    }

    private int getUpdatedCount(final boolean up, final int current) {
        final boolean shift = isShiftKeyDown();
        final boolean ctrl = isCtrlKeyDown();
        if (shift && ctrl) {
            if (up) {
                return current <= Integer.MAX_VALUE / 2 ? current * 2 : Integer.MAX_VALUE;
            }
            return Math.max(1, current / 2);
        }
        final int step = ctrl ? 100 : shift ? 10 : 1;
        if (up) {
            return current < Integer.MAX_VALUE - step ? current + step : Integer.MAX_VALUE;
        }
        return Math.max(1, current - step);
    }

    @Override
    protected boolean shouldRenderSingleCountSlot(final Slot slot) {
        return slot instanceof AppEngSlot;
    }

    @Override
    protected EmptyingAction getEmptyingAction(final Slot slot, final ItemStack stack) {
        if (slot instanceof ItemConfigSlot) {
            return null;
        }
        return super.getEmptyingAction(slot, stack);
    }

    @Override
    public Collection<? extends Slot> getHEISlots(final Object ingredient) {
        final List<Slot> slots = new ArrayList<>();
        for (final Slot slot : this.container.inventorySlots) {
            if (slot instanceof ItemConfigSlot itemConfigSlot && !toItemFilterStack(itemConfigSlot, ingredient).isEmpty()) {
                slots.add(slot);
            }
        }
        return slots;
    }

    @Override
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        final Slot slot = this.getSlotUnderMouse();
        if (slot != null) {
            final ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                final List<String> tooltip = new ArrayList<>(this.getItemToolTip(stack));
                if (slot instanceof ItemConfigSlot) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("gui.meiteminputbus.items_marked", formatItemAmount(stack)));
                    tooltip.addAll(getScrollActionInfo());
                    this.drawHoveringText(tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
