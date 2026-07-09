package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.FluidList;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;

public final class LegacyAeStorageBridge {

    private LegacyAeStorageBridge() {
    }

    public static ae2.api.config.Actionable toNewActionable(final Actionable mode) {
        return mode == Actionable.SIMULATE ? ae2.api.config.Actionable.SIMULATE : ae2.api.config.Actionable.MODULATE;
    }

    public static AEKey toKey(final IAEStack<?> stack) {
        if (stack instanceof AEItemStack itemStack) {
            return itemStack.getKey();
        }
        if (stack instanceof AEFluidStack fluidStack) {
            return fluidStack.getKey();
        }
        if (stack instanceof IAEItemStack itemStack) {
            return AEItemKey.of(itemStack.createItemStack());
        }
        if (stack instanceof IAEFluidStack fluidStack) {
            return AEFluidKey.of(fluidStack.getFluidStack());
        }
        throw new IllegalArgumentException("Unsupported old AE stack " + stack.getClass().getName());
    }

    public static IAEStack<?> toOldStack(final AEKey key, final long amount) {
        if (key instanceof AEItemKey itemKey) {
            return AEItemStack.fromKey(itemKey, amount);
        }
        if (key instanceof AEFluidKey fluidKey) {
            return AEFluidStack.fromKey(fluidKey, amount);
        }
        throw new IllegalArgumentException("Unsupported new AE key " + key.getClass().getName());
    }

    public static boolean matchesChannel(final IStorageChannel<?> channel, final AEKey key) {
        if (channel instanceof IItemStorageChannel) {
            return key instanceof AEItemKey;
        }
        if (channel instanceof IFluidStorageChannel) {
            return key instanceof AEFluidKey;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IAEStack<T>> IItemList<T> createList(final IStorageChannel<T> channel) {
        if (channel instanceof IItemStorageChannel) {
            return (IItemList<T>) new ItemList();
        }
        if (channel instanceof IFluidStorageChannel) {
            return (IItemList<T>) new FluidList();
        }
        throw new IllegalArgumentException("Unsupported old AE storage channel " + channel.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    public static <T extends IAEStack<T>> T castOldStack(final IAEStack<?> stack) {
        return (T) stack;
    }
}
