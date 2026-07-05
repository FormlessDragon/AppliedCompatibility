package appeng.api.config;

public enum AccessRestriction {
    NO_ACCESS(0),
    READ(1),
    WRITE(2),
    READ_WRITE(3);

    private final int permissionBit;

    AccessRestriction(final int permissionBit) {
        this.permissionBit = permissionBit;
    }

    public boolean hasPermission(final AccessRestriction restriction) {
        return (this.permissionBit & restriction.permissionBit) == restriction.permissionBit;
    }

    public AccessRestriction restrictPermissions(final AccessRestriction restriction) {
        return getPermByBit(this.permissionBit & restriction.permissionBit);
    }

    public AccessRestriction addPermissions(final AccessRestriction restriction) {
        return getPermByBit(this.permissionBit | restriction.permissionBit);
    }

    public AccessRestriction removePermissions(final AccessRestriction restriction) {
        return getPermByBit(this.permissionBit & ~restriction.permissionBit);
    }

    private static AccessRestriction getPermByBit(final int bit) {
        return switch (bit) {
            case 1 -> READ;
            case 2 -> WRITE;
            case 3 -> READ_WRITE;
            default -> NO_ACCESS;
        };
    }
}
