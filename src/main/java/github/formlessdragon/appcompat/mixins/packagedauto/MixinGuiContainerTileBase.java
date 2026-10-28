package github.formlessdragon.appcompat.mixins.packagedauto;

import ae2.api.behaviors.ContainerItemStrategies;
import ae2.api.behaviors.EmptyingAction;
import ae2.api.stacks.AmountFormat;
import ae2.api.stacks.GenericStack;
import ae2.client.gui.me.common.StackSizeRenderer;
import ae2.core.localization.ButtonToolTips;
import ae2.core.localization.Tooltips;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedEncoderGhostBridge;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternStacks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thelm.packagedauto.client.gui.GuiContainerTileBase;
import thelm.packagedauto.client.gui.GuiItemAmountSpecifying;
import thelm.packagedauto.network.PacketHandler;
import thelm.packagedauto.network.packet.PacketSetItemStack;
import thelm.packagedauto.slot.SlotFalseCopy;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Mixin(value = GuiContainerTileBase.class, remap = false)
public abstract class MixinGuiContainerTileBase extends GuiContainer {

    @Unique
    private final Set<Slot> appcompat$dragClick = new ReferenceOpenHashSet<>();
    @Unique
    private final Set<Slot> appcompat$dragClickSent = new ReferenceOpenHashSet<>();

    public MixinGuiContainerTileBase(final Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Shadow
    public abstract int getItemAmountSpecificationLimit(Slot slot);

    /**
     * @author circulation
     * @reason 对齐新 AE 标记语义：左键写物品，右键写容器内容；空手左键仍打开数量编辑
     */
    @Overwrite
    protected void handleMouseClick(final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (type == ClickType.PICKUP && slot instanceof SlotFalseCopy && slot.isEnabled()) {
            final ItemStack carried = this.mc.player.inventory.getItemStack();
            if (carried.isEmpty() && mouseButton == 0 && !slot.getStack().isEmpty()) {
                this.mc.displayGuiScreen(new GuiItemAmountSpecifying((GuiContainerTileBase<?>) (Object) this,
                    this.mc.player.inventory, slot.slotNumber, slot.getStack(), getItemAmountSpecificationLimit(slot)));
                return;
            }
            if (!carried.isEmpty()) {
                final ItemStack stack = PackagedEncoderGhostBridge.toClickStack(carried, mouseButton);
                if (!stack.isEmpty()) {
                    PacketHandler.INSTANCE.sendToServer(new PacketSetItemStack(slot.slotNumber, stack));
                    this.appcompat$dragClick.add(slot);
                    this.appcompat$dragClickSent.add(slot);
                    return;
                }
            }
        }
        super.handleMouseClick(slot, slotId, mouseButton, type);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        this.appcompat$dragClick.clear();
        this.appcompat$dragClickSent.clear();
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        final Slot slot = appcompat$findSlot(mouseX, mouseY);
        final ItemStack carried = this.mc.player.inventory.getItemStack();
        if (slot instanceof SlotFalseCopy && slot.isEnabled() && !carried.isEmpty()) {
            this.appcompat$dragClick.add(slot);
            if (this.appcompat$dragClick.size() > 1) {
                for (final Slot draggedSlot : this.appcompat$dragClick) {
                    if (this.appcompat$dragClickSent.add(draggedSlot)) {
                        appcompat$sendFalseCopyStack(draggedSlot, PackagedEncoderGhostBridge.toClickStack(carried, clickedMouseButton));
                    }
                }
            }
            return;
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Unique
    private Slot appcompat$findSlot(final int mouseX, final int mouseY) {
        for (final Slot slot : this.inventorySlots.inventorySlots) {
            if (slot.isEnabled() && appcompat$isHovering(slot, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    @Unique
    private boolean appcompat$isHovering(final Slot slot, final int mouseX, final int mouseY) {
        final int x = mouseX - this.guiLeft;
        final int y = mouseY - this.guiTop;
        return x >= slot.xPos && x < slot.xPos + 16 && y >= slot.yPos && y < slot.yPos + 16;
    }

    @Unique
    private void appcompat$sendFalseCopyStack(final Slot slot, final ItemStack stack) {
        if (!stack.isEmpty()) {
            PacketHandler.INSTANCE.sendToServer(new PacketSetItemStack(slot.slotNumber, stack));
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void appcompat$drawFalseCopyEmptyingTooltip(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        if (!(this.hoveredSlot instanceof SlotFalseCopy)) {
            return;
        }
        final ItemStack carried = this.mc.player.inventory.getItemStack();
        if (!carried.isEmpty()) {
            appcompat$drawEmptyingTooltip(carried, mouseX, mouseY);
            return;
        }
        final ItemStack ghostStack = PackagedEncoderGhostBridge.getGhostTooltipStack();
        if (!ghostStack.isEmpty() && appcompat$drawEmptyingTooltip(ghostStack, mouseX, mouseY)) {
            return;
        }
        final GenericStack ghostGenericStack = PackagedEncoderGhostBridge.getGhostTooltipGenericStack();
        if (ghostGenericStack == null) {
            return;
        }
        final List<String> tooltip = new ObjectArrayList<>();
        PackagedPatternStacks.addTooltip(tooltip, ghostGenericStack);
        drawHoveringText(tooltip, mouseX, mouseY);
    }

    @Unique
    private boolean appcompat$drawEmptyingTooltip(final ItemStack actionStack, final int mouseX, final int mouseY) {
        final EmptyingAction emptyingAction = ContainerItemStrategies.getEmptyingAction(actionStack);
        if (emptyingAction == null) {
            return false;
        }
        final List<String> tooltip = new ObjectArrayList<>(2);
        for (final var line : Tooltips.getEmptyingTooltip(ButtonToolTips.SetAction, actionStack, emptyingAction)) {
            tooltip.add(line.getFormattedText());
        }
        drawHoveringText(tooltip, mouseX, mouseY);
        return true;
    }

    @Override
    protected void drawSlot(final Slot slot) {
        super.drawSlot(slot);
        final ItemStack stack = slot.getStack();
        final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
        if (genericStack == null) {
            return;
        }
        final String amount = genericStack.what().formatAmount(genericStack.amount(), AmountFormat.SLOT);
        StackSizeRenderer.renderSizeLabel(this.fontRenderer, slot.xPos, slot.yPos, amount, false);
    }

    @Override
    public List<String> getItemToolTip(final ItemStack stack) {
        final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
        final List<String> tooltip = super.getItemToolTip(stack);
        if (genericStack != null) {
            PackagedPatternStacks.addTooltip(tooltip, genericStack);
            return tooltip;
        }
        return tooltip;
    }
}
