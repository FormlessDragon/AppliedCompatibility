package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.inventories.VersionedInternalInventory;
import ae2.api.stacks.AEItemKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A stable, read-only inventory of virtual patterns, refreshed when the provider's recipes change.
 */
public final class PackagedPatternInventory implements VersionedInternalInventory {

    private final Supplier<? extends List<? extends IPatternDetails>> patterns;
    private final Function<List<IPatternDetails>, List<AEItemKey>> displayEncoder;
    private Snapshot snapshot;
    private List<AEItemKey> displayDefinitions;
    private Set<AEItemKey> displayKeys;
    private long contentsVersion;

    public PackagedPatternInventory(final Supplier<? extends List<? extends IPatternDetails>> patterns) {
        this(patterns, details -> {
            final var definitions = new ObjectArrayList<AEItemKey>(details.size());
            for (final IPatternDetails detail : details) {
                definitions.add(detail.getDefinition());
            }
            return definitions;
        });
    }

    public PackagedPatternInventory(final Supplier<? extends List<? extends IPatternDetails>> patterns,
                                    final Function<List<IPatternDetails>, List<AEItemKey>> displayEncoder) {
        this.patterns = patterns;
        this.displayEncoder = displayEncoder;
    }

    public void invalidate() {
        this.snapshot = null;
        this.displayDefinitions = null;
        this.displayKeys = null;
        this.contentsVersion++;
    }

    @Override
    public long getContentsVersion() {
        return this.contentsVersion;
    }

    private Snapshot snapshot() {
        if (this.snapshot == null) {
            // The supplier may retain its list; take one owned snapshot before wrapping it.
            final var patterns = new ObjectArrayList<IPatternDetails>(this.patterns.get());
            final var keys = new ObjectOpenHashSet<AEItemKey>(patterns.size());
            for (final IPatternDetails pattern : patterns) {
                keys.add(pattern.getDefinition());
            }
            this.snapshot = new Snapshot(ObjectLists.unmodifiable(patterns), ObjectSets.unmodifiable(keys));
        }
        return this.snapshot;
    }

    public List<IPatternDetails> getPatterns() {
        return snapshot().patterns();
    }

    public boolean contains(final AEItemKey pattern) {
        return containsCraftingPattern(pattern) || pattern != null && displayKeys().contains(pattern);
    }

    public boolean containsCraftingPattern(final AEItemKey pattern) {
        return pattern != null && snapshot().keys().contains(pattern);
    }

    private List<AEItemKey> displayDefinitions() {
        if (this.displayDefinitions == null) {
            final var definitions = new ObjectArrayList<>(this.displayEncoder.apply(getPatterns()));
            this.displayDefinitions = ObjectLists.unmodifiable(definitions);
            this.displayKeys = ObjectSets.unmodifiable(new ObjectOpenHashSet<>(definitions));
        }
        return this.displayDefinitions;
    }

    private Set<AEItemKey> displayKeys() {
        displayDefinitions();
        return this.displayKeys;
    }

    @Override
    public int size() {
        return displayDefinitions().size();
    }

    @Override
    public int getSlotLimit(final int slot) {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(final int slotIndex) {
        return displayDefinitions().get(slotIndex).toStack();
    }

    @Override
    public void setItemDirect(final int slotIndex, final ItemStack stack) {
        // These slots describe recipes; there are no stored items to replace.
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        return stack;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        return ItemStack.EMPTY;
    }

    private record Snapshot(List<IPatternDetails> patterns, Set<AEItemKey> keys) {
    }
}
