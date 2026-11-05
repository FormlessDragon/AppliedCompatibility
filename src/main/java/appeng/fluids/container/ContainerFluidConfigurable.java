package appeng.fluids.container;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.container.implementations.ContainerUpgradeable;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.Map;

public abstract class ContainerFluidConfigurable extends ContainerUpgradeable implements IFluidSyncContainer {
    private appeng.fluids.helper.FluidSyncHelper sync;

    public abstract appeng.fluids.util.IAEFluidTank getFluidConfigInventory();

    private appeng.fluids.helper.FluidSyncHelper sync() {
        if (sync == null) sync = new appeng.fluids.helper.FluidSyncHelper(getFluidConfigInventory(), 0);
        return sync;
    }

    public ContainerFluidConfigurable(final InventoryPlayer ip, final IUpgradeableHost te) {
        super(ip, te);
    }

    @Override
    public void receiveFluidSlots(final Map<Integer, IAEFluidStack> fluids) {
        sync().readPacket(fluids);
    }

    @Override public void addListener(net.minecraft.inventory.IContainerListener listener) {
        super.addListener(listener);
        sync().sendFull(java.util.Collections.singleton(listener));
    }

    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!getInventoryPlayer().player.world.isRemote) sync().sendDiff(listeners);
    }
}
