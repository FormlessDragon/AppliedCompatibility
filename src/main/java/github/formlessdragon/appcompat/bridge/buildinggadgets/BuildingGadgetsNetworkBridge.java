package github.formlessdragon.appcompat.bridge.buildinggadgets;

import ae2.api.config.Actionable;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.networking.energy.IEnergyService;
import ae2.api.networking.security.IActionHost;
import ae2.api.networking.security.IActionSource;
import ae2.api.networking.storage.IStorageService;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.KeyCounter;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.me.helpers.IGridConnectedTile;
import ae2.api.util.DimensionalBlockPos;
import ae2.util.Platform;
import com.direwolf20.buildinggadgets.common.tools.NetworkIO;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BuildingGadgetsNetworkBridge {

    private BuildingGadgetsNetworkBridge() {
    }

    @Nullable
    public static IItemHandler createHandler(final TileEntity tile, final IInWorldGridNodeHost host,
                                             final EntityPlayer player, final NetworkIO.Operation operation) {
        Objects.requireNonNull(tile, "tile");
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(operation, "operation");
        if (tile.getWorld() == null
            || !Platform.hasPermissions(new DimensionalBlockPos(tile.getWorld(), tile.getPos()), player)) {
            return null;
        }

        final IGridNode node = findNode(host);
        if (node == null || !node.isActive()) {
            return null;
        }

        final IGrid grid = node.grid();
        final IStorageService storageService = grid.getStorageService();
        final IEnergyService energyService = grid.getEnergyService();
        final IActionSource source = host instanceof IActionHost actionHost
            ? IActionSource.ofPlayer(player, actionHost)
            : IActionSource.ofPlayer(player);
        return new NetworkItemHandler(storageService.getInventory(), storageService.getCachedInventory(), energyService,
            source, operation);
    }

    @Nullable
    private static IGridNode findNode(final IInWorldGridNodeHost host) {
        if (host instanceof IGridConnectedTile connectedTile) {
            if (!connectedTile.getMainNode().isReady()) {
                return null;
            }
            return connectedTile.getMainNode().getNode();
        }

        final IGridNode internalNode = host.getGridNode(null);
        if (internalNode != null) {
            return internalNode;
        }

        IGridNode fallback = null;
        for (final EnumFacing side : EnumFacing.VALUES) {
            final IGridNode candidate = host.getGridNode(side);
            if (candidate == null) {
                continue;
            }
            if (candidate.isActive()) {
                return candidate;
            }
            if (fallback == null) {
                fallback = candidate;
            }
        }
        return fallback;
    }

    private static final class NetworkItemHandler implements IItemHandler {

        private final MEStorage storage;
        private final IEnergyService energy;
        private final IActionSource source;
        @Nullable
        private final ObjectArrayList<AEItemKey> extractionKeys;
        @Nullable
        private final LongArrayList extractionAmounts;

        private NetworkItemHandler(final MEStorage storage, final KeyCounter availableStacks,
                                   final IEnergyService energy, final IActionSource source,
                                   final NetworkIO.Operation operation) {
            this.storage = storage;
            this.energy = energy;
            this.source = source;
            if (operation != NetworkIO.Operation.EXTRACT) {
                this.extractionKeys = null;
                this.extractionAmounts = null;
                return;
            }

            this.extractionKeys = new ObjectArrayList<>();
            this.extractionAmounts = new LongArrayList();
            for (final Object2LongMap.Entry<AEKey> entry : availableStacks) {
                if (entry.getLongValue() <= 0L || !(entry.getKey() instanceof AEItemKey itemKey)) {
                    continue;
                }
                this.extractionKeys.add(itemKey);
                this.extractionAmounts.add(entry.getLongValue());
            }
        }

        @Override
        public int getSlots() {
            return this.extractionKeys == null ? 1 : this.extractionKeys.size();
        }

        @Override
        public ItemStack getStackInSlot(final int slot) {
            checkSlot(slot);
            if (this.extractionKeys == null) {
                return ItemStack.EMPTY;
            }

            final long amount = this.extractionAmounts.getLong(slot);
            if (amount <= 0L) {
                return ItemStack.EMPTY;
            }
            final int displayedAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
            return this.extractionKeys.get(slot).toStack(displayedAmount);
        }

        @Override
        public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
            checkSlot(slot);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            final AEItemKey key = AEItemKey.of(stack);
            if (key == null) {
                return stack.copy();
            }

            final long inserted = StorageHelper.poweredInsert(this.energy, this.storage, key, stack.getCount(),
                this.source, simulate ? Actionable.SIMULATE : Actionable.MODULATE);
            if (inserted <= 0L) {
                return stack.copy();
            }
            if (inserted >= stack.getCount()) {
                return ItemStack.EMPTY;
            }

            final ItemStack remaining = stack.copy();
            remaining.shrink((int) inserted);
            return remaining;
        }

        @Override
        public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
            checkSlot(slot);
            if (amount <= 0 || this.extractionKeys == null) {
                return ItemStack.EMPTY;
            }

            final long available = this.extractionAmounts.getLong(slot);
            if (available <= 0L) {
                return ItemStack.EMPTY;
            }

            final long requested = Math.min((long) amount, available);
            final long extracted = StorageHelper.poweredExtraction(this.energy, this.storage,
                this.extractionKeys.get(slot), requested, this.source,
                simulate ? Actionable.SIMULATE : Actionable.MODULATE);
            if (extracted <= 0L) {
                return ItemStack.EMPTY;
            }
            if (!simulate) {
                this.extractionAmounts.set(slot, available - extracted);
            }
            return this.extractionKeys.get(slot).toStack((int) extracted);
        }

        @Override
        public int getSlotLimit(final int slot) {
            checkSlot(slot);
            return Integer.MAX_VALUE;
        }

        private void checkSlot(final int slot) {
            final int slots = getSlots();
            if (slot < 0 || slot >= slots) {
                throw new IndexOutOfBoundsException("Building Gadgets AE network slot " + slot
                    + " is outside [0, " + slots + ')');
            }
        }
    }
}
