package appeng.container.slot;

import net.minecraftforge.items.IItemHandler;

public class SlotDisabled extends AppEngSlot {

    public SlotDisabled(final IItemHandler inv, final int idx, final int x, final int y) {
        super(inv, idx, x, y);
    }

    @Override public boolean isItemValid(net.minecraft.item.ItemStack stack) { return false; }
    @Override public boolean canTakeStack(net.minecraft.entity.player.EntityPlayer player) { return false; }
}
