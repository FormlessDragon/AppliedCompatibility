package github.formlessdragon.appcompat.bridge.pneumaticcraft;

import ae2.api.crafting.IPatternDetails;
import ae2.api.networking.GridHelper;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridConnection;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IGridNodeListener;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.IStackWatcher;
import ae2.api.networking.crafting.ICraftingProvider;
import ae2.api.networking.crafting.ICraftingWatcherNode;
import ae2.api.networking.storage.IStorageWatcherNode;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.KeyCounter;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeStorageBridge;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Owns the new AE direct node used by a PneumaticCraft requester attached to an AE interface.
 */
public final class PneumaticCraftRequesterNode implements ICraftingProvider {

    private static final IGridNodeListener<PneumaticCraftRequesterNode> NODE_LISTENER =
        new IGridNodeListener<>() {
            @Override
            public void onSaveChanges(final PneumaticCraftRequesterNode owner, final IGridNode ignored) {
                owner.access.appcompat$markRequesterDirty();
            }
        };

    private final PneumaticCraftRequesterAccess access;
    private final PneumaticCraftRequesterWatcher craftingWatcher;
    private final PneumaticCraftRequesterWatcher storageWatcher;
    private IManagedGridNode managedNode;
    private IGridConnection connection;

    public PneumaticCraftRequesterNode(final PneumaticCraftRequesterAccess access) {
        this.access = access;
        this.craftingWatcher = new PneumaticCraftRequesterWatcher(this, true);
        this.storageWatcher = new PneumaticCraftRequesterWatcher(this, false);
    }

    public boolean attach(final World world, final BlockPos pos, final IGridNode interfaceNode) {
        if (interfaceNode == null) {
            return false;
        }
        boolean changed = false;
        if (this.managedNode == null) {
            this.managedNode = GridHelper.createManagedNode(this, NODE_LISTENER)
                                         .setInWorldNode(false)
                                         .setIdlePowerUsage(1.0D)
                                         .setVisualRepresentation(this.access.appcompat$getVisualItemStack());
            this.managedNode.addService(ICraftingProvider.class, this);
            this.managedNode.addService(ICraftingWatcherNode.class, this.craftingWatcher);
            this.managedNode.addService(IStorageWatcherNode.class, this.storageWatcher);
            changed = true;
        }
        if (!this.managedNode.isReady()) {
            this.managedNode.create(world, pos);
            changed = true;
        }
        final IGridNode requesterNode = this.managedNode.getNode();
        if (requesterNode == null) {
            return false;
        }
        if (this.connection != null && this.connection.getOtherSide(requesterNode) != interfaceNode) {
            this.connection.destroy();
            this.connection = null;
        }
        if (this.connection == null) {
            this.connection = GridHelper.createConnection(interfaceNode, requesterNode);
            changed = true;
        }
        if (changed) {
            refresh();
        }
        return true;
    }

    public void disconnect() {
        if (this.connection != null) {
            this.connection.destroy();
            this.connection = null;
        }
        if (this.managedNode != null) {
            this.managedNode.destroy();
            this.managedNode = null;
        }
    }

    public boolean isReady() {
        return this.managedNode != null && this.managedNode.isReady();
    }

    public void refresh() {
        if (this.managedNode == null || !this.managedNode.isReady()) {
            return;
        }
        ICraftingProvider.requestUpdate(this.managedNode);
        this.craftingWatcher.refresh();
        this.storageWatcher.refresh();
    }

    @Override
    public List<? extends IPatternDetails> getAvailablePatterns() {
        return Collections.emptyList();
    }

    @Override
    public boolean pushPattern(final IPatternDetails patternDetails, final KeyCounter[] inputHolder,
                               final int multiplier) {
        return false;
    }

    @Override
    public boolean canMergePatternPush(final IPatternDetails patternDetails) {
        return false;
    }

    @Override
    public int getMaxPatternPushMultiplier(final IPatternDetails patternDetails, final int maxMultiplier) {
        return 0;
    }

    @Override
    public boolean isBusy() {
        return true;
    }

    @Override
    public Set<AEKey> getEmitableItems() {
        final Set<AEKey> result = new ObjectOpenHashSet<>();
        for (final appeng.api.storage.data.IAEItemStack stack : this.access.appcompat$getProvidingItems()) {
            result.add(LegacyAeStorageBridge.toKey(stack));
        }
        final IItemHandlerModifiable filters = this.access.appcompat$getFilters();
        for (int slot = 0; slot < filters.getSlots(); slot++) {
            final AEItemKey key = AEItemKey.of(filters.getStackInSlot(slot));
            if (key != null) {
                result.add(key);
            }
        }
        return result;
    }

    void updateRequestedAmount(final AEKey key) {
        if (!(key instanceof AEItemKey itemKey) || this.managedNode == null) {
            return;
        }
        final IGrid grid = this.managedNode.getGrid();
        if (grid == null) {
            return;
        }
        final long requested = grid.getCraftingService().getRequestedAmount(key);
        final int amount = requested > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) requested;
        final IItemHandlerModifiable filters = this.access.appcompat$getFilters();
        int freeSlot = -1;
        for (int slot = 0; slot < filters.getSlots(); slot++) {
            final ItemStack filter = filters.getStackInSlot(slot);
            if (!filter.isEmpty() && itemKey.matches(filter)) {
                filters.setStackInSlot(slot, itemKey.toStack(amount));
                return;
            }
            if (filter.isEmpty() && freeSlot < 0) {
                freeSlot = slot;
            }
        }
        if (freeSlot >= 0 && amount > 0) {
            filters.setStackInSlot(freeSlot, itemKey.toStack(amount));
        }
    }

    void clearCompletedRequests() {
        if (this.managedNode == null) {
            return;
        }
        final IGrid grid = this.managedNode.getGrid();
        if (grid == null) {
            return;
        }
        boolean changed = false;
        final IItemHandlerModifiable filters = this.access.appcompat$getFilters();
        for (int slot = 0; slot < filters.getSlots(); slot++) {
            final ItemStack filter = filters.getStackInSlot(slot);
            final AEItemKey key = AEItemKey.of(filter);
            if (key != null && !grid.getCraftingService().isRequesting(key)) {
                filters.setStackInSlot(slot, ItemStack.EMPTY);
                changed = true;
            }
        }
        if (changed) {
            refresh();
        }
    }

    private static final class PneumaticCraftRequesterWatcher implements ICraftingWatcherNode, IStorageWatcherNode {

        private final PneumaticCraftRequesterNode owner;
        private final boolean crafting;
        private IStackWatcher watcher;

        private PneumaticCraftRequesterWatcher(final PneumaticCraftRequesterNode owner, final boolean crafting) {
            this.owner = owner;
            this.crafting = crafting;
        }

        @Override
        public void updateWatcher(final IStackWatcher newWatcher) {
            this.watcher = newWatcher;
            refresh();
        }

        @Override
        public void onRequestChange(final AEKey what) {
            if (this.crafting) {
                this.owner.updateRequestedAmount(what);
            }
        }

        @Override
        public void onCraftableChange(final AEKey what) {
            if (this.crafting) {
                this.owner.refresh();
            }
        }

        @Override
        public void onStackChange(final AEKey what, final long amount) {
            if (!this.crafting) {
                this.owner.clearCompletedRequests();
            }
        }

        private void refresh() {
            if (this.watcher == null) {
                return;
            }
            this.watcher.reset();
            for (final AEKey key : this.owner.getEmitableItems()) {
                this.watcher.add(key);
            }
        }
    }
}
