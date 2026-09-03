package appeng.fluids.container;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.container.implementations.ContainerUpgradeable;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.Map;

public class ContainerFluidConfigurable extends ContainerUpgradeable implements IFluidSyncContainer {

    public ContainerFluidConfigurable(final InventoryPlayer ip, final IUpgradeableHost te) {
        super(ip, te);
    }

    @Override
    public void receiveFluidSlots(final Map<Integer, IAEFluidStack> fluids) {
    }
}
