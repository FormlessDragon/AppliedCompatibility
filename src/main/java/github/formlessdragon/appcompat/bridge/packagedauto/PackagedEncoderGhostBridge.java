package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.behaviors.ContainerItemStrategies;
import ae2.api.behaviors.EmptyingAction;
import ae2.api.integrations.hei.IngredientConverter;
import ae2.api.integrations.hei.IngredientConverters;
import ae2.api.stacks.GenericStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.lwjgl.input.Mouse;
import thelm.packagedauto.client.gui.GuiEncoder;
import thelm.packagedauto.network.PacketHandler;
import thelm.packagedauto.network.packet.PacketSetItemStack;
import thelm.packagedauto.slot.SlotFalseCopy;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

public final class PackagedEncoderGhostBridge {

    private static PackagedEncoderGhostState ghostState;

    private PackagedEncoderGhostBridge() {
    }

    public static void setGhostState(final PackagedEncoderGhostState state) {
        ghostState = state;
    }

    public static void clearGhostState(final PackagedEncoderGhostState state) {
        if (ghostState == state) {
            ghostState = null;
        }
    }

    public static ItemStack getGhostTooltipStack() {
        if (ghostState == null) {
            return ItemStack.EMPTY;
        }
        final Object ingredient = ghostState.appcompat$getCurrentGhostIngredient();
        return ingredient == null ? ItemStack.EMPTY : toLeftClickStack(ingredient);
    }

    public static GenericStack getGhostTooltipGenericStack() {
        if (ghostState == null) {
            return null;
        }
        final Object ingredient = ghostState.appcompat$getCurrentGhostIngredient();
        return ingredient == null ? null : toGenericStack(ingredient);
    }

    public static <I> List<IGhostIngredientHandler.Target<I>> getTargets(final GuiEncoder gui, final I ingredient, final int fallbackMouseButton) {
        final ItemStack leftStack = toLeftClickStack(ingredient);
        final ItemStack rightStack = toRightClickStack(ingredient);
        if (leftStack.isEmpty() && rightStack.isEmpty()) {
            return Collections.emptyList();
        }
        final List<IGhostIngredientHandler.Target<I>> targets = new ObjectArrayList<>();
        for (final Slot slot : gui.container.inventorySlots) {
            if (slot instanceof SlotFalseCopy) {
                targets.add(new SlotTarget<>(gui, slot, fallbackMouseButton, leftStack, rightStack));
            }
        }
        return targets;
    }

    public static ItemStack toClickStack(final ItemStack stack, final int mouseButton) {
        if (mouseButton == 1) {
            final EmptyingAction action = ContainerItemStrategies.getEmptyingAction(stack);
            return action == null ? ItemStack.EMPTY : GenericStack.wrapInItemStack(action.what(), action.maxAmount());
        }
        return stack;
    }

    public static ItemStack toLeftClickStack(final Object ingredient) {
        if (ingredient instanceof ItemStack stack) {
            return stack;
        }
        return toWrappedIngredientStack(ingredient);
    }

    public static ItemStack toRightClickStack(final Object ingredient) {
        if (ingredient instanceof ItemStack stack) {
            final EmptyingAction action = ContainerItemStrategies.getEmptyingAction(stack);
            return action == null ? ItemStack.EMPTY : GenericStack.wrapInItemStack(action.what(), action.maxAmount());
        }
        return toWrappedIngredientStack(ingredient);
    }

    private static ItemStack toWrappedIngredientStack(final Object ingredient) {
        for (final IngredientConverter<?> converter : IngredientConverters.getConverters()) {
            final ItemStack stack = toWrappedIngredientStack(converter, ingredient);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static GenericStack toGenericStack(final Object ingredient) {
        if (ingredient instanceof ItemStack stack) {
            final GenericStack wrapped = GenericStack.unwrapItemStack(stack);
            return wrapped == null ? null : wrapped;
        }
        for (final IngredientConverter<?> converter : IngredientConverters.getConverters()) {
            final GenericStack stack = toGenericStack(converter, ingredient);
            if (stack != null && stack.amount() > 0) {
                return stack;
            }
        }
        return null;
    }

    public static int activeMouseButton() {
        final int eventButton = Mouse.getEventButton();
        if (eventButton >= 0) {
            return eventButton;
        }
        if (Mouse.isButtonDown(1)) {
            return 1;
        }
        if (Mouse.isButtonDown(0)) {
            return 0;
        }
        return -1;
    }

    private static <T> ItemStack toWrappedIngredientStack(final IngredientConverter<T> converter, final Object ingredient) {
        final Class<? extends T> ingredientClass = converter.getIngredientType().getIngredientClass();
        if (!ingredientClass.isInstance(ingredient)) {
            return ItemStack.EMPTY;
        }
        final GenericStack stack = converter.getStackFromIngredient(ingredientClass.cast(ingredient));
        return stack == null ? ItemStack.EMPTY : GenericStack.wrapInItemStack(stack);
    }

    private static <T> GenericStack toGenericStack(final IngredientConverter<T> converter, final Object ingredient) {
        final Class<? extends T> ingredientClass = converter.getIngredientType().getIngredientClass();
        if (!ingredientClass.isInstance(ingredient)) {
            return null;
        }
        return converter.getStackFromIngredient(ingredientClass.cast(ingredient));
    }

    private record SlotTarget<I>(GuiContainer gui, Slot slot, int fallbackMouseButton, ItemStack leftStack, ItemStack rightStack) implements IGhostIngredientHandler.Target<I> {

        @Override
        public Rectangle getArea() {
            return new Rectangle(this.gui.getGuiLeft() + this.slot.xPos, this.gui.getGuiTop() + this.slot.yPos, 16, 16);
        }

        @Override
        public void accept(final @NonNull I ingredient) {
            int mouseButton = activeMouseButton();
            if (mouseButton < 0) {
                mouseButton = this.fallbackMouseButton;
            }
            ItemStack stack = mouseButton == 1 ? this.rightStack : this.leftStack;
            if (stack.isEmpty() && mouseButton >= 0) {
                stack = mouseButton == 1 ? toRightClickStack(ingredient) : toLeftClickStack(ingredient);
            }
            if (!stack.isEmpty()) {
                PacketHandler.INSTANCE.sendToServer(new PacketSetItemStack(this.slot.slotNumber, stack));
            }
        }
    }
}
