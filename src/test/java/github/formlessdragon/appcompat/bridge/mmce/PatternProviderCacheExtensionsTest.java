package github.formlessdragon.appcompat.bridge.mmce;

import ae2.api.networking.IGrid;
import ae2.api.networking.security.IActionHost;
import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import ae2.api.stacks.KeyCounter;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.me.key.AEEssentiaKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternProviderCacheExtensionsTest {

    @BeforeAll
    static void initializeMinecraft() {
        Bootstrap.register();
    }

    @Test
    void unsupportedInputRejectsWholePushWithoutConsumingAnyInputs() {
        final AEKey item = AEItemKey.of(Items.IRON_INGOT);
        final AEKey unsupported = AEEssentiaKey.of(Aspect.AIR);
        final KeyCounter items = KeyCounter.checked();
        items.add(item, 20);
        final KeyCounter extra = KeyCounter.checked();
        extra.add(unsupported, 12);
        assertFalse(PatternProviderCacheExtensions.supportsInputs(new KeyCounter[]{items, extra}));
        assertEquals(20, items.get(item));
        assertEquals(12, extra.get(unsupported));
    }

    @Test
    void nativeItemAndFluidInputsNeedNoExtension() {
        final KeyCounter input = KeyCounter.checked();
        input.add(AEItemKey.of(Items.IRON_INGOT), 20);
        input.add(AEFluidKey.of(FluidRegistry.WATER), 1000);
        assertTrue(PatternProviderCacheExtensions.supportsInputs(new KeyCounter[]{input}));
        assertEquals(2, input.size());
    }

    @Test
    void supportCheckDoesNotInvokeMutationAndRegistrationDeduplicates() {
        final AEKey key = AEEssentiaKey.of(Aspect.FIRE);
        final AtomicInteger calls = new AtomicInteger();
        final PatternProviderCacheExtension extension = new TestExtension(key, calls);
        PatternProviderCacheExtensions.register(extension);
        PatternProviderCacheExtensions.register(extension);
        final KeyCounter inputs = KeyCounter.checked();
        inputs.add(key, 17);
        assertTrue(PatternProviderCacheExtensions.supportsInputs(new KeyCounter[]{inputs}));
        assertEquals(0, calls.get());
        PatternProviderCacheExtensions.appendStacks(new ArrayList<>(), new InfItemFluidHandler());
        assertEquals(1, calls.get());
    }

    private record TestExtension(AEKey key, AtomicInteger calls) implements PatternProviderCacheExtension {
        @Override
        public boolean supports(final AEKey what) {
            return this.key.equals(what);
        }

        @Override
        public boolean push(final InfItemFluidHandler handler, final AEKey what, final long amount) {
            this.calls.incrementAndGet();
            return supports(what);
        }

        @Override
        public void appendStacks(final List<GenericStack> stacks, final InfItemFluidHandler handler) {
            this.calls.incrementAndGet();
        }

        @Override
        public void returnStacks(final IGrid grid, final IActionHost host, final InfItemFluidHandler handler) {
            this.calls.incrementAndGet();
        }
    }
}
