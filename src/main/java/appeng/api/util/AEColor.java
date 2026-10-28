package appeng.api.util;

import net.minecraft.item.EnumDyeColor;

public enum AEColor {
    WHITE("White", "gui.appliedenergistics2.White", "white", EnumDyeColor.WHITE, 0xb4b4b4, 0xe0e0e0, 0xf9f9f9, 0x000000),
    ORANGE("Orange", "gui.appliedenergistics2.Orange", "orange", EnumDyeColor.ORANGE, 0xd9782f, 0xeca23c, 0xf2ba49, 0x000000),
    MAGENTA("Magenta", "gui.appliedenergistics2.Magenta", "magenta", EnumDyeColor.MAGENTA, 0xc15189, 0xd5719c, 0xe69ebf, 0x000000),
    LIGHT_BLUE("Light Blue", "gui.appliedenergistics2.LightBlue", "light_blue", EnumDyeColor.LIGHT_BLUE, 0x69b9ff, 0x70d2ff, 0x80f7ff, 0x000000),
    YELLOW("Yellow", "gui.appliedenergistics2.Yellow", "yellow", EnumDyeColor.YELLOW, 0xffcf40, 0xffe359, 0xf4ff80, 0x000000),
    LIME("Lime", "gui.appliedenergistics2.Lime", "lime", EnumDyeColor.LIME, 0x4ec04e, 0x70e259, 0xb3f86d, 0x000000),
    PINK("Pink", "gui.appliedenergistics2.Pink", "pink", EnumDyeColor.PINK, 0xd86eaa, 0xff99bb, 0xfbcad5, 0x000000),
    GRAY("Gray", "gui.appliedenergistics2.Gray", "gray", EnumDyeColor.GRAY, 0x4f4f4f, 0x6c6b6c, 0x949294, 0x000000),
    LIGHT_GRAY("Light Gray", "gui.appliedenergistics2.LightGray", "light_gray", EnumDyeColor.SILVER, 0x7e7e7e, 0xa09fa0, 0xc4c4c4, 0x000000),
    CYAN("Cyan", "gui.appliedenergistics2.Cyan", "cyan", EnumDyeColor.CYAN, 0x22b0ae, 0x2fccb7, 0x65e8c9, 0x000000),
    PURPLE("Purple", "gui.appliedenergistics2.Purple", "purple", EnumDyeColor.PURPLE, 0x6e5cb8, 0x915dcd, 0xb06fdd, 0x000000),
    BLUE("Blue", "gui.appliedenergistics2.Blue", "blue", EnumDyeColor.BLUE, 0x337ff0, 0x3894ff, 0x40c1ff, 0x000000),
    BROWN("Brown", "gui.appliedenergistics2.Brown", "brown", EnumDyeColor.BROWN, 0x6e4a12, 0x7e5c16, 0x8e6e1a, 0x000000),
    GREEN("Green", "gui.appliedenergistics2.Green", "green", EnumDyeColor.GREEN, 0x079b6b, 0x17b86d, 0x32d850, 0x000000),
    RED("Red", "gui.appliedenergistics2.Red", "red", EnumDyeColor.RED, 0xaa212b, 0xd73e42, 0xf07665, 0x000000),
    BLACK("Black", "gui.appliedenergistics2.Black", "black", EnumDyeColor.BLACK, 0x131313, 0x272727, 0x3b3b3b, 0xffffff),
    TRANSPARENT("Fluix", "gui.appliedenergistics2.Fluix", "fluix", null, 0x5a479e, 0x915dcd, 0xe2a3e3, 0x000000);

    public static final AEColor[] VALUES = values();

    public static final AEColor[] VALID_COLORS = new AEColor[] {
        WHITE,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE,
        YELLOW,
        LIME,
        PINK,
        GRAY,
        LIGHT_GRAY,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK
    };
    public static final int TINTINDEX_DARK = 1;
    public static final int TINTINDEX_MEDIUM = 2;
    public static final int TINTINDEX_BRIGHT = 3;
    public static final int TINTINDEX_MEDIUM_BRIGHT = 4;

    public final String englishName;
    public final String translationKey;
    public final String registryPrefix;
    public final EnumDyeColor dye;
    public final int blackVariant;
    public final int mediumVariant;
    public final int whiteVariant;
    public final int contrastTextColor;

    AEColor(final String englishName, final String translationKey, final String registryPrefix, final EnumDyeColor dye,
            final int blackVariant, final int mediumVariant, final int whiteVariant, final int contrastTextColor) {
        this.englishName = englishName;
        this.translationKey = translationKey;
        this.registryPrefix = registryPrefix;
        this.dye = dye;
        this.blackVariant = blackVariant;
        this.mediumVariant = mediumVariant;
        this.whiteVariant = whiteVariant;
        this.contrastTextColor = contrastTextColor;
    }

    public static AEColor fromDye(final EnumDyeColor vanillaDye) {
        for (final AEColor value : VALUES) {
            if (value.dye == vanillaDye) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown Vanilla dye: " + vanillaDye);
    }

    public int getVariantByTintIndex(final int tintIndex) {
        return switch (tintIndex) {
            case TINTINDEX_DARK -> this.blackVariant;
            case TINTINDEX_MEDIUM -> this.mediumVariant;
            case TINTINDEX_BRIGHT -> this.whiteVariant;
            case TINTINDEX_MEDIUM_BRIGHT -> {
                final int light = this.whiteVariant;
                final int dark = this.mediumVariant;
                yield ((light >> 16 & 0xff) + (dark >> 16 & 0xff)) / 2 << 16
                    | ((light >> 8 & 0xff) + (dark >> 8 & 0xff)) / 2 << 8
                    | ((light & 0xff) + (dark & 0xff)) / 2;
            }
            default -> -1;
        };
    }

    public String getEnglishName() {
        return this.englishName;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public String getSerializedName() {
        return this.registryPrefix;
    }

    @Override
    public String toString() {
        return this.translationKey;
    }
}
