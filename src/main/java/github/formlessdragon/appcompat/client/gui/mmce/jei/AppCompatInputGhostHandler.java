package github.formlessdragon.appcompat.client.gui.mmce.jei;

import ae2.container.slot.FakeSlot;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEItemInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus.ItemConfigSlot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.awt.Rectangle;
import java.util.List;

public class AppCompatInputGhostHandler implements IGhostIngredientHandler<GuiMEItemInputBus> {

    @Override
    public <I> List<Target<I>> getTargets(final @NonNull GuiMEItemInputBus gui, final @NonNull I ingredient, final boolean doStart) {
        final List<Target<I>> targets = new ObjectArrayList<>();
        final ContainerMEItemInputBus container = (ContainerMEItemInputBus) gui.inventorySlots;
        for (final Slot slot : container.inventorySlots) {
            if (slot instanceof ItemConfigSlot itemConfigSlot && !GuiMEItemInputBus.toItemFilterStack(itemConfigSlot, ingredient).isEmpty()) {
                targets.add(new SlotTarget<>(gui, itemConfigSlot));
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }

    private record SlotTarget<I>(GuiMEItemInputBus gui, FakeSlot slot) implements Target<I> {

        @Override
        public Rectangle getArea() {
            return new Rectangle(this.gui.getGuiLeft() + this.slot.xPos, this.gui.getGuiTop() + this.slot.yPos, 16, 16);
        }

        @Override
        public void accept(final @NonNull I ingredient) {
            final ItemStack stack = GuiMEItemInputBus.toItemFilterStack((ItemConfigSlot) this.slot, ingredient);
            if (!stack.isEmpty()) {
                this.slot.setFilterTo(stack);
            }
        }
    }
}
