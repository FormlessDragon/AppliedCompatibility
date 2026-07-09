package github.formlessdragon.appcompat.bridge.ae;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.item.ItemStack;

public final class LegacyAeItemPowerStorageBridge {

    private LegacyAeItemPowerStorageBridge() {
    }

    public static boolean isChargeable(final ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IAEItemPowerStorage powerStorage)) {
            return false;
        }
        return powerStorage.getPowerFlow(stack) != AccessRestriction.READ;
    }

    public static boolean isFull(final ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IAEItemPowerStorage powerStorage)) {
            return false;
        }
        return powerStorage.getAECurrentPower(stack) >= powerStorage.getAEMaxPower(stack);
    }

    public static double getCurrentPower(final ItemStack stack) {
        return getPowerStorage(stack).getAECurrentPower(stack);
    }

    public static double getMaxPower(final ItemStack stack) {
        return getPowerStorage(stack).getAEMaxPower(stack);
    }

    public static double getChargeRate(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Cannot resolve old AE item charge rate for empty stack");
        }
        return AEApi.instance().registries().charger().getChargeRate(stack.getItem());
    }

    public static double injectPower(final ItemStack stack, final double amount, final ae2.api.config.Actionable mode) {
        return getPowerStorage(stack).injectAEPower(stack, amount, toOldActionable(mode));
    }

    public static appeng.api.config.Actionable toOldActionable(final ae2.api.config.Actionable mode) {
        if (mode == ae2.api.config.Actionable.MODULATE) {
            return appeng.api.config.Actionable.MODULATE;
        }
        if (mode == ae2.api.config.Actionable.SIMULATE) {
            return appeng.api.config.Actionable.SIMULATE;
        }
        throw new IllegalArgumentException("Unsupported new AE actionable for old AE item power bridge: " + mode);
    }

    private static IAEItemPowerStorage getPowerStorage(final ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IAEItemPowerStorage powerStorage)) {
            throw new IllegalArgumentException("Stack is not an old AE powered item: " + stack);
        }
        return powerStorage;
    }
}
