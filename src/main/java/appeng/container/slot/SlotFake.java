package appeng.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jspecify.annotations.NonNull;

public class SlotFake extends AppEngSlot implements IJEITargetSlot {

    public SlotFake(final IItemHandler inv, final int idx, final int x, final int y) {
        super(inv, idx, x, y);
    }

    @Override
    public @NonNull ItemStack getStack() {
        return super.getStack();
    }

    @Override
    public void putStack(final @NonNull ItemStack stack) {
        super.putStack(stack.copy());
    }

    @Override public boolean canTakeStack(net.minecraft.entity.player.EntityPlayer player) { return false; }
}
