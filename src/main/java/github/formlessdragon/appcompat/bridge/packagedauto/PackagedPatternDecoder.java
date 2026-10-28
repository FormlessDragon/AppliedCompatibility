package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.crafting.IPatternDetailsDecoder;
import ae2.api.stacks.AEItemKey;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thelm.packagedauto.api.IPackageItem;

/**
 * Decodes the virtual package items sent to AE's pattern terminals.
 */
public final class PackagedPatternDecoder implements IPatternDetailsDecoder {

    public static final PackagedPatternDecoder INSTANCE = new PackagedPatternDecoder();

    private PackagedPatternDecoder() {
    }

    @Override
    public boolean isEncodedPattern(final ItemStack stack) {
        return stack.getItem() instanceof IPackageItem && PackagedPatternKind.fromStack(stack) != null;
    }

    @Override
    public @Nullable IPatternDetails decodePattern(final AEItemKey what, final World level) {
        if (what == null) {
            return null;
        }
        final ItemStack stack = what.toStack();
        if (!(stack.getItem() instanceof IPackageItem packageItem)) {
            return null;
        }
        final PackagedPatternKind kind = PackagedPatternKind.fromStack(stack);
        if (kind == null) {
            return null;
        }
        final var recipe = packageItem.getRecipeInfo(stack);
        if (recipe == null || recipe.getPatterns().isEmpty()) {
            return null;
        }
        return switch (kind) {
            case PACKAGE -> recipe.isPackageable() && recipe.validPatternIndex(packageItem.getIndex(stack))
                ? PackagedPatternDetails.packagePattern(recipe.getPatterns().get(packageItem.getIndex(stack))) : null;
            case RECIPE -> recipe.isCraftable() ? PackagedPatternDetails.recipePattern(recipe) : null;
            case DIRECT -> recipe.isCraftable() ? PackagedPatternDetails.directPattern(recipe) : null;
        };
    }
}
