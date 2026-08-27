package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.features.ChargeableItems;
import ae2.api.movable.BlockEntityMoveStrategies;

public final class AppCompatAEHooks {

    private static boolean initialized;

    private AppCompatAEHooks() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        BlockEntityMoveStrategies.add(new LegacyMovableTileMoveStrategy());
        ChargeableItems.register(LegacyAeItemPowerStorageBridge.INSTANCE);
    }
}
