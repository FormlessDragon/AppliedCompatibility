package appeng.api.config;

public enum SortDir {
    ASCENDING(1),
    DESCENDING(-1);

    public final int sortHint;

    SortDir(final int sortHint) {
        this.sortHint = sortHint;
    }
}
