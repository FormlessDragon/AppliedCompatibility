package github.formlessdragon.appcompat.bridge.mmce.mekeng;

import ae2.api.networking.IGrid;
import ae2.api.networking.security.IActionHost;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatGasBridge;
import github.formlessdragon.appcompat.bridge.mmce.PatternProviderCacheExtension;
import github.formlessdragon.appcompat.bridge.mmce.PatternProviderCacheExtensions;
import github.formlessdragon.appcompat.bridge.mmce.PatternProviderCacheInsertion;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import me.ramidzkh.mekae2.ae2.AEGasKey;
import mekanism.api.gas.GasStack;

import java.util.List;

public final class PatternProviderGasCacheExtension implements PatternProviderCacheExtension {

    private static final PatternProviderGasCacheExtension INSTANCE = new PatternProviderGasCacheExtension();

    private PatternProviderGasCacheExtension() {
    }

    public static void register() {
        PatternProviderCacheExtensions.register(INSTANCE);
    }

    @SuppressWarnings("unchecked")
    private static List<GasStack> getGasStackList(final InfItemFluidHandler handler) {
        return (List<GasStack>) handler.getGasStackList();
    }

    @Override
    public boolean supports(final AEKey what) {
        return what instanceof AEGasKey;
    }

    @Override
    public boolean push(final InfItemFluidHandler handler, final AEKey what, final long amount) {
        if (!(what instanceof AEGasKey gasKey)) {
            return false;
        }
        synchronized (handler) {
            final GasStack template = gasKey.toStack(1);
            PatternProviderCacheInsertion.insertBatches(amount, getGasStackList(handler),
                template::isGasEqual, stack -> stack.amount,
                batch -> handler.receiveGas(null, gasKey.toStack(batch), true));
        }
        return true;
    }

    @Override
    public void appendStacks(final List<GenericStack> stacks, final InfItemFluidHandler handler) {
        for (final Object raw : handler.getGasStackList()) {
            if (raw == null) {
                continue;
            }
            if (!(raw instanceof GasStack stack)) {
                throw new IllegalStateException("Unexpected gas cache type: " + raw);
            }
            final AEGasKey key = AEGasKey.of(stack);
            if (key != null && stack.amount > 0) {
                stacks.add(new GenericStack(key, stack.amount));
            }
        }
    }

    @Override
    public void returnStacks(final IGrid grid, final IActionHost host, final InfItemFluidHandler handler) {
        final List<GasStack> gasStacks = getGasStackList(handler);
        for (int i = 0; i < gasStacks.size(); i++) {
            final GasStack stack = gasStacks.get(i);
            if (stack == null) {
                continue;
            }
            final GasStack left = AppCompatGasBridge.insert(grid, host, stack);
            gasStacks.set(i, left == null ? null : left.copy());
        }
    }
}
