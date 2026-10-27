package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.config.Actionable;
import ae2.api.config.PowerMultiplier;
import ae2.api.config.Setting;
import ae2.api.config.Settings;
import ae2.api.config.SortDir;
import ae2.api.config.SortOrder;
import ae2.api.config.ViewItems;
import ae2.api.config.YesNo;
import ae2.api.implementations.blockentities.IViewCellStorage;
import ae2.api.implementations.blockentities.IWirelessAccessPoint;
import ae2.api.implementations.guiobjects.IPortableTerminal;
import ae2.api.implementations.guiobjects.ItemGuiHost;
import ae2.api.inventories.ISegmentedInventory;
import ae2.api.inventories.InternalInventory;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.security.IActionHost;
import ae2.api.networking.security.IActionSource;
import ae2.api.networking.storage.IStorageService;
import ae2.api.stacks.AEKey;
import ae2.api.storage.ILinkStatus;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.api.storage.SupplierStorage;
import ae2.api.util.DimensionalBlockPos;
import ae2.api.util.IConfigManager;
import ae2.api.util.KeyTypeSelection;
import ae2.api.util.KeyTypeSelectionHost;
import ae2.container.GuiIds;
import ae2.container.ISubGui;
import ae2.core.AEConfig;
import ae2.core.gui.GuiOpener;
import ae2.core.gui.locator.ItemGuiHostLocator;
import ae2.core.localization.GuiText;
import ae2.core.localization.PlayerMessages;
import ae2.helpers.IPatternTerminalGuiHost;
import ae2.helpers.IPatternTerminalLogicHost;
import ae2.items.tools.powered.WirelessTerminals;
import ae2.me.storage.NullInventory;
import ae2.parts.encoding.PatternEncodingLogic;
import ae2.parts.reporting.CraftingTerminalPart;
import ae2.tile.networking.TileWirelessAccessPoint;
import ae2.util.Platform;
import ae2.util.inv.AppEngInternalInventory;
import ae2.util.inv.InternalInventoryHost;
import appeng.api.features.IWirelessTermHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.Locale;

