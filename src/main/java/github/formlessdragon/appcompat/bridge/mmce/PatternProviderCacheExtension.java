package github.formlessdragon.appcompat.bridge.mmce;

import ae2.api.networking.IGrid;
import ae2.api.networking.security.IActionHost;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import github.kasuminova.mmce.common.util.InfItemFluidHandler;

import java.util.List;

public interface PatternProviderCacheExtension {

    /**
     * Must be side-effect free: crafting inputs are checked before any cache is changed.
     */
    default boolean supports(final AEKey what) {
        return false;
    }

    boolean push(InfItemFluidHandler handler, AEKey what, long amount);

    void appendStacks(List<GenericStack> stacks, InfItemFluidHandler handler);

    void returnStacks(IGrid grid, IActionHost host, InfItemFluidHandler handler);
}
