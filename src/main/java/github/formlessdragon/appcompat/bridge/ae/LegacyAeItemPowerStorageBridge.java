package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.config.Actionable;
import ae2.api.implementations.items.IChargeableItemAdapter;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.item.ItemStack;

public final class LegacyAeItemPowerStorageBridge implements IChargeableItemAdapter {
    public static final LegacyAeItemPowerStorageBridge INSTANCE = new LegacyAeItemPowerStorageBridge();

    private LegacyAeItemPowerStorageBridge() {
    }

    @Override
    public boolean handles(final ItemStack stack) {
        return isChargeable(stack);
    }
    private static boolean isChargeable(final ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IAEItemPowerStorage powerStorage)) {
            return false;
        }
        return powerStorage.getPowerFlow(stack) != AccessRestriction.READ;
    }

    @Override
    public double getCurrentPower(final ItemStack stack) {
        return getPowerStorage(stack).getAECurrentPower(stack);
    }

    @Override
    public double getMaxPower(final ItemStack stack) {
        return getPowerStorage(stack).getAEMaxPower(stack);
    }

    @Override
    public double getChargeRate(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Cannot resolve old AE item charge rate for empty stack");
        }
        return AEApi.instance().registries().charger().getChargeRate(stack.getItem());
    }

    @Override
    public double injectPower(final ItemStack stack, final double amount, final Actionable mode) {
        return getPowerStorage(stack).injectAEPower(stack, amount, toOldActionable(mode));
    }

    @Override
    public boolean isFullyCharged(final ItemStack stack) {
        return getCurrentPower(stack) >= getMaxPower(stack);
    }

    private static appeng.api.config.Actionable toOldActionable(final Actionable mode) {
        if (mode == Actionable.MODULATE) {
            return appeng.api.config.Actionable.MODULATE;
        }
        if (mode == Actionable.SIMULATE) {
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