public final class LegacyWirelessTerminalHost extends ItemGuiHost<Item> implements IPortableTerminal, IActionHost,
    KeyTypeSelectionHost, ISegmentedInventory, IViewCellStorage, InternalInventoryHost,
    IPatternTerminalGuiHost, IPatternTerminalLogicHost {

    private static final String DATA_TAG = "appcompat_wireless";
    private final IWirelessTermHandler handler;
    private final GuiIds.GuiKey guiKey;
    private final ItemStack inventoryStack;
    private final MEStorage storage = new SupplierStorage(this::connectedStorage);
    private final IConfigManager config;
    private IWirelessAccessPoint accessPoint;
    private ILinkStatus linkStatus = ILinkStatus.ofDisconnected();
    private long lastUpdate = Long.MIN_VALUE;
    private double lastX;
    private double lastY;
    private double lastZ;
    private World lastWorld;
    private double distance;
    private AppEngInternalInventory crafting;
    private AppEngInternalInventory viewCells;
    private PatternEncodingLogic logic;
    private boolean loading;

    public LegacyWirelessTerminalHost(final Item item, final EntityPlayer player, final ItemGuiHostLocator locator,
                                     final IWirelessTermHandler handler, final GuiIds.GuiKey guiKey) {
        super(item, player, locator);
        this.handler = handler;
        this.guiKey = guiKey;
        this.inventoryStack = this.getItemStack();
        this.config = IConfigManager.builder(this::getItemStack)
            .registerSetting(Settings.SORT_BY, SortOrder.NAME)
            .registerSetting(Settings.VIEW_MODE, ViewItems.ALL)
            .registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING)
            .registerSetting(Settings.PATTERN_AUTO_FILL, YesNo.NO)
            .build();
        final NBTTagCompound tag = this.inventoryStack.getTagCompound();
        if (!this.isClientSide() && tag != null && !tag.hasKey(IConfigManager.EXPORTED_SETTINGS, 10)) {
            final NBTTagCompound legacySettings = new NBTTagCompound();
            for (final Setting<?> setting : this.config.getSettings()) {
                final String oldName = setting.getName().toUpperCase(Locale.ROOT);
                if (tag.hasKey(oldName, 8)) {
                    legacySettings.setString(setting.getName(), tag.getString(oldName));
                }
            }
            this.config.readFromNBT(legacySettings);
        }
    }

    private MEStorage connectedStorage() {
        final IGridNode node = this.getActionableNode();
        return node == null ? NullInventory.of() : node.grid().getStorageService().getInventory();
    }

    private void refreshConnection() {
        if (this.isClientSide()) {
            return;
        }
        final EntityPlayer player = this.getPlayer();
        final long time = player.world.getTotalWorldTime();
        if (this.lastUpdate == time && this.lastWorld == player.world && this.lastX == player.posX
            && this.lastY == player.posY && this.lastZ == player.posZ && this.getItemStack() == this.inventoryStack) {
            return;
        }
        this.lastUpdate = time;
        this.lastWorld = player.world;
        this.lastX = player.posX;
        this.lastY = player.posY;
        this.lastZ = player.posZ;
        this.accessPoint = null;
        this.distance = Double.MAX_VALUE;
        final ItemStack stack = this.getItemStack();
        if (stack != this.inventoryStack || !this.handler.canHandle(stack)
            || !this.handler.hasPower(player, 0, stack)) {
            this.linkStatus = ILinkStatus.ofDisconnected(GuiText.OutOfPower.text());
            return;
        }
        final NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(WirelessTerminals.TAG_LINK, 10)) {
            this.linkStatus = ILinkStatus.ofDisconnected(PlayerMessages.DeviceNotLinked.text());
            return;
        }
        final NBTTagCompound link = tag.getCompoundTag(WirelessTerminals.TAG_LINK);
        final World world = DimensionManager.getWorld(link.getInteger(WirelessTerminals.TAG_LINK_DIM));
        final BlockPos pos = new BlockPos(link.getInteger(WirelessTerminals.TAG_LINK_X),
            link.getInteger(WirelessTerminals.TAG_LINK_Y), link.getInteger(WirelessTerminals.TAG_LINK_Z));
        if (world == null || !world.isBlockLoaded(pos)
            || !Platform.hasPermissions(new DimensionalBlockPos(world, pos), this.getPlayer())
            || !(world.getTileEntity(pos) instanceof IWirelessAccessPoint linked)) {
            this.linkStatus = ILinkStatus.ofDisconnected(PlayerMessages.LinkedNetworkNotFound.text());
            return;
        }
        final IGrid grid = linked.getGrid();
        if (grid != null) {
            for (final TileWirelessAccessPoint candidate : grid.getMachines(TileWirelessAccessPoint.class)) {
                if (!candidate.isActive() || candidate.getLocation().getLevel() != this.getPlayer().world) {
                    continue;
                }
                final BlockPos location = candidate.getLocation().getPos();
                final double dx = location.getX() - this.getPlayer().posX;
                final double dy = location.getY() - this.getPlayer().posY;
                final double dz = location.getZ() - this.getPlayer().posZ;
                final double squared = dx * dx + dy * dy + dz * dz;
                if (squared < this.distance && squared < candidate.getRange() * candidate.getRange()) {
                    this.accessPoint = candidate;
                    this.distance = squared;
                }
            }
        }
        this.distance = Math.sqrt(this.distance);
        this.linkStatus = this.accessPoint == null ? ILinkStatus.ofDisconnected(PlayerMessages.OutOfRange.text())
            : this.consumeIdlePower(Actionable.SIMULATE) ? ILinkStatus.ofConnected()
            : ILinkStatus.ofDisconnected(GuiText.OutOfPower.text());
    }

    @Override
    public boolean isValid() {
        final ItemStack stack = this.getItemStack();
        return super.isValid() && (this.isClientSide() || stack == this.inventoryStack) && this.handler.canHandle(stack);
    }

    @Override
    public void tick() {
        this.refreshConnection();
        if (this.accessPoint != null && !this.consumeIdlePower(Actionable.MODULATE)) {
            this.accessPoint = null;
            this.linkStatus = ILinkStatus.ofDisconnected(GuiText.OutOfPower.text());
        }
    }

    @Override
    protected double getPowerDrainPerTick() {
        return this.accessPoint == null ? 0 : AEConfig.instance().wireless_getDrainRate(this.distance);
    }

    @Override
    public double extractAEPower(final double amount, final Actionable mode, final PowerMultiplier multiplier) {
        if (!(amount > 0) || !Double.isFinite(amount) || this.isClientSide()) {
            return 0;
        }
        final ItemStack stack = this.getItemStack();
        if (this.getItem() instanceof LegacyWirelessEnergySource source) {
            return source.appcompat$extractWirelessPower(this.getPlayer(), stack, amount, mode);
        }
        if (!this.handler.hasPower(this.getPlayer(), amount, stack)) {
            return 0;
        }
        return mode == Actionable.SIMULATE || this.handler.usePower(this.getPlayer(), amount, stack) ? amount : 0;
    }

    @Override
    public IGridNode getActionableNode() {
        this.refreshConnection();
        final IGridNode node = this.accessPoint == null ? null : this.accessPoint.getActionableNode();
        return this.linkStatus.connected() && node != null && node.isActive() ? node : null;
    }

    @Override
    public MEStorage getInventory() {
        return this.storage;
    }

    @Override
    public IStorageService getGridStorageService() {
        final IGridNode node = this.getActionableNode();
        return node == null ? null : node.grid().getStorageService();
    }

    @Override
    public ILinkStatus getLinkStatus() {
        this.refreshConnection();
        return this.linkStatus;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.config;
    }

    @Override
    public KeyTypeSelection getKeyTypeSelection() {
        return KeyTypeSelection.forStack(this.getItemStack(), ignored -> true);
    }

    @Override
    public InternalInventory getSubInventory(final ResourceLocation id) {
        if (!CraftingTerminalPart.INV_CRAFTING.equals(id)) {
            return null;
        }
        if (this.crafting == null) {
            this.crafting = new AppEngInternalInventory(this, 9);
            this.loading = true;
            try {
                this.crafting.readFromNBT(this.data(), "crafting");
            } finally {
                this.loading = false;
            }
        }
        return this.crafting;
    }

    @Override
    public InternalInventory getViewCellStorage() {
        if (this.viewCells == null) {
            this.viewCells = new AppEngInternalInventory(this, 5);
            this.loading = true;
            try {
                this.viewCells.readFromNBT(this.data(), "view_cells");
            } finally {
                this.loading = false;
            }
        }
        return this.viewCells;
    }

    @Override
    public PatternEncodingLogic getLogic() {
        if (this.logic == null) {
            this.logic = new PatternEncodingLogic(this);
            this.loading = true;
            try {
                this.logic.readFromNBT(this.data().getCompoundTag("encoding"));
            } finally {
                this.loading = false;
            }
        }
        return this.logic;
    }

    private NBTTagCompound data() {
        final NBTTagCompound root = WirelessTerminals.getOrCreateTag(this.inventoryStack);
        if (!root.hasKey(DATA_TAG, 10)) {
            root.setTag(DATA_TAG, new NBTTagCompound());
        }
        return root.getCompoundTag(DATA_TAG);
    }

    @Override
    public void saveChangedInventory(final AppEngInternalInventory inventory) {
        if (!this.loading && !this.isClientSide()) {
            inventory.writeToNBT(this.data(), inventory == this.crafting ? "crafting" : "view_cells");
            this.getPlayer().inventory.markDirty();
        }
    }

    @Override
    public World getLevel() {
        return this.getPlayer().world;
    }

    @Override
    public void markForSave() {
        if (!this.loading && !this.isClientSide()) {
            final NBTTagCompound tag = new NBTTagCompound();
            this.logic.writeToNBT(tag);
            this.data().setTag("encoding", tag);
            this.getPlayer().inventory.markDirty();
        }
    }

    @Override
    public long insert(final EntityPlayer player, final AEKey what, final long amount, final Actionable mode) {
        return !this.isClientSide() && this.getLinkStatus().connected()
            ? StorageHelper.poweredInsert(this, this.storage, what, amount, IActionSource.ofPlayer(player), mode) : 0;
    }

    @Override
    public void returnToMainContainer(final EntityPlayer player, final ISubGui subGui) {
        GuiOpener.openItemGui(player, this.guiKey, this.getLocator(), true);
    }

    @Override
    public ItemStack getMainContainerIcon() {
        return this.getItemStack();
    }

    @Override
    public String getCloseHotkey() {
        return switch (this.guiKey) {
            case WIRELESS_CRAFTING_TERMINAL -> "wireless_crafting_terminal";
            case WIRELESS_PATTERN_ENCODING_TERMINAL -> "wireless_pattern_encoding_terminal";
            default -> "wireless_terminal";
        };
    }
}
