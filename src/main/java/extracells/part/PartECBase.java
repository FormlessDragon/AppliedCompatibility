package extracells.part;

import net.minecraft.item.ItemStack;

public class PartECBase {

    public void initializePart(final ItemStack partStack) {
        if (partStack == null || partStack.isEmpty()) {
            throw new IllegalArgumentException("ExtraCells part stack is required");
        }
    }
}
