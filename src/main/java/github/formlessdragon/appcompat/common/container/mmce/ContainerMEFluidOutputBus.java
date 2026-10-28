package github.formlessdragon.appcompat.common.container.mmce;

import ae2.api.inventories.PlatformInventoryWrapper;
import ae2.container.AEBaseContainer;
import ae2.container.guisync.GuiSync;
import ae2.core.definitions.AEItems;
import github.kasuminova.mmce.common.tile.MEFluidOutputBus;
import github.kasuminova.mmce.common.util.AEFluidInventoryUpgradeable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collections;

public class ContainerMEFluidOutputBus extends AEBaseContainer {

    private final MEFluidOutputBus owner;
    private final MirroredMachineUpgradeInventory upgrades;

    @GuiSync(40)
    public SyncedFluidList tankFluids = new SyncedFluidList(Collections.emptyList());
    @GuiSync(41)
    public int capacity = 8000;

    public ContainerMEFluidOutputBus(final MEFluidOutputBus owner, final EntityPlayer player) {
        super(player.inventory, owner);
        this.owner = owner;

        final PlatformInventoryWrapper upgradesBacking = new PlatformInventoryWrapper(owner.getInventoryByName("upgrades"));
        this.upgrades = new MirroredMachineUpgradeInventory(owner.getVisualItemStack().getItem(), 5, upgradesBacking);
        this.setupUpgrades(this.upgrades);
        for (int i = 0; i < 5; i++) {
            this.getSlot(i).xPos = 187;
            this.getSlot(i).yPos = 8 + 18 * i;
        }

        this.addPlayerInventorySlots(8, 112);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            if (this.upgrades != null) {
                this.upgrades.pullFromBacking();
            }
            refreshCapacity();
            this.tankFluids = SyncedFluidList.from(this.owner.getTanks());
        }
        super.broadcastChanges();
    }

    public MEFluidOutputBus getOwner() {
        return this.owner;
    }

    private void refreshCapacity() {
        final int installed = this.upgrades == null ? 0 : this.upgrades.getInstalledUpgrades(AEItems.CAPACITY_CARD.item());
        this.capacity = (int) (Math.pow(4, installed + 1) * 2000);
        if (this.owner.getTanks() instanceof AEFluidInventoryUpgradeable upgradeable) {
            upgradeable.setCapacity(this.capacity);
        }
    }
}
