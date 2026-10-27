package appeng.api.config;

@SuppressWarnings("unused")
public enum ViewItems {
    ALL,
    STORED,
    CRAFTABLE,
    FLOWING;

    public ae2.api.config.ViewItems toNewView() {
        return switch (this) {
            case ALL -> ae2.api.config.ViewItems.ALL;
            case STORED -> ae2.api.config.ViewItems.STORED;
            case CRAFTABLE -> ae2.api.config.ViewItems.CRAFTABLE;
            case FLOWING -> ae2.api.config.ViewItems.FLOWING;
        };
    }

    public static ViewItems fromNewView(final ae2.api.config.ViewItems view) {
        return switch (view) {
            case ALL -> ViewItems.ALL;
            case STORED -> ViewItems.STORED;
            case CRAFTABLE -> ViewItems.CRAFTABLE;
            case FLOWING -> ViewItems.FLOWING;
        };
    }
}
