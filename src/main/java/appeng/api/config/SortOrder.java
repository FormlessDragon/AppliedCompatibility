package appeng.api.config;

@SuppressWarnings("unused")
public enum SortOrder {
    NAME,
    AMOUNT,
    MOD,
    INVTWEAKS,
    HEI;

    public ae2.api.config.SortOrder toNewOrder() {
        return switch (this) {
            case NAME -> ae2.api.config.SortOrder.NAME;
            case AMOUNT -> ae2.api.config.SortOrder.AMOUNT;
            case MOD -> ae2.api.config.SortOrder.MOD;
            case INVTWEAKS -> ae2.api.config.SortOrder.INVTWEAKS;
            case HEI -> ae2.api.config.SortOrder.HEI;
        };
    }

    public static SortOrder fromNewOrder(final ae2.api.config.SortOrder order) {
        return switch (order) {
            case NAME -> NAME;
            case AMOUNT -> AMOUNT;
            case MOD -> MOD;
            case INVTWEAKS -> INVTWEAKS;
            case HEI -> HEI;
        };
    }
}
