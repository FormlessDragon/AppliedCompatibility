package appeng.api.features;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Old AE grinder recipe record used by legacy integrations registering grindable inputs.
 */
public interface IGrinderRecipe {

    /**
     * Returns the accepted grinder input.
     */
    @Nonnull
    ItemStack getInput();

    /**
     * Returns the guaranteed grinder output.
     */
    @Nonnull
    ItemStack getOutput();

    /**
     * Returns the first optional grinder output.
     */
    @Nonnull
    Optional<ItemStack> getOptionalOutput();

    /**
     * Returns the second optional grinder output.
     */
    @Nonnull
    Optional<ItemStack> getSecondOptionalOutput();

    /**
     * Returns the chance for the first optional output.
     */
    float getOptionalChance();

    /**
     * Returns the chance for the second optional output.
     */
    float getSecondOptionalChance();

    /**
     * Returns the grinder turn cost.
     */
    int getRequiredTurns();
}
