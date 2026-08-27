package github.formlessdragon.appcompat.bridge.enderioae;

import ae2.api.features.GridLinkables;
import ae2.api.features.IGridLinkableHandler;
import ae2.api.implementations.blockentities.IWirelessAccessPoint;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.energy.IEnergyService;
import ae2.api.networking.security.IActionSource;
import ae2.api.networking.storage.IStorageService;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.KeyCounter;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import com.enderio.core.common.util.ItemUtil;
import ae2.api.util.DimensionalBlockPos;
import ae2.items.tools.powered.WirelessTerminals;
import ae2.tile.networking.TileWirelessAccessPoint;
import ae2.util.Platform;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.function.BiConsumer;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public final class ConduitSwapperNetworkBridge {

    private ConduitSwapperNetworkBridge() {
    }

    public static void registerGridLinkable(final Item item) {
        if (GridLinkables.get(item) == null) {
            GridLinkables.register(item, new LinkHandler());
        }
    }

    public static boolean hasAccess(final EntityPlayer player, final ItemStack stack) {
        return player != null && context(player, stack) != null;
    }

    public static void appendBindingTooltip(final ItemStack stack, final List<String> tooltip) {
        if (!hasWirelessLink(stack)) {
            tooltip.add(I18n.translateToLocal("appcompat.conduitswapper.tooltip.unlinked"));
        } else {
            tooltip.add(I18n.translateToLocal("appcompat.conduitswapper.tooltip.bound"));
        }
    }

    public static int countStack(final EntityPlayer player, final ItemStack stack, final ItemStack query) {

        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(query);
        if (context == null || key == null) {
            return 0;
        }
        final long amount = context.cachedInventory.get(key);
        return clamp(amount);
    }

    public static void forEachCandidate(final EntityPlayer player, final ItemStack stack,
                                        final Class<? extends IConduit> conduitClass, final ItemStack source,
                                        final BiConsumer<ItemStack, Integer> consumer) {
        final Context context = context(player, stack);
        if (context == null) {
            return;
        }
        for (final Object2LongMap.Entry<AEKey> entry : context.cachedInventory) {
            if (entry.getLongValue() <= 0 || !(entry.getKey() instanceof AEItemKey key)) {
                continue;
            }
            final ItemStack candidate = key.toStack(clamp(entry.getLongValue()));
            if (candidate.getItem() instanceof IConduitItem conduitItem
                && conduitItem.getBaseConduitType() == conduitClass
                && !ItemUtil.areStacksEqual(candidate, source)) {
                consumer.accept(candidate, clamp(entry.getLongValue()));
            }
        }
    }

    public static int extract(final EntityPlayer player, final ItemStack stack, final ItemStack requested,
                              final int amount) {
        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(requested);
        if (context == null || key == null || amount <= 0) {
            return 0;
        }
        final long extracted = StorageHelper.poweredExtraction(context.energy, context.storage, key, amount,
            IActionSource.ofPlayer(player));
        return clamp(extracted);
    }

    public static ItemStack store(final EntityPlayer player, final ItemStack stack, final ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(input);
        if (context == null || key == null) {
            return input;
        }
        final long inserted = StorageHelper.poweredInsert(context.energy, context.storage, key, input.getCount(),
            IActionSource.ofPlayer(player));
        if (inserted >= input.getCount()) {
            return ItemStack.EMPTY;
        }
        final ItemStack remaining = input.copy();
        remaining.shrink((int) inserted);
        return remaining;
    }

    private static Context context(final EntityPlayer player, final ItemStack stack) {
        if (!hasWirelessLink(stack)) {
            return null;
        }
        final NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return null;
        }
        final NBTTagCompound binding = tag.getCompoundTag(WirelessTerminals.TAG_LINK);
        final int dimension = binding.getInteger(WirelessTerminals.TAG_LINK_DIM);
        final World world = player.world.provider.getDimension() == dimension
            ? player.world : DimensionManager.getWorld(dimension);
        if (!(world instanceof WorldServer)) {
            return null;
        }
        final BlockPos pos = new BlockPos(binding.getInteger(WirelessTerminals.TAG_LINK_X),
            binding.getInteger(WirelessTerminals.TAG_LINK_Y),
            binding.getInteger(WirelessTerminals.TAG_LINK_Z));
        if (!Platform.hasPermissions(new DimensionalBlockPos(world, pos), player)) {
            return null;
        }
        final TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IWirelessAccessPoint accessPoint) || !accessPoint.isActive()) {
            return null;
        }
        final IGridNode node = accessPoint.getActionableNode();
        if (node == null || !node.isActive()) {
            return null;
        }
        final IGrid grid = accessPoint.getGrid();
        if (grid == null || !withinRange(player, accessPoint)) {
            return null;
        }
        return new Context(grid.getStorageService(), grid.getEnergyService());
    }

    private static boolean hasWirelessLink(final ItemStack stack) {
        final NBTTagCompound tag = stack.isEmpty() ? null : stack.getTagCompound();
        return tag != null && tag.hasKey(WirelessTerminals.TAG_LINK, 10);
    }

    private static boolean withinRange(final EntityPlayer player, final IWirelessAccessPoint accessPoint) {
        if (accessPoint.getLocation().getLevel() != player.world) {
            return false;
        }
        final BlockPos pos = accessPoint.getLocation().getPos();
        final double dx = pos.getX() + 0.5D - player.posX;
        final double dy = pos.getY() + 0.5D - player.posY;
        final double dz = pos.getZ() + 0.5D - player.posZ;
        final double range = accessPoint.getRange();
        return range > 0.0D && dx * dx + dy * dy + dz * dz <= range * range;
    }

    private static int clamp(final long amount) {
        return (int) Math.clamp(amount, 0L, Integer.MAX_VALUE);
    }

    private static final class Context {
        private final MEStorage storage;
        private final KeyCounter cachedInventory;
        private final IEnergyService energy;

        private Context(final IStorageService storageService, final IEnergyService energy) {
            this.storage = storageService.getInventory();
            this.cachedInventory = storageService.getCachedInventory();
            this.energy = energy;
        }
    }

    private static final class LinkHandler implements IGridLinkableHandler {
        @Override
        public boolean canLink(final ItemStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public void link(final ItemStack stack, final World world, final BlockPos pos) {
            final TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof TileWirelessAccessPoint accessPoint) || !accessPoint.isActive()) {
                throw new IllegalArgumentException("Conduit swapper can only link to an active AE wireless access point");
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
        }
    }
}
