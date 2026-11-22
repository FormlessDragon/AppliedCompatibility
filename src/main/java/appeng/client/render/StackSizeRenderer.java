package appeng.client.render;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.client.gui.FontRenderer;

public class StackSizeRenderer {

    public StackSizeRenderer() {
    }

    public void renderStackSize(final FontRenderer fontRenderer, final IAEItemStack aeStack, final int xPos, final int yPos) {
        if (aeStack == null) return;
        String label = aeStack.getStackSize() > 0
            ? ae2.util.ReadableNumberConverter.format(aeStack.getStackSize(), 4)
            : aeStack.isCraftable() ? "+" : "0";
        ae2.client.gui.me.common.StackSizeRenderer.renderSizeLabel(fontRenderer, xPos, yPos, label);
    }
}
