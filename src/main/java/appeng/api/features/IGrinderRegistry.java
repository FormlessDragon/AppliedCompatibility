package appeng.api.features;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IGrinderRegistry {

    @Nonnull
    IGrinderRecipeBuilder builder();

    @Nonnull
    Collection<IGrinderRecipe> getRecipes();

    boolean addRecipe(IGrinderRecipe recipe);

    boolean removeRecipe(@Nonnull IGrinderRecipe recipe);

    @Nullable
    IGrinderRecipe getRecipeForInput(@Nonnull ItemStack input);

    void addDustRatio(@Nonnull String oredictName, int ratio);

    boolean removeDustRatio(@Nonnull String oredictName);
}
