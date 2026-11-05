package appeng.fluids.helper;

import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.IAEFluidTank;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayerMP;
import appeng.core.sync.network.NetworkHandler;
import github.formlessdragon.appcompat.bridge.ae.LegacyFluidSlotsMessage;

public class FluidSyncHelper {
    private final IAEFluidTank tank;
    private final int offset;
    private final Map<Integer, NBTTagCompound> previous = new LinkedHashMap<>();

    public FluidSyncHelper(final IAEFluidTank tank, final int idOffset) {
        this.tank = tank;
        this.offset = idOffset;
    }

    public void sendDiff(final Iterable<?> listeners) {
        Map<Integer, IAEFluidStack> changes = new LinkedHashMap<>();
        for (int slot = 0; slot < tank.getSlots(); slot++) {
            IAEFluidStack fluid = tank.getFluidInSlot(slot);
            NBTTagCompound data = null;
            if (fluid != null) { data = new NBTTagCompound(); fluid.writeToNBT(data); }
            if (!previous.containsKey(slot) || !Objects.equals(previous.get(slot), data)) {
                changes.put(slot + offset, fluid);
                previous.put(slot, data);
            }
        }
        send(listeners, changes);
    }

    public void sendFull(final Iterable<?> listeners) {
        Map<Integer, IAEFluidStack> values = new LinkedHashMap<>();
        for (int slot = 0; slot < tank.getSlots(); slot++) values.put(slot + offset, tank.getFluidInSlot(slot));
        send(listeners, values);
    }

    public void readPacket(final Map<Integer, IAEFluidStack> data) {
        data.forEach((id, fluid) -> {
            int slot = id - offset;
            if (slot >= 0 && slot < tank.getSlots()) tank.setFluidInSlot(slot, fluid == null ? null : fluid.copy());
        });
    }

    private void send(Iterable<?> listeners, Map<Integer, IAEFluidStack> fluids) {
        if (fluids.isEmpty()) return;
        for (Object listener : listeners) if (listener instanceof EntityPlayerMP player) {
            NetworkHandler.instance().channel().sendTo(new LegacyFluidSlotsMessage(player.openContainer.windowId, fluids), player);
        }
    }
}
