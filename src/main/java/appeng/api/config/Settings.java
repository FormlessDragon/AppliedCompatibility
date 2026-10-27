package appeng.api.config;

import java.util.EnumSet;

public enum Settings {
    SORT_DIRECTION(EnumSet.allOf(SortDir.class)),
    SORT_BY(EnumSet.allOf(SortOrder.class)),
    VIEW_MODE(EnumSet.allOf(ViewItems.class)),
    ACTIONS(EnumSet.allOf(ActionItems.class));

    private final EnumSet<? extends Enum<?>> values;

    Settings(final EnumSet<? extends Enum<?>> values) {
        this.values = values;
    }

    public EnumSet<? extends Enum<?>> getPossibleValues() {
        return this.values;
    }
}
