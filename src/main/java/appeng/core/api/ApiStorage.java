package appeng.core.api;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageHelper;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.FluidList;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import com.mekeng.github.common.me.data.IAEGasStack;
import com.mekeng.github.common.me.data.impl.AEGasStack;
import com.mekeng.github.common.me.storage.IGasStorageChannel;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ApiStorage implements IStorageHelper {

    private final Map<Class<?>, IStorageChannel<?>> channels = new Object2ObjectLinkedOpenHashMap<>();

    public ApiStorage() {
        this.registerStorageChannel(IItemStorageChannel.class, new ItemStorageChannel());
        this.registerStorageChannel(IFluidStorageChannel.class, new FluidStorageChannel());
        this.registerStorageChannel(IGasStorageChannel.class, new GasStorageChannel());
    }

    @Override
    public <T extends IAEStack<T>, C extends IStorageChannel<T>> void registerStorageChannel(final Class<C> channel,
                                                                                             final C factory) {
        if (channel == null || factory == null) {
            throw new IllegalArgumentException("Storage channel and factory are required");
        }
        if (!channel.isInstance(factory)) {
            throw new IllegalArgumentException("Storage factory " + factory.getClass().getName() + " does not implement " + channel.getName());
        }
        if (this.channels.containsKey(channel)) {
            throw new IllegalArgumentException("Storage channel already registered: " + channel.getName());
        }
        this.channels.put(channel, factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IAEStack<T>, C extends IStorageChannel<T>> C getStorageChannel(final Class<C> channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Storage channel class is required");
        }
        final IStorageChannel<?> result = this.channels.get(channel);
        if (result == null) {
            throw new IllegalStateException("Storage channel is not registered: " + channel.getName());
        }
        return (C) result;
    }

    @Override
    public Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels() {
        return Collections.unmodifiableCollection((Collection) this.channels.values());
    }

    @Override
    public <T extends IAEStack<T>> T poweredInsert(final IEnergySource energy, final IMEInventory<T> cell,
                                                  final T input, final IActionSource src, final Actionable mode) {
        return (T) Platform.poweredInsert(energy, cell, input, src, mode);
    }

    @Override
    public <T extends IAEStack<T>> T poweredExtraction(final IEnergySource energy, final IMEInventory<T> cell,
                                                       final T request, final IActionSource src, final Actionable mode) {
        return (T) Platform.poweredExtraction(energy, cell, request, src, mode);
    }

    private static final class ItemStorageChannel implements IItemStorageChannel {

        @Override
        public IItemList<IAEItemStack> createList() {
            return new ItemList();
        }

        @Override
        public IAEItemStack createStack(final Object input) {
            if (input instanceof ItemStack stack) {
                return AEItemStack.fromItemStack(stack);
            }
            return null;
        }

        @Override
        public IAEItemStack readFromPacket(final ByteBuf input) throws IOException {
            if (input == null) {
                throw new IllegalArgumentException("Packet buffer is required");
            }
            return AEItemStack.fromPacket(input);
        }

        @Override
        public IAEItemStack createFromNBT(final NBTTagCompound nbt) {
            if (nbt == null) {
                throw new IllegalArgumentException("NBT is required");
            }
            return AEItemStack.fromNBT(nbt);
        }
    }

    private static final class FluidStorageChannel implements IFluidStorageChannel {

        @Override
        public int transferFactor() {
            return 1000;
        }

        @Override
        public int getUnitsPerByte() {
            return 8000;
        }

        @Override
        public IItemList<IAEFluidStack> createList() {
            return new FluidList();
        }

        @Override
        public IAEFluidStack createStack(final Object input) {
            if (input instanceof FluidStack stack) {
                return AEFluidStack.fromFluidStack(stack);
            }
            if (input instanceof ItemStack stack) {
                return AEFluidStack.fromFluidStack(FluidUtil.getFluidContained(stack));
            }
            return null;
        }

        @Override
        public IAEFluidStack readFromPacket(final ByteBuf input) throws IOException {
            if (input == null) {
                throw new IllegalArgumentException("Packet buffer is required");
            }
            return AEFluidStack.fromPacket(input);
        }

        @Override
        public IAEFluidStack createFromNBT(final NBTTagCompound nbt) {
            if (nbt == null) {
                throw new IllegalArgumentException("NBT is required");
            }
            return AEFluidStack.fromNBT(nbt);
        }
    }

    private static final class GasStorageChannel implements IGasStorageChannel {

        @Override
        public int transferFactor() {
            return 1000;
        }

        @Override
        public int getUnitsPerByte() {
            return 8000;
        }

        @Override
        public IItemList<IAEGasStack> createList() {
            return new GenericStackList<>();
        }

        @Override
        public IAEGasStack createStack(final Object input) {
            if (input instanceof GasStack stack) {
                return AEGasStack.of(stack);
            }
            return null;
        }

        @Override
        public IAEGasStack readFromPacket(final ByteBuf input) throws IOException {
            if (input == null) {
                throw new IllegalArgumentException("Packet buffer is required");
            }
            return AEGasStack.of(input);
        }

        @Override
        public IAEGasStack createFromNBT(final NBTTagCompound nbt) {
            if (nbt == null) {
                throw new IllegalArgumentException("NBT is required");
            }
            return AEGasStack.of(nbt);
        }
    }

    private static final class GenericStackList<T extends IAEStack<T>> implements IItemList<T> {

        private final Map<T, T> records = new Object2ObjectLinkedOpenHashMap<>();

        @Override
        public void addStorage(final T option) {
            getOrCreate(option).incStackSize(option.getStackSize());
        }

        @Override
        public void addCrafting(final T option) {
            getOrCreate(option).setCraftable(true);
        }

        @Override
        public void addRequestable(final T option) {
            getOrCreate(option).incCountRequestable(option.getStackSize());
        }

        @Override
        public T getFirstItem() {
            final Iterator<T> iterator = iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }

        @Override
        public int size() {
            return this.records.size();
        }

        @Override
        public void resetStatus() {
            for (final T stack : this.records.values()) {
                stack.setCountRequestable(0);
                stack.setCraftable(false);
            }
        }

        @Override
        public void add(final T option) {
            getOrCreate(option).add(option);
        }

        @Override
        public T findPrecise(final T option) {
            return option == null ? null : this.records.get(option);
        }

        @Override
        public Collection<T> findFuzzy(final T filter, final FuzzyMode fuzzy) {
            final it.unimi.dsi.fastutil.objects.ObjectArrayList<T> result = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();
            if (filter == null) {
                return result;
            }
            for (final T stack : this.records.values()) {
                if (stack.fuzzyComparison(filter, fuzzy)) {
                    result.add(stack);
                }
            }
            return result;
        }

        @Override
        public boolean isEmpty() {
            return this.records.isEmpty();
        }

        @Override
        public @NonNull Iterator<T> iterator() {
            return this.records.values().iterator();
        }

        private T getOrCreate(final T option) {
            if (option == null) {
                throw new IllegalArgumentException("Cannot add a null stack");
            }
            final T existing = this.records.get(option);
            if (existing != null) {
                return existing;
            }
            final T empty = option.empty();
            this.records.put(option, empty);
            return empty;
        }
    }
}
