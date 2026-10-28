package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.client.AEKeyRendering;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AmountFormat;
import ae2.api.stacks.GenericStack;
import ae2.core.localization.ButtonToolTips;
import ae2.core.localization.Tooltips;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class PackagedPatternStacks {

    private PackagedPatternStacks() {
    }

    public static GenericStack fromItemStack(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        final GenericStack wrapped = GenericStack.unwrapItemStack(stack);
        if (wrapped != null) {
            if (wrapped.amount() <= 0) {
                throw new IllegalArgumentException("Wrapped PackagedAuto stack has invalid amount");
            }
            return wrapped;
        }
        final AEItemKey key = AEItemKey.of(stack);
        if (key == null) {
            return null;
        }
        return new GenericStack(key, stack.getCount());
    }

    public static List<GenericStack> fromItemStacks(final List<ItemStack> stacks) {
        final List<GenericStack> result = new ObjectArrayList<>();
        for (final ItemStack stack : stacks) {
            final GenericStack genericStack = fromItemStack(stack);
            if (genericStack != null) {
                result.add(genericStack);
            }
        }
        return result;
    }

    public static ItemStack toItemStack(final GenericStack stack) {
        if (stack.amount() <= 0) {
            return ItemStack.EMPTY;
        }
        if (stack.what() instanceof AEItemKey itemKey) {
            return itemKey.toStack((int) Math.min(stack.amount(), Integer.MAX_VALUE));
        }
        return GenericStack.wrapInItemStack(stack);
    }

    public static long amount(final ItemStack stack) {
        final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
        return genericStack == null ? stack.getCount() : genericStack.amount();
    }

    public static ItemStack withAmount(final ItemStack stack, final long amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        final GenericStack wrapped = GenericStack.unwrapItemStack(stack);
        if (wrapped != null) {
            return GenericStack.wrapInItemStack(new GenericStack(wrapped.what(), amount));
        }
        final ItemStack copy = stack.copy();
        copy.setCount((int) Math.min(amount, Integer.MAX_VALUE));
        return copy;
    }

    public static boolean hasInsertedAny(final ItemStack original, final ItemStack remainder) {
        return amount(remainder) < amount(original);
    }

    public static String format(final ItemStack stack) {
        final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
        if (genericStack == null) {
            return stack.getCount() + " " + stack.getDisplayName();
        }
        return format(genericStack);
    }

    public static String format(final GenericStack stack) {
        return stack.what().getType().formatAmount(stack.amount(), AmountFormat.FULL) + " " + stack.what().getDisplayName().getFormattedText();
    }

    public static void addTooltip(final List<String> tooltip, final GenericStack stack) {
        tooltip.clear();
        for (final var line : AEKeyRendering.getTooltip(stack.what())) {
            tooltip.add(line.getFormattedText());
        }
        if (stack.amount() > 1 || Tooltips.shouldShowAmountTooltip(stack.what(), stack.amount())) {
            tooltip.add(Tooltips.getAmountTooltipLocal(ButtonToolTips.Amount, stack));
        }
    }
}
