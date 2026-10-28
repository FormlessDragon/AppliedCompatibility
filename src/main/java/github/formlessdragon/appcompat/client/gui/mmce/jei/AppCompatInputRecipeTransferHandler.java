package github.formlessdragon.appcompat.client.gui.mmce.jei;

import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.transfer.RecipeTransferErrorInternal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class AppCompatInputRecipeTransferHandler implements IRecipeTransferHandler<ContainerMEItemInputBus> {

    @Override
    public Class<ContainerMEItemInputBus> getContainerClass() {
        return ContainerMEItemInputBus.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(final @NonNull ContainerMEItemInputBus container,
                                               final IRecipeLayout recipeLayout,
                                               final @NonNull EntityPlayer player,
                                               final boolean maxTransfer,
                                               final boolean doTransfer) {
        if (!recipeLayout.getRecipeCategory().getUid().contains("modularmachinery.recipe")) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        if (!doTransfer) {
            return null;
        }

        final List<ItemStack> inputs = new ObjectArrayList<>();
        final Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredients = recipeLayout.getItemStacks().getGuiIngredients();
        for (final IGuiIngredient<ItemStack> ingredient : ingredients.values()) {
            if (ingredient.isInput()) {
                final ItemStack displayed = ingredient.getDisplayedIngredient();
                if (displayed != null && !displayed.isEmpty()) {
                    inputs.add(displayed);
                }
            }
        }
        if (inputs.isEmpty()) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        container.fillRecipe(inputs);
        return null;
    }
}
