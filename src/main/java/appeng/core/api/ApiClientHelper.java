package appeng.core.api;

import appeng.api.config.IncludeExclude;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IClientHelper;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ApiClientHelper implements IClientHelper {

    @Override
    public <T extends IAEStack<T>> void addCellInformation(final ICellInventoryHandler<T> handler,
                                                           final List<String> lines) {
        if (handler == null) {
            throw new IllegalArgumentException("Cell inventory handler is required");
        }
        if (lines == null) {
            throw new IllegalArgumentException("Tooltip line list is required");
        }
        final ICellInventory<T> cellInventory = handler.getCellInv();
        if (cellInventory == null) {
            return;
        }
        lines.add("Bytes: " + cellInventory.getUsedBytes() + " / " + cellInventory.getTotalBytes());
        lines.add("Types: " + cellInventory.getStoredItemTypes() + " / " + cellInventory.getTotalItemTypes());
        if (handler.isPreformatted()) {
            final String mode = handler.getIncludeExcludeMode() == IncludeExclude.WHITELIST ? "Included" : "Excluded";
            lines.add("Partitioned - " + mode + ' ' + (handler.isFuzzy() ? "Fuzzy" : "Precise"));
            appendConfiguredAmounts(handler, cellInventory, lines);
        } else {
            appendStoredAmounts(cellInventory, lines);
        }
    }

    private static <T extends IAEStack<T>> void appendConfiguredAmounts(final ICellInventoryHandler<T> handler,
                                                                        final ICellInventory<T> cellInventory,
                                                                        final List<String> lines) {
        final IItemList<T> available = cellInventory.getChannel().createList();
        cellInventory.getAvailableItems(available);
        for (int i = 0; i < cellInventory.getConfigInventory().getSlots(); i++) {
            final ItemStack stack = cellInventory.getConfigInventory().getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (cellInventory.getChannel() instanceof IItemStorageChannel) {
                final IAEItemStack configured = AEItemStack.fromItemStack(stack);
                final IAEStack<?> stored = ((IItemList) available).findPrecise(configured);
                lines.add(stack.getDisplayName() + ": " + (stored == null ? 0 : stored.getStackSize()));
            } else if (cellInventory.getChannel() instanceof IFluidStorageChannel) {
                final IAEFluidStack configured = (IAEFluidStack) cellInventory.getChannel().createStack(stack);
                if (configured != null) {
                    final IAEStack<?> stored = ((IItemList) available).findPrecise(configured);
                    lines.add(stack.getDisplayName() + ": " + (stored == null ? "0 mB" : stored.getStackSize() + " mB"));
                }
            }
        }
    }

    private static <T extends IAEStack<T>> void appendStoredAmounts(final ICellInventory<T> cellInventory,
                                                                    final List<String> lines) {
        final IItemList<T> available = cellInventory.getChannel().createList();
        cellInventory.getAvailableItems(available);
        for (final T stack : available) {
            if (stack instanceof IAEItemStack itemStack) {
                lines.add(itemStack.getDefinition().getDisplayName() + ": " + itemStack.getStackSize());
            } else if (stack instanceof IAEFluidStack fluidStack) {
                lines.add(fluidStack.getFluidStack().getLocalizedName() + ": " + fluidStack.getStackSize() + " mB");
            } else {
                lines.add(stack.asItemStackRepresentation().getDisplayName() + ": " + stack.getStackSize());
            }
        }
    }
}
