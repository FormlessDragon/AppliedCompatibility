package github.formlessdragon.appcompat.client.gui.mmce;

import ae2.api.stacks.AEFluidKey;
import ae2.client.gui.style.FluidBlitter;
import ae2.client.gui.style.GuiStyleManager;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEFluidOutputBus;
import github.formlessdragon.appcompat.common.container.mmce.SyncedFluidList;
import github.kasuminova.mmce.common.tile.MEFluidOutputBus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collections;
import java.util.List;

public class GuiMEFluidOutputBus extends GuiMEBusBase<ContainerMEFluidOutputBus> {

    private static final int TANKS = 9;

    private final ContainerMEFluidOutputBus container;

    public GuiMEFluidOutputBus(final MEFluidOutputBus te, final EntityPlayer player) {
        super(new ContainerMEFluidOutputBus(te, player), player.inventory,
            GuiStyleManager.loadStyleDoc("/screens/appcompat_me_fluid_output_bus.json"));
        this.container = (ContainerMEFluidOutputBus) this.inventorySlots;
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawFluidBars(this.container.tankFluids, 26, 68);
    }

    @Override
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        final int relX = mouseX - this.guiLeft;
        final int relY = mouseY - this.guiTop;
        for (int i = 0; i < TANKS; i++) {
            final int x = 8 + 18 * i;
            if (relX >= x && relX < x + 16 && relY >= 26 && relY < 94) {
                final List<String> tooltip = buildFluidTooltip(this.container.tankFluids, i, getCapacity());
                if (!tooltip.isEmpty()) {
                    this.drawHoveringText(tooltip, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    private void drawFluidBars(final SyncedFluidList fluids, final int y, final int height) {
        for (int i = 0; i < TANKS; i++) {
            final AEFluidKey key = fluids.keyAt(i);
            final long amount = fluids.amountAt(i);
            if (key == null || amount <= 0) {
                continue;
            }
            final int x = 8 + 18 * i;
            final int capacity = getCapacity();
            final int filledHeight = Math.max(1, (int) Math.round((double) amount * height / capacity));
            FluidBlitter.create(key).dest(x, y + height - filledHeight, 16, filledHeight).blit();
        }
    }

    private List<String> buildFluidTooltip(final SyncedFluidList fluids, final int index, final int capacity) {
        final AEFluidKey key = fluids.keyAt(index);
        if (key == null) {
            return Collections.emptyList();
        }
        final List<String> tooltip = new ObjectArrayList<>();
        tooltip.add(key.getDisplayName().getFormattedText());
        tooltip.add(fluids.amountAt(index) + " / " + capacity + " mB");
        return tooltip;
    }

    private int getCapacity() {
        return Math.max(1, this.container.capacity);
    }
}
