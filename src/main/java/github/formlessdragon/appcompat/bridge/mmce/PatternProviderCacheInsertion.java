package github.formlessdragon.appcompat.bridge.mmce;

import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Keeps a native long quantity exact while filling MMCE's int-sized cache entries.
 */
public final class PatternProviderCacheInsertion {

    private PatternProviderCacheInsertion() {
    }

    public static void appendItems(final InfItemFluidHandler handler, final AEItemKey key, final long amount) {
        synchronized (handler) {
            insertBatches(amount, handler.getItemStackList(), key::matches,
                stack -> stack.isEmpty() ? 0 : stack.getCount(), batch -> handler.appendItem(key.toStack(batch)));
        }
    }

    public static void appendFluid(final InfItemFluidHandler handler, final AEFluidKey key, final long amount) {
        synchronized (handler) {
            final var template = key.toStack(1);
            insertBatches(amount, handler.getFluidStackList(), template::isFluidEqual,
                stack -> stack.amount, batch -> handler.fill(key.toStack(batch), true));
        }
    }

    public static <T> void insertBatches(final long amount, final List<T> stacks, final Predicate<T> matches,
                                         final ToIntFunction<T> count, final IntConsumer insert) {
        long remaining = amount;
        int candidate = 0;
        while (remaining > 0) {
            int batch = (int) Math.min(remaining, Integer.MAX_VALUE);
            // MMCE may copy the original offered stack into an empty entry after a partial merge.
            // Never let one call spill past its first usable entry, retaining its change callbacks.
            while (candidate < stacks.size()) {
                final T stack = stacks.get(candidate);
                if (stack == null || count.applyAsInt(stack) == 0) {
                    break;
                }
                if (matches.test(stack)) {
                    final int capacity = Integer.MAX_VALUE - count.applyAsInt(stack);
                    if (capacity > 0) {
                        batch = Math.min(batch, capacity);
                        break;
                    }
                }
                candidate++;
            }
            insert.accept(batch);
            remaining -= batch;
        }
    }
}
