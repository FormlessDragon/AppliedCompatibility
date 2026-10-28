package com.mekeng.github.common.me.inventory;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;

public interface IGasInventory {

    int size();

    boolean usable(int slot);

    GasTank[] tanks();

    GasStack getGasStack(int slot);

    int addGas(int slot, GasStack gas, boolean simulate);

    GasStack removeGas(int slot, GasStack gas, boolean simulate);

    GasStack removeGas(int slot, int amount, boolean simulate);

    void setGas(int slot, GasStack gas);

    void setCap(int capacity);
}
