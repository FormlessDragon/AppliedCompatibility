package github.formlessdragon.appcompat.client.gui.mmce;

import ae2.client.gui.style.GuiStyleManager;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEGasOutputBus;
import github.formlessdragon.appcompat.common.container.mmce.SyncedGasList;
import github.kasuminova.mmce.common.tile.MEGasOutputBus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.ramidzkh.mekae2.ae2.AEGasKey;
import mekanism.api.gas.GasStack;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collections;
import java.util.List;

public class GuiMEGasOutputBus extends GuiMEBusBase<ContainerMEGasOutputBus> {

    private static final int TANKS = 9;

    private final ContainerMEGasOutputBus container;

    public GuiMEGasOutputBus(final MEGasOutputBus te, final EntityPlayer player) {
        super(new ContainerMEGasOutputBus(te, player), player.inventory,
            GuiStyleManager.loadStyleDoc("/screens/appcompat_me_gas_output_bus.json"));
        this.container = (ContainerMEGasOutputBus) this.inventorySlots;
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawGasBars(this.container.tankGases, 26, 68);
    }

    @Override
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        final int relX = mouseX - this.guiLeft;
        final int relY = mouseY - this.guiTop;
        for (int i = 0; i < TANKS; i++) {
            final int x = 8 + 18 * i;
            if (relX >= x && relX < x + 16 && relY >= 26 && relY < 94) {
                final List<String> tooltip = buildGasTooltip(this.container.tankGases, i, getCapacity());
                if (!tooltip.isEmpty()) {
                    this.drawHoveringText(tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    private void drawGasBars(final SyncedGasList gases, final int y, final int height) {
        for (int i = 0; i < TANKS; i++) {
            final AEGasKey key = gases.keyAt(i);
            final long amount = gases.amountAt(i);
            if (key == null || amount <= 0) {
                continue;
            }
            final int x = 8 + 18 * i;
            final int capacity = getCapacity();
            final int filledHeight = Math.max(1, (int) Math.round((double) amount * height / capacity));
            drawGasIconColumn(key, x, y + height - filledHeight, filledHeight);
        }
    }

    private void drawGasIconColumn(final AEGasKey key, final int x, final int y, final int height) {
        final GasStack stack = key.toStack(1);
        final var icon = key.getGas().getIcon();
        final TextureAtlasSprite sprite = icon == null ? this.mc.getTextureMapBlocks().getMissingSprite()
            : this.mc.getTextureMapBlocks().getAtlasSprite(icon.toString());
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        MekanismRenderer.color(stack);
        try {
            final int remainder = height % 16;
            if (remainder > 0) {
                this.drawTexturedModalRect(x, y, sprite, 16, remainder);
            }
            for (int i = 0; i < height / 16; i++) {
                this.drawTexturedModalRect(x, y + remainder + i * 16, sprite, 16, 16);
            }
        } finally {
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
        }
    }

    private List<String> buildGasTooltip(final SyncedGasList gases, final int index, final int capacity) {
        final AEGasKey key = gases.keyAt(index);
        if (key == null) {
            return Collections.emptyList();
        }
        final List<String> tooltip = new ObjectArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        tooltip.add(gases.amountAt(index) + " / " + capacity + " mB");
        return tooltip;
    }

    private int getCapacity() {
        return Math.max(1, this.container.capacity);
    }
}
