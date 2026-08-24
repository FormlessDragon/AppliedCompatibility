package github.formlessdragon.appcompat.bridge.enderioae;

import com.enderio.core.common.util.stackable.Things;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Queues Ender IO thing specifications until AE2 item definitions are initialized.
 */
public final class EnderIOThingQueue {

    private static final List<PendingThing> PENDING = new ArrayList<>();

    private EnderIOThingQueue() {
    }

    public static synchronized void enqueue(final Things things, final String specification) {
        if (things == null || specification == null || specification.isBlank()) {
            throw new IllegalArgumentException("Thing queue entries require a target and specification");
        }
        PENDING.add(new PendingThing(things, specification));
    }

    public static synchronized void flush() {
        if (LegacyAeItemMappings.isInitialized()) {
            for (final PendingThing pending : PENDING) {
                replay(pending.things(), pending.specification());
            }
            PENDING.clear();
        }
    }

    private static void replay(final Things things, final String specification) {
        final String[] parts = specification.split(",", -1);
        for (final String rawPart : parts) {
            final String part = rawPart.trim();
            if (part.isEmpty()) {
                continue;
            }
            final ItemStack mapped = LegacyAeItemMappings.mappedSpecStackOrNull(part, 1);
            if (mapped != null) {
                things.add(mapped);
            } else {
                things.add(part);
            }
        }
    }

    private record PendingThing(Things things, String specification) {
    }
}
