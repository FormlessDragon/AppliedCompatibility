package appeng.core.features.registries;

import ae2.api.features.GridLinkables;
import ae2.api.features.IGridLinkableHandler;
import ae2.api.implementations.blockentities.IWirelessAccessPoint;
import ae2.core.gui.GuiOpener;
import ae2.core.gui.locator.GuiHostLocators;
import ae2.items.tools.powered.WirelessTerminals;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.features.IWirelessTermRegistry;
import appeng.core.sync.GuiBridge;
import github.formlessdragon.appcompat.bridge.ae.LegacyWirelessTerminalHost;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class WirelessRegistry implements IWirelessTermRegistry {

    private final ObjectArrayList<IWirelessTermHandler> handlers = new ObjectArrayList<>();

    @Override
    public void registerWirelessHandler(final IWirelessTermHandler handler) {
        Objects.requireNonNull(handler, "handler");
        for (final IWirelessTermHandler registered : this.handlers) {
            if (registered == handler) {
                return;
            }
        }
        this.handlers.add(handler);
        if (handler instanceof Item item && GridLinkables.get(item) == null) {
            GridLinkables.register(item, new LinkHandler(handler));
        }
    }

    @Override
    public boolean isWirelessTerminal(final ItemStack stack) {
        return this.getWirelessTerminalHandler(stack) != null;
    }

    @Override
    public IWirelessTermHandler getWirelessTerminalHandler(final ItemStack stack) {
        if (!stack.isEmpty()) {
            for (final IWirelessTermHandler handler : this.handlers) {
                if (handler.canHandle(stack)) {
                    return handler;
                }
            }
        }
        return null;
    }

    @Override
    public void openWirelessTerminalGui(final ItemStack stack, final World world, final EntityPlayer player) {
        if (world.isRemote || !this.performCheck(stack, player)) {
            return;
        }
        final IWirelessTermHandler handler = this.getWirelessTerminalHandler(stack);
        if (!(handler.getGuiHandler(stack) instanceof GuiBridge bridge)) {
            throw new IllegalArgumentException("Unsupported legacy wireless GUI handler for " + stack.getItem().getRegistryName());
        }
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (player.inventory.getStackInSlot(slot) == stack) {
                GuiOpener.openItemGui(player, bridge.getGuiKey(), GuiHostLocators.forInventorySlot(slot));
                return;
            }
        }
    }

    @Override
    public boolean checkRange(final ItemStack stack, final EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (player.inventory.getStackInSlot(slot) == stack) {
                final IWirelessTermHandler handler = this.getWirelessTerminalHandler(stack);
                if (handler == null || !(handler.getGuiHandler(stack) instanceof GuiBridge bridge)) {
                    return false;
                }
                return new LegacyWirelessTerminalHost(stack.getItem(), player,
                    GuiHostLocators.forInventorySlot(slot), handler, bridge.getGuiKey()).getLinkStatus().connected();
            }
        }
        return false;
    }

    @Override
    public boolean performCheck(final ItemStack stack, final EntityPlayer player) {
        final IWirelessTermHandler handler = this.getWirelessTerminalHandler(stack);
        return handler != null && handler.hasPower(player, 0, stack) && this.checkRange(stack, player);
    }

    private record LinkHandler(IWirelessTermHandler handler) implements IGridLinkableHandler {

        @Override
        public boolean canLink(final ItemStack stack) {
            return this.handler.canHandle(stack);
        }

        @Override
        public void link(final ItemStack stack, final World world, final BlockPos pos) {
            if (!(world.getTileEntity(pos) instanceof IWirelessAccessPoint)) {
                throw new IllegalArgumentException("Legacy wireless terminal requires an AE wireless access point at " + pos);
            }
            final NBTTagCompound link = new NBTTagCompound();
            link.setInteger(WirelessTerminals.TAG_LINK_DIM, world.provider.getDimension());
            link.setInteger(WirelessTerminals.TAG_LINK_X, pos.getX());
            link.setInteger(WirelessTerminals.TAG_LINK_Y, pos.getY());
            link.setInteger(WirelessTerminals.TAG_LINK_Z, pos.getZ());
            WirelessTerminals.getOrCreateTag(stack).setTag(WirelessTerminals.TAG_LINK, link);
        }

        @Override
        public void unlink(final ItemStack stack) {
            final NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                tag.removeTag(WirelessTerminals.TAG_LINK);
            }
            this.handler.setEncryptionKey(stack, "", "");
        }
    }
}
