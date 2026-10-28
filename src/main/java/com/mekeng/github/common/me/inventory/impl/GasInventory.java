package com.mekeng.github.common.me.inventory.impl;

import com.mekeng.github.common.me.inventory.IGasInventory;
import com.mekeng.github.common.me.inventory.IGasInventoryHost;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import net.minecraft.nbt.NBTTagCompound;

public record GasInventory(GasTank[] tanks) implements IGasInventory {

    public GasInventory(final int size, final int capacity, final IGasInventoryHost host) {
        final IGasInventoryHost actualHost = host == null ? IGasInventoryHost.empty() : host;
        this(new GasTank[size]);
        for (int i = 0; i < size; i++) {
            final int slot = i;
            this.tanks[i] = new NotifiableGasTank(capacity, () -> actualHost.onGasInventoryChanged(this, slot));
        }
    }

    public GasInventory(final int size, final IGasInventoryHost host) {
        this(size, Integer.MAX_VALUE, host);
    }

    public GasInventory(final int size, final int capacity) {
        this(size, capacity, null);
    }

    public GasInventory(final int size) {
        this(size, Integer.MAX_VALUE, null);
    }

    private static GasStack copyGas(final GasStack gas) {
        return gas == null ? null : gas.copy();
    }

    public NBTTagCompound save() {
        final NBTTagCompound data = new NBTTagCompound();
        for (int i = 0; i < this.tanks.length; i++) {
            final NBTTagCompound tankTag = new NBTTagCompound();
            this.tanks[i].write(tankTag);
            data.setTag("#" + i, tankTag);
        }
        return data;
    }

    public void load(final NBTTagCompound data) {
        for (int i = 0; i < this.tanks.length; i++) {
            if (!data.hasKey("#" + i, 10)) {
                this.tanks[i].setGas(null);
                continue;
            }
            final GasTank loaded = GasTank.readFromNBT(data.getCompoundTag("#" + i));
            if (loaded == null) {
                this.tanks[i].setGas(null);
                continue;
            }
            this.tanks[i].setMaxGas(loaded.getMaxGas());
            this.tanks[i].setGas(copyGas(loaded.getGas()));
        }
    }

    @Override
    public int size() {
        return this.tanks.length;
    }

    @Override
    public boolean usable(final int slot) {
        return slot >= 0 && slot < this.tanks.length && this.tanks[slot] != null && this.tanks[slot].getMaxGas() > 0;
    }

    @Override
    public GasStack getGasStack(final int slot) {
        if (!usable(slot)) {
            return null;
        }
        return copyGas(this.tanks[slot].getGas());
    }

    @Override
    public int addGas(final int slot, final GasStack gas, final boolean simulate) {
        if (!usable(slot) || gas == null || gas.amount <= 0) {
            return 0;
        }
        return this.tanks[slot].receive(copyGas(gas), !simulate);
    }

    @Override
    public GasStack removeGas(final int slot, final GasStack gas, final boolean simulate) {
        if (!usable(slot) || gas == null || gas.amount <= 0 || !this.tanks[slot].canDraw(gas.getGas())) {
            return null;
        }
        return this.tanks[slot].draw(gas.amount, !simulate);
    }

    @Override
    public GasStack removeGas(final int slot, final int amount, final boolean simulate) {
        if (!usable(slot) || amount <= 0) {
            return null;
        }
        return this.tanks[slot].draw(amount, !simulate);
    }

    @Override
    public void setGas(final int slot, final GasStack gas) {
        if (slot < 0 || slot >= this.tanks.length) {
            return;
        }
        this.tanks[slot].setGas(copyGas(gas));
    }

    @Override
    public void setCap(final int capacity) {
        for (final GasTank tank : this.tanks) {
            tank.setMaxGas(capacity);
            final GasStack gas = tank.getGas();
            if (gas != null && gas.amount > capacity) {
                tank.setGas(gas.withAmount(capacity));
            }
        }
    }

    private static final class NotifiableGasTank extends GasTank {

        private final Runnable notifier;

        private NotifiableGasTank(final int maxGas, final Runnable notifier) {
            super(maxGas);
            this.notifier = notifier;
        }

        private static boolean sameGasAndAmount(final GasStack left, final GasStack right) {
            if (left == null || right == null) {
                return left == right;
            }
            return left.amount == right.amount && left.isGasEqual(right);
        }

        @Override
        public int receive(final GasStack stack, final boolean doReceive) {
            final int received = super.receive(stack, doReceive);
            if (doReceive && received > 0) {
                this.notifier.run();
            }
            return received;
        }

        @Override
        public GasStack draw(final int amount, final boolean doDraw) {
            final GasStack drawn = super.draw(amount, doDraw);
            if (doDraw && drawn != null && drawn.amount > 0) {
                if (this.stored != null && this.stored.amount <= 0) {
                    this.stored = null;
                }
                this.notifier.run();
            }
            return drawn;
        }

        @Override
        public void setMaxGas(final int maxGas) {
            final int before = getMaxGas();
            super.setMaxGas(maxGas);
            if (before != maxGas) {
                this.notifier.run();
            }
        }

        @Override
        public GasStack getGas() {
            return copyGas(super.getGas());
        }

        @Override
        public void setGas(final GasStack stack) {
            final GasStack before = this.stored;
            super.setGas(copyGas(stack));
            if (!sameGasAndAmount(before, this.stored)) {
                this.notifier.run();
            }
        }
    }
}
