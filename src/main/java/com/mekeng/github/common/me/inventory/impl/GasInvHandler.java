package com.mekeng.github.common.me.inventory.impl;

import com.mekeng.github.common.me.inventory.IExtendedGasHandler;
import com.mekeng.github.common.me.inventory.IGasInventory;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import net.minecraft.util.EnumFacing;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.EnumSet;

public class GasInvHandler implements IExtendedGasHandler {

    private final IGasInventory inventory;
    private final EnumSet<EnumFacing> validSide;

    public GasInvHandler(final IGasInventory inventory) {
        this(inventory, EnumSet.allOf(EnumFacing.class));
    }

    public GasInvHandler(final IGasInventory inventory, final EnumSet<EnumFacing> validSide) {
        this.inventory = inventory;
        this.validSide = validSide;
    }

    public void setSide(final Collection<EnumFacing> sides) {
        this.validSide.clear();
        this.validSide.addAll(sides);
    }

    @Override
    public int receiveGas(final EnumFacing side, final GasStack stack, final boolean doTransfer) {
        if (!checkSide(side) || stack == null || stack.amount <= 0) {
            return 0;
        }
        final GasStack remaining = stack.copy();
        for (int i = 0; i < this.inventory.size() && remaining.amount > 0; i++) {
            final int accepted = this.inventory.addGas(i, remaining, !doTransfer);
            remaining.amount -= accepted;
        }
        return stack.amount - remaining.amount;
    }

    @Override
    public GasStack drawGas(final EnumFacing side, final int amount, final boolean doTransfer) {
        if (!checkSide(side) || amount <= 0) {
            return null;
        }
        for (int i = 0; i < this.inventory.size(); i++) {
            final GasStack stored = this.inventory.getGasStack(i);
            if (stored != null && stored.amount > 0) {
                stored.amount = amount;
                return drawGas(side, stored, doTransfer);
            }
        }
        return null;
    }

    @Override
    public GasStack drawGas(final EnumFacing side, final GasStack stack, final boolean doTransfer) {
        if (!checkSide(side) || stack == null || stack.amount <= 0) {
            return null;
        }
        int remaining = stack.amount;
        int drawn = 0;
        for (int i = 0; i < this.inventory.size() && remaining > 0; i++) {
            final GasStack stored = this.inventory.getGasStack(i);
            if (stored == null || !stored.isGasEqual(stack)) {
                continue;
            }
            final GasStack request = stack.copy();
            request.amount = remaining;
            final GasStack extracted = this.inventory.removeGas(i, request, !doTransfer);
            if (extracted == null || extracted.amount <= 0) {
                continue;
            }
            drawn += extracted.amount;
            remaining -= extracted.amount;
        }
        return drawn <= 0 ? null : new GasStack(stack.getGas(), drawn);
    }

    @Override
    public GasStack drawGas(final GasStack gas, final boolean doTransfer) {
        return drawGas(null, gas, doTransfer);
    }

    @Override
    public boolean canReceiveGas(final EnumFacing side, final Gas gas) {
        if (!checkSide(side) || gas == null) {
            return false;
        }
        for (int i = 0; i < this.inventory.size(); i++) {
            if (!this.inventory.usable(i)) {
                continue;
            }
            final GasStack stored = this.inventory.getGasStack(i);
            final GasTank tank = this.inventory.tanks()[i];
            if (stored == null || stored.getGas() == gas && stored.amount < tank.getMaxGas()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canDrawGas(final EnumFacing side, final Gas gas) {
        if (!checkSide(side) || gas == null) {
            return false;
        }
        for (int i = 0; i < this.inventory.size(); i++) {
            final GasStack stored = this.inventory.getGasStack(i);
            if (stored != null && stored.getGas() == gas && stored.amount > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GasTankInfo @NonNull [] getTankInfo() {
        return this.inventory.tanks();
    }

    public boolean checkSide(final EnumFacing side) {
        return side == null || this.validSide.contains(side);
    }
}
