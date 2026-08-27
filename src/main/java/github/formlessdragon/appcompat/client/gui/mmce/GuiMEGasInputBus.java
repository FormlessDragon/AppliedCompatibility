package github.formlessdragon.appcompat.client.gui.mmce;

import ae2.api.stacks.GenericStack;
import ae2.client.gui.style.GuiStyleManager;
import ae2.integration.modules.hei.GenericIngredientHelper;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEGasInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEGasInputBus.GasConfigSlot;
import github.formlessdragon.appcompat.common.container.mmce.SyncedGasList;
import github.kasuminova.mmce.common.tile.MEGasInputBus;
import me.ramidzkh.mekae2.ae2.AEGasKey;
import mekanism.api.gas.GasStack;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GuiMEGasInputBus extends GuiMEBusBase<ContainerMEGasInputBus> {

    private static final int TANKS = 9;

    private final ContainerMEGasInputBus container;

    public GuiMEGasInputBus(final MEGasInputBus te, final EntityPlayer player) {
        super(new ContainerMEGasInputBus(te, player), player.inventory,
            GuiStyleManager.loadStyleDoc("/screens/appcompat_me_gas_input_bus.json"));
        this.container = (ContainerMEGasInputBus) this.inventorySlots;
    }

    private static boolean isGasIngredient(final Object ingredient) {
        if (ingredient instanceof GasStack stack) {
            return AEGasKey.of(stack) != null;
        }
        if (ingredient instanceof ItemStack stack) {
            return GasConfigSlot.isGasFilterStack(stack);
        }
        final GenericStack stack = GenericIngredientHelper.ingredientToStack(ingredient);
        return stack != null && stack.what() instanceof AEGasKey;
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawGasBars(this.container.tankGases, 53, 68);
    }

    @Override
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        final Slot slot = this.getSlotUnderMouse();
        if (slot instanceof GasConfigSlot) {
            final List<String> tooltip = buildConfigGasTooltip(slot.getSlotIndex());
            if (!tooltip.isEmpty()) {
                this.drawHoveringText(tooltip, mouseX, mouseY);
            }
            return;
        }
        final int relX = mouseX - this.guiLeft;
        final int relY = mouseY - this.guiTop;
        for (int i = 0; i < TANKS; i++) {
            final int x = 8 + 18 * i;
            if (relX >= x && relX < x + 16 && relY >= 53 && relY < 121) {
                final List<String> tooltip = buildGasTooltip(this.container.tankGases, i, getCapacity());
                if (!tooltip.isEmpty()) {
                    this.drawHoveringText(tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    private List<String> buildConfigGasTooltip(final int index) {
        final AEGasKey key = this.container.configGases.keyAt(index);
        if (key == null) {
            return Collections.emptyList();
        }
        final List<String> tooltip = new ArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        if (this.playerInventory.getItemStack().isEmpty()) {
            tooltip.add(TextFormatting.GRAY + I18n.format("gui.ae2.Clean"));
        }
        return tooltip;
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
        final List<String> tooltip = new ArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        tooltip.add(gases.amountAt(index) + " / " + capacity + " mB");
        return tooltip;
    }

    private int getCapacity() {
        return Math.max(1, this.container.capacity);
    }

    @Override
    public Collection<? extends Slot> getHEISlots(final Object ingredient) {
        if (!isGasIngredient(ingredient)) {
            return Collections.emptyList();
        }
        final List<Slot> slots = new ArrayList<>();
        for (final Slot slot : this.container.inventorySlots) {
            if (slot instanceof GasConfigSlot) {
                slots.add(slot);
            }
        }
        return slots;
    }
}
