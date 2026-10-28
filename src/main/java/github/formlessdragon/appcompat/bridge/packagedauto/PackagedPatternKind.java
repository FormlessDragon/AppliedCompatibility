package github.formlessdragon.appcompat.bridge.packagedauto;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum PackagedPatternKind {
    PACKAGE("package"),
    RECIPE("recipe"),
    DIRECT("direct");

    private final String name;

    PackagedPatternKind(final String name) {
        this.name = name;
    }

    public static @Nullable PackagedPatternKind fromStack(final ItemStack stack) {
        final var tag = stack.getSubCompound("appcompat");
        if (tag == null) {
            return null;
        }
        return switch (tag.getString("PatternKind")) {
            case "package" -> PACKAGE;
            case "recipe" -> RECIPE;
            case "direct" -> DIRECT;
            default -> null;
        };
    }

    public ItemStack encodeDefinition(final ItemStack stack) {
        final ItemStack definition = stack.copy();
        definition.setCount(1);
        definition.getOrCreateSubCompound("appcompat").setString("PatternKind", this.name);
        return definition;
    }
}
