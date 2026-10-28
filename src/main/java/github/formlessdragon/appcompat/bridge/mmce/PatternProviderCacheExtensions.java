package github.formlessdragon.appcompat.bridge.mmce;

import ae2.api.networking.IGrid;
import ae2.api.networking.security.IActionHost;
import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import ae2.api.stacks.KeyCounter;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;

public final class PatternProviderCacheExtensions {

    private static volatile List<PatternProviderCacheExtension> extensions = ObjectLists.emptyList();

    private PatternProviderCacheExtensions() {
    }

    public static synchronized void register(final PatternProviderCacheExtension extension) {
        if (!extensions.contains(extension)) {
            final var updated = new ObjectArrayList<>(extensions);
            updated.add(extension);
            extensions = ObjectLists.unmodifiable(updated);
        }
    }

    public static boolean push(final InfItemFluidHandler handler, final AEKey what, final long amount) {
        for (final PatternProviderCacheExtension extension : snapshot()) {
            if (extension.push(handler, what, amount)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportsInputs(final KeyCounter[] inputs) {
        for (final KeyCounter counter : inputs) {
            for (final var entry : counter) {
                final AEKey key = entry.getKey();
                if (entry.getLongValue() > 0 && !(key instanceof AEItemKey) && !(key instanceof AEFluidKey)
                    && !supports(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean supports(final AEKey what) {
        for (final PatternProviderCacheExtension extension : snapshot()) {
            if (extension.supports(what)) {
                return true;
            }
        }
        return false;
    }

    public static void appendStacks(final List<GenericStack> stacks, final InfItemFluidHandler handler) {
        for (final PatternProviderCacheExtension extension : snapshot()) {
            extension.appendStacks(stacks, handler);
        }
    }

    public static void returnStacks(final IGrid grid, final IActionHost host, final InfItemFluidHandler handler) {
        for (final PatternProviderCacheExtension extension : snapshot()) {
            extension.returnStacks(grid, host, handler);
        }
    }

    private static List<PatternProviderCacheExtension> snapshot() {
        return extensions;
    }
}
