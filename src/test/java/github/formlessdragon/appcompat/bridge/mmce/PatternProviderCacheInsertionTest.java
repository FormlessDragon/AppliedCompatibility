package github.formlessdragon.appcompat.bridge.mmce;

import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternProviderCacheInsertionTest {

    @BeforeAll
    static void initializeMinecraft() {
        Bootstrap.register();
    }

    private static long itemAmount(final InfItemFluidHandler handler) {
        return handler.getItemStackList().stream().mapToLong(ItemStack::getCount).sum();
    }

    @Test
    void itemMergeAcrossExistingEntriesKeepsExactAmount() {
        final InfItemFluidHandler handler = new InfItemFluidHandler();
        handler.getItemStackList().add(new ItemStack(Items.IRON_INGOT, Integer.MAX_VALUE - 3));
        handler.getItemStackList().add(new ItemStack(Items.IRON_INGOT, 4));
        final AtomicInteger changed = new AtomicInteger();
        handler.setOnItemChanged(slot -> changed.incrementAndGet());
        PatternProviderCacheInsertion.appendItems(handler, AEItemKey.of(Items.IRON_INGOT), 12);
        assertEquals((long) Integer.MAX_VALUE + 13, itemAmount(handler));
        assertEquals(13, handler.getItemStackList().get(1).getCount());
        assertTrue(changed.get() > 0);
    }

    @Test
    void longItemAmountUsesIntSizedCacheBatches() {
        final InfItemFluidHandler handler = new InfItemFluidHandler();
        final AtomicInteger changed = new AtomicInteger();
        handler.setOnItemChanged(slot -> changed.incrementAndGet());
        final long amount = (long) Integer.MAX_VALUE + 7;
        PatternProviderCacheInsertion.appendItems(handler, AEItemKey.of(Items.IRON_INGOT), amount);
        assertEquals(amount, itemAmount(handler));
        assertEquals(2, handler.getItemStackList().size());
        assertTrue(changed.get() < 5);
    }

    @Test
    void partialFluidMergeFollowedByEmptyEntryDoesNotDuplicateRemainder() {
        final InfItemFluidHandler handler = new InfItemFluidHandler();
        final AEFluidKey key = AEFluidKey.of(FluidRegistry.WATER);
        handler.getFluidStackList().add(key.toStack(Integer.MAX_VALUE - 3));
        handler.getFluidStackList().add(null);
        PatternProviderCacheInsertion.appendFluid(handler, key, 12);
        assertEquals((long) Integer.MAX_VALUE + 9,
            handler.getFluidStackList().stream().filter(stack -> stack != null).mapToLong(stack -> stack.amount).sum());
        assertEquals(9, handler.getFluidStackList().get(1).amount);
    }

    @Test
    void batchPreflightVisitsFullPrefixOnlyOnce() {
        final List<Long> entries = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            entries.add((long) Integer.MAX_VALUE);
        }
        final AtomicInteger visits = new AtomicInteger();
        final long amount = 4L * Integer.MAX_VALUE + 7;
        PatternProviderCacheInsertion.insertBatches(amount, entries, stack -> true,
            stack -> {
                visits.incrementAndGet();
                return stack.intValue();
            }, batch -> entries.add((long) batch));
        assertEquals(500L * Integer.MAX_VALUE + amount, entries.stream().mapToLong(Long::longValue).sum());
        assertTrue(visits.get() < 1100, "Each existing entry should be visited at most twice");
    }
}
