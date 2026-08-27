package github.formlessdragon.appcompat.bridge.mmceaddons;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.security.IActionHost;
import ae2.api.networking.security.IActionSource;
import ae2.api.networking.storage.IStorageService;
import ae2.api.stacks.AEKey;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.me.helpers.ActionHostEnergySource;
import ae2.me.helpers.IGridConnectedTile;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.RequirementEssentia;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.me.key.AEEssentiaKey;

import java.util.Map;

public final class AppCompatEssentiaBridge {

    private AppCompatEssentiaBridge() {
    }

    public static MEStorage getStorage(final Object owner) {
        final IGridConnectedTile host = (IGridConnectedTile) owner;
        final IGridNode node = host.getGridNode(EnumFacing.UP);
        if (node == null) {
            return null;
        }
        final IGrid grid = node.grid();
        if (grid == null) {
            return null;
        }
        final IStorageService service = grid.getStorageService();
        return service == null ? null : service.getInventory();
    }

    public static AEEssentiaKey key(final RequirementEssentia requirement) {
        final Aspect aspect = requirement.getEssentiaStack().getAspect();
        return AEEssentiaKey.of(aspect);
    }

    public static void updateSnapshot(final Map<String, Long> target, final MEStorage storage) {
        target.clear();
        if (storage == null) {
            return;
        }
        for (final Object2LongMap.Entry<AEKey> entry : storage.getAvailableStacks()) {
            if (entry.getKey() instanceof AEEssentiaKey essentiaKey) {
                final long amount = entry.getLongValue();
                if (amount > 0) {
                    target.put(essentiaKey.getAspectTag(), amount);
                }
            }
        }
    }

    public static long extract(final Object owner, final RequirementEssentia requirement) {
        final MEStorage storage = getStorage(owner);
        final AEEssentiaKey key = key(requirement);
        if (storage == null || key == null) {
            return 0;
        }
        final IActionSource source = IActionSource.ofMachine((IActionHost) owner);
        final ActionHostEnergySource energy = new ActionHostEnergySource((IActionHost) owner);
        return StorageHelper.poweredExtraction(energy, storage, key,
            requirement.getEssentiaStack().getAmount(), source);
    }

    public static long insert(final Object owner, final RequirementEssentia requirement, final long amount) {
        final MEStorage storage = getStorage(owner);
        final AEEssentiaKey key = key(requirement);
        if (storage == null || key == null || amount <= 0) {
            return 0;
        }
        final IActionSource source = IActionSource.ofMachine((IActionHost) owner);
        final ActionHostEnergySource energy = new ActionHostEnergySource((IActionHost) owner);
        return StorageHelper.poweredInsert(energy, storage, key, amount, source);
    }

    public static boolean insertPending(final Object owner, final Map<String, Long> amounts) {
        final MEStorage storage = getStorage(owner);
        if (storage == null) {
            return false;
        }
        final IActionSource source = IActionSource.ofMachine((IActionHost) owner);
        final ActionHostEnergySource energy = new ActionHostEnergySource((IActionHost) owner);
        boolean changed = false;
        final var iterator = amounts.entrySet().iterator();
        while (iterator.hasNext()) {
            final var entry = iterator.next();
            final Aspect aspect = Aspect.getAspect(entry.getKey());
            if (aspect == null) {
                iterator.remove();
                continue;
            }
            final long pending = entry.getValue();
            if (pending <= 0) {
                iterator.remove();
                continue;
            }
            final AEEssentiaKey key = AEEssentiaKey.of(aspect);
            final long inserted = StorageHelper.poweredInsert(energy, storage, key, pending, source);
            if (inserted > 0) {
                changed = true;
                final long remaining = pending - inserted;
                if (remaining <= 0) {
                    iterator.remove();
                } else {
                    entry.setValue(remaining);
                }
            }
        }
        return changed;
    }
}
