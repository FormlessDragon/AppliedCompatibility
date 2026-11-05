package appeng.container;

import appeng.helpers.InventoryAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import appeng.container.slot.SlotFake;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;

public class AEBaseContainer extends Container {
    private final InventoryPlayer inventoryPlayer;
    private final Object anchor;
    private boolean validContainer = true;

    public AEBaseContainer(final InventoryPlayer ip, final Object anchor) {
        this.inventoryPlayer = ip;
        this.anchor = anchor;
    }

    public InventoryPlayer getInventoryPlayer() { return inventoryPlayer; }
    public Object getTarget() { return anchor; }
    public TileEntity getTileEntity() { return anchor instanceof TileEntity tile ? tile : null; }
    public boolean isValidContainer() { return validContainer; }
    public void setValidContainer(boolean valid) { validContainer = valid; }

    public void verifyPermissions(appeng.api.config.SecurityPermissions permission, boolean requirePower) {
        if (inventoryPlayer.player.world.isRemote) return;
        if (anchor instanceof TileEntity tile) {
            // AE2S exposes its native location permission check rather than
            // AE2UEL's removed per-action security service.
            validContainer &= ae2.util.Platform.hasPermissions(
                new ae2.api.util.DimensionalBlockPos(tile.getWorld(), tile.getPos()), inventoryPlayer.player);
        }
        if (requirePower && anchor instanceof ae2.api.networking.security.IActionHost host) {
            var node = host.getActionableNode();
            validContainer &= node != null && node.isPowered();
        }
    }

    protected void bindPlayerInventory(final InventoryPlayer inventory, final int offsetX, final int offsetY) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(inventory, column + row * 9 + 9,
                    8 + column * 18 + offsetX, offsetY + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new Slot(inventory, column, 8 + column * 18 + offsetX, offsetY + 58));
        }
    }

    public void doAction(final EntityPlayerMP player, final InventoryAction action, final int index, final long id) {
        if (index < 0 || index >= inventorySlots.size()) return;
        final Slot slot = inventorySlots.get(index);
        if (!(slot instanceof SlotFake)) return;
        final ItemStack current = slot.getStack();
        if (action == InventoryAction.PICKUP_OR_SET_DOWN) {
            slot.putStack(player.inventory.getItemStack().copy());
        } else if (!current.isEmpty()) {
            final ItemStack changed = current.copy();
            long amount = changed.getCount();
            amount = switch (action) {
                case PLACE_SINGLE -> amount + 1;
                case PICKUP_SINGLE -> Math.max(1, amount - 1);
                case DOUBLE -> amount * 2;
                case HALVE -> Math.max(1, amount / 2);
                default -> amount;
            };
            changed.setCount((int) Math.min(Integer.MAX_VALUE, amount));
            slot.putStack(changed);
        }
        detectAndSendChanges();
    }

    @Override
    public ItemStack slotClick(int index, int button, ClickType type, EntityPlayer player) {
        if (index >= 0 && index < inventorySlots.size() && inventorySlots.get(index) instanceof SlotFake) {
            final Slot slot = inventorySlots.get(index);
            if (type == ClickType.PICKUP || type == ClickType.QUICK_MOVE) {
                ItemStack marker = player.inventory.getItemStack().copy();
                if (button == 1 && !marker.isEmpty()) marker.setCount(1);
                slot.putStack(marker);
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(index, button, type, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= inventorySlots.size()) return ItemStack.EMPTY;
        final Slot source = inventorySlots.get(index);
        if (!source.canTakeStack(player) || !source.getHasStack() || source instanceof SlotFake) return ItemStack.EMPTY;
        ItemStack remaining = source.getStack();
        ItemStack original = remaining.copy();
        final boolean fromPlayer = source.inventory == inventoryPlayer;
        for (Slot target : inventorySlots) {
            if (target == source || target instanceof SlotFake || (target.inventory == inventoryPlayer) == fromPlayer
                || !target.isItemValid(remaining)) continue;
            ItemStack existing = target.getStack();
            int limit = Math.min(target.getItemStackLimit(remaining), remaining.getMaxStackSize());
            if (existing.isEmpty()) {
                int moved = Math.min(limit, remaining.getCount());
                if (moved <= 0) continue;
                target.putStack(remaining.splitStack(moved));
            } else if (ItemStack.areItemsEqual(existing, remaining) && ItemStack.areItemStackTagsEqual(existing, remaining)) {
                int moved = Math.min(Math.max(0, limit - existing.getCount()), remaining.getCount());
                existing.grow(moved);
                remaining.shrink(moved);
                target.onSlotChanged();
            }
            if (remaining.isEmpty()) break;
        }
        if (remaining.getCount() == original.getCount()) return ItemStack.EMPTY;
        if (remaining.isEmpty()) source.putStack(ItemStack.EMPTY); else source.onSlotChanged();
        source.onTake(player, remaining);
        return original;
    }

    protected void updateHeld(final EntityPlayerMP player) {
        player.connection.sendPacket(new SPacketSetSlot(-1, -1, player.inventory.getItemStack()));
        detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(final EntityPlayer playerIn) {
        if (!validContainer) return false;
        if (anchor instanceof TileEntity tile) {
            return !tile.isInvalid() && tile.getWorld() == playerIn.world
                && playerIn.world.getTileEntity(tile.getPos()) == tile
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5,
                    tile.getPos().getZ() + 0.5) <= 64;
        }
        return true;
    }
}
