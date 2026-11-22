package appeng.fluids.client.render;

import appeng.api.storage.data.IAEFluidStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Legacy labels retain bucket units while using the native AE2S font renderer. */
@SideOnly(Side.CLIENT)
public final class FluidStackSizeRenderer {
    public void renderStackSize(FontRenderer font, IAEFluidStack stack, int x, int y) {
        if (stack == null || stack.getStackSize() <= 0) return;
        long amount = stack.getStackSize();
        String label = amount < 1_000_000
            ? java.math.BigDecimal.valueOf(amount, 3).stripTrailingZeros().toPlainString()
            : ae2.util.ReadableNumberConverter.format(amount / 1000, 4);
        ae2.client.gui.me.common.StackSizeRenderer.renderSizeLabel(font, x, y, label);
    }
}
