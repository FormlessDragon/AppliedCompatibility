package appeng.api.features;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Old AE builder API for integrations that construct grinder recipes fluently.
 */
public interface IGrinderRecipeBuilder {

    /**
     * Sets the grinder input stack.
     */
    @Nonnull
    IGrinderRecipeBuilder withInput(@Nonnull ItemStack input);

    /**
     * Sets the guaranteed grinder output stack.
     */
    @Nonnull
    IGrinderRecipeBuilder withOutput(@Nonnull ItemStack output);

    /**
     * Sets the first optional output stack and chance.
     */
    @Nonnull
    IGrinderRecipeBuilder withFirstOptional(@Nonnull ItemStack optional, float chance);

    /**
     * Sets the second optional output stack and chance.
     */
    @Nonnull
    IGrinderRecipeBuilder withSecondOptional(@Nonnull ItemStack optional, float chance);

    /**
     * Sets the grinder turn cost.
     */
    @Nonnull
    IGrinderRecipeBuilder withTurns(@Nonnegative int turns);

    /**
     * Builds an immutable grinder recipe.
     */
    @Nonnull
    IGrinderRecipe build();
}
