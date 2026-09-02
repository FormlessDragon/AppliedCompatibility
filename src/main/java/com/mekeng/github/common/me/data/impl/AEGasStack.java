package com.mekeng.github.common.me.data.impl;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import com.mekeng.github.common.me.data.IAEGasStack;
import com.mekeng.github.common.me.storage.IGasStorageChannel;
import io.netty.buffer.ByteBuf;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public final class AEGasStack implements IAEGasStack, Comparable<AEGasStack> {

    private final Gas gas;
    private long amount;
    private long countRequestable;
    private boolean craftable;

    private AEGasStack(final Gas gas, final long amount) {
        this.gas = gas;
        this.amount = amount;
    }

    private AEGasStack(final AEGasStack stack) {
        this(stack.gas, stack.amount);
        this.countRequestable = stack.countRequestable;
        this.craftable = stack.craftable;
    }

    public static AEGasStack of(final GasStack stack) {
        if (stack == null || stack.getGas() == null || stack.amount <= 0) {
            return null;
        }
        return new AEGasStack(stack.getGas(), stack.amount);
    }

    public static IAEGasStack of(final NBTTagCompound data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        final GasStack stack = GasStack.readFromNBT(data);
        final AEGasStack gasStack = of(stack);
        if (gasStack == null) {
            return null;
        }
        if (data.hasKey("Cnt")) {
            gasStack.setStackSize(data.getLong("Cnt"));
        }
        gasStack.setCountRequestable(data.getLong("Req"));
        gasStack.setCraftable(data.getBoolean("Craft"));
        return gasStack;
    }

    public static IAEGasStack of(final ByteBuf data) {
        final Gas gas = GasRegistry.getGas(data.readInt());
        final long amount = data.readLong();
        final long countRequestable = data.readLong();
        final boolean craftable = data.readBoolean();
        final AEGasStack stack = of(new GasStack(gas, Math.clamp(amount, 0, Integer.MAX_VALUE)));
        if (stack == null) {
            return null;
        }
        stack.setStackSize(amount);
        stack.setCountRequestable(countRequestable);
        stack.setCraftable(craftable);
        return stack;
    }

    @Override
    public GasStack getGasStack() {
        return new GasStack(this.gas, Math.clamp(this.amount, 0, Integer.MAX_VALUE));
    }

    @Override
    public Gas getGas() {
        return this.gas;
    }

    @Override
    public long getStackSize() {
        return this.amount;
    }

    @Override
    public IAEGasStack setStackSize(final long size) {
        this.amount = Math.max(0, size);
        return this;
    }

    @Override
    public long getCountRequestable() {
        return this.countRequestable;
    }

    @Override
    public IAEGasStack setCountRequestable(final long countRequestable) {
        this.countRequestable = Math.max(0, countRequestable);
        return this;
    }

    @Override
    public boolean isCraftable() {
        return this.craftable;
    }

    @Override
    public IAEGasStack setCraftable(final boolean isCraftable) {
        this.craftable = isCraftable;
        return this;
    }

    @Override
    public IAEGasStack reset() {
        this.amount = 0;
        this.countRequestable = 0;
        this.craftable = false;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return this.amount > 0 || this.countRequestable > 0 || this.craftable;
    }

    @Override
    public void incStackSize(final long amount) {
        setStackSize(this.amount + amount);
    }

    @Override
    public void decStackSize(final long amount) {
        setStackSize(this.amount - amount);
    }

    @Override
    public void incCountRequestable(final long amount) {
        setCountRequestable(this.countRequestable + amount);
    }

    @Override
    public void decCountRequestable(final long amount) {
        setCountRequestable(this.countRequestable - amount);
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        getGasStack().write(data);
        data.setLong("Cnt", this.amount);
        data.setLong("Req", this.countRequestable);
        data.setBoolean("Craft", this.craftable);
    }

    @Override
    public boolean fuzzyComparison(final IAEGasStack other, final FuzzyMode mode) {
        return other != null && this.gas == other.getGas();
    }

    @Override
    public void writeToPacket(final ByteBuf data) {
        data.writeInt(GasRegistry.getGasID(this.gas));
        data.writeLong(this.amount);
        data.writeLong(this.countRequestable);
        data.writeBoolean(this.craftable);
    }

    @Override
    public IAEGasStack copy() {
        return new AEGasStack(this);
    }

    @Override
    public IAEGasStack empty() {
        return new AEGasStack(this.gas, 0);
    }

    @Override
    public void add(final IAEGasStack stack) {
        if (stack == null) {
            return;
        }
        if (this.gas != stack.getGas()) {
            throw new IllegalArgumentException("Cannot add a different gas stack");
        }
        incStackSize(stack.getStackSize());
        incCountRequestable(stack.getCountRequestable());
        setCraftable(this.craftable || stack.isCraftable());
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public IStorageChannel<IAEGasStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IGasStorageChannel.class);
    }

    @Override
    public ItemStack asItemStackRepresentation() {
        return ItemStack.EMPTY;
    }

    @Override
    public int compareTo(final AEGasStack other) {
        return other.gas.getName().compareTo(this.gas.getName());
    }

    @Override
    public int hashCode() {
        return this.gas.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IAEGasStack stack) {
            return this.gas == stack.getGas();
        }
        if (obj instanceof GasStack stack) {
            return this.gas == stack.getGas();
        }
        return false;
    }

    @Override
    public String toString() {
        return this.amount + "x" + this.gas.getName();
    }
}
