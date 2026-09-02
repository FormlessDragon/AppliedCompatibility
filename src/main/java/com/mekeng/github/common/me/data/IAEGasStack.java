package com.mekeng.github.common.me.data;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import io.netty.buffer.ByteBuf;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public interface IAEGasStack extends IAEStack<IAEGasStack> {

    @Override
    IAEGasStack copy();

    GasStack getGasStack();

    Gas getGas();

    long getStackSize();

    IAEGasStack setStackSize(long size);

    long getCountRequestable();

    IAEGasStack setCountRequestable(long countRequestable);

    boolean isCraftable();

    IAEGasStack setCraftable(boolean isCraftable);

    IAEGasStack reset();

    boolean isMeaningful();

    void incStackSize(long amount);

    void decStackSize(long amount);

    void incCountRequestable(long amount);

    void decCountRequestable(long amount);

    void writeToNBT(NBTTagCompound data);

    boolean fuzzyComparison(IAEGasStack other, FuzzyMode mode);

    void writeToPacket(ByteBuf data);

    IAEGasStack empty();

    void add(IAEGasStack stack);

    boolean isItem();

    boolean isFluid();

    IStorageChannel<IAEGasStack> getChannel();

    ItemStack asItemStackRepresentation();
}
