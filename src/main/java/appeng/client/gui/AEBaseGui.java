package appeng.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import appeng.client.gui.widgets.GuiCustomSlot;
import appeng.container.slot.SlotFake;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AEBaseGui extends GuiContainer {
    protected final List<GuiCustomSlot> guiSlots = new ArrayList<>();

    public AEBaseGui(final Container container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && isShiftKeyDown()) {
            mouseWheelEvent(Mouse.getEventX() * width / mc.displayWidth,
                height - Mouse.getEventY() * height / mc.displayHeight - 1, Integer.signum(wheel));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    protected void mouseWheelEvent(int mouseX, int mouseY, int wheel) {
        Slot slot = getSlot(mouseX, mouseY);
        if (slot instanceof SlotFake) {
            InventoryAction action = isCtrlKeyDown()
                ? (wheel > 0 ? InventoryAction.DOUBLE : InventoryAction.HALVE)
                : (wheel > 0 ? InventoryAction.PLACE_SINGLE : InventoryAction.PICKUP_SINGLE);
            NetworkHandler.instance().sendToServer(new PacketInventoryAction(action, slot.slotNumber, 0));
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    public void drawSlot(final Slot slot) {
        super.drawSlot(slot);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        drawBG(guiLeft, guiTop, mouseX, mouseY);
        for (GuiCustomSlot slot : guiSlots) slot.drawBackground(guiLeft, guiTop);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawFG(guiLeft, guiTop, mouseX, mouseY);
    }

    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {}
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        for (GuiCustomSlot slot : guiSlots) {
            if (slot.isSlotEnabled()) slot.drawContent(mc, mouseX, mouseY, partialTicks);
        }
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
        renderHoveredToolTip(mouseX, mouseY);
        for (GuiCustomSlot slot : guiSlots) {
            String message = slot.getMessage();
            if (slot.isVisible() && message != null && isPointInRegion(slot.xPos(), slot.yPos(),
                slot.getWidth(), slot.getHeight(), mouseX, mouseY)) {
                drawHoveringText(Arrays.asList(message.split("\n")), mouseX, mouseY, fontRenderer);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        for (GuiCustomSlot slot : guiSlots) {
            if (slot.isSlotEnabled() && isPointInRegion(slot.xPos(), slot.yPos(), slot.getWidth(), slot.getHeight(), mouseX, mouseY)
                && slot.canClick(mc.player)) {
                slot.slotClicked(mc.player.inventory.getItemStack(), button);
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    protected Slot getSlot(int mouseX, int mouseY) {
        for (Slot slot : inventorySlots.inventorySlots) {
            if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY)) return slot;
        }
        return null;
    }

    public List<GuiCustomSlot> getGuiSlots() { return guiSlots; }
    public void bindTexture(String file) { bindTexture("appliedenergistics2", file); }
    public void bindTexture(String namespace, String file) {
        mc.getTextureManager().bindTexture(new ResourceLocation(namespace, "textures/" + file));
    }
}
