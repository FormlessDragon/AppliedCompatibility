package appeng.api.config;

@SuppressWarnings("unused")
public enum SortDir {
    ASCENDING,
    DESCENDING;

    public ae2.api.config.SortDir toNewDirection() {
        return switch (this) {
            case ASCENDING -> ae2.api.config.SortDir.ASCENDING;
            case DESCENDING -> ae2.api.config.SortDir.DESCENDING;
        };
    }

    public static SortDir fromNewDirection(final ae2.api.config.SortDir direction) {
        return switch (direction) {
            case ASCENDING -> ASCENDING;
            case DESCENDING -> DESCENDING;
        };
    }
}
