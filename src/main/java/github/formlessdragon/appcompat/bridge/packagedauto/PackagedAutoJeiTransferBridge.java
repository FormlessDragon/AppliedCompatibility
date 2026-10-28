package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.integrations.hei.IngredientConverter;
import ae2.api.integrations.hei.IngredientConverters;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import ae2.api.stacks.KeyAmounts;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.api.MiscUtil;

import java.util.List;
import java.util.Map;

public final class PackagedAutoJeiTransferBridge {

    private PackagedAutoJeiTransferBridge() {
    }

    public static ObjectObjectImmutablePair<List<ItemStack>,List<ItemStack>> getProcessingStacks(final IRecipeLayout recipeLayout) {
        final List<ItemStack> input = new ObjectArrayList<>();
        final List<ItemStack> output = new ObjectArrayList<>();
        for (final IngredientConverter<?> converter : IngredientConverters.getConverters()) {
            addProcessingStacks(recipeLayout, converter, input, output);
        }
        return new ObjectObjectImmutablePair<>(input, output);
    }

    public static List<ItemStack> addPositionedTransfer(final IRecipeLayout recipeLayout,
                                                        final Int2ObjectMap<ItemStack> map,
                                                        final int firstIndex) {
        final List<ItemStack> output = new ObjectArrayList<>();
        int index = firstIndex;
        for (final IngredientConverter<?> converter : IngredientConverters.getConverters()) {
            index = addPositionedStacks(recipeLayout, converter, map, index, output);
        }
        return output;
    }

    public static List<ItemStack> condenseStacks(final List<ItemStack> stacks, final boolean ignoreStackSize) {
        final List<ItemStack> itemStacks = new ObjectArrayList<>();
        final Object2LongLinkedOpenHashMap<AEKey> genericStacks = new Object2LongLinkedOpenHashMap<>();
        for (final ItemStack stack : stacks) {
            final GenericStack genericStack = GenericStack.unwrapItemStack(stack);
            if (genericStack == null) {
                itemStacks.add(stack);
                continue;
            }
            final AEKey key = genericStack.what();
            final long amount = genericStack.amount();
            genericStacks.put(key, KeyAmounts.add(key, genericStacks.getLong(key), amount));
        }
        final List<ItemStack> result = MiscUtil.condenseStacks(itemStacks, ignoreStackSize);
        for (final Object2LongMap.Entry<AEKey> entry : genericStacks.object2LongEntrySet()) {
            result.add(GenericStack.wrapInItemStack(new GenericStack(entry.getKey(), entry.getLongValue())));
        }
        return result;
    }

    private static <T> void addProcessingStacks(final IRecipeLayout recipeLayout,
                                                final IngredientConverter<T> converter,
                                                final List<ItemStack> inputStacks,
                                                final List<ItemStack> outputStacks) {
        final IGuiIngredientGroup<T> group = converter.getIngredientGroup(recipeLayout);
        if (group == null) {
            return;
        }
        final Map<Integer, ? extends IGuiIngredient<T>> ingredients = group.getGuiIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }
        if (ingredients instanceof Int2ObjectMap<? extends IGuiIngredient<T>> intMap) {
            for (final Int2ObjectMap.Entry<? extends IGuiIngredient<T>> entry : intMap.int2ObjectEntrySet()) {
                final IGuiIngredient<T> ingredient = entry.getValue();
                if (ingredient == null) {
                    continue;
                }
                final GenericStack stack = firstGenericStack(converter, ingredient);
                if (!isValid(stack)) {
                    continue;
                }
                if (ingredient.isInput()) {
                    inputStacks.add(PackagedPatternStacks.toItemStack(stack));
                } else {
                    outputStacks.add(PackagedPatternStacks.toItemStack(stack));
                }
            }
            return;
        }
        for (final Map.Entry<Integer, ? extends IGuiIngredient<T>> entry : ingredients.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("PackagedAuto JEI transfer ingredient map contains a null slot index");
            }
            final IGuiIngredient<T> ingredient = entry.getValue();
            if (ingredient == null) {
                continue;
            }
            final GenericStack stack = firstGenericStack(converter, ingredient);
            if (!isValid(stack)) {
                continue;
            }
            if (ingredient.isInput()) {
                inputStacks.add(PackagedPatternStacks.toItemStack(stack));
            } else {
                outputStacks.add(PackagedPatternStacks.toItemStack(stack));
            }
        }
    }

    private static <T> int addPositionedStacks(final IRecipeLayout recipeLayout,
                                               final IngredientConverter<T> converter,
                                               final Int2ObjectMap<ItemStack> map,
                                               final int index,
                                               final List<ItemStack> output) {
        final IGuiIngredientGroup<T> group = converter.getIngredientGroup(recipeLayout);
        if (group == null) {
            return index;
        }
        final Map<Integer, ? extends IGuiIngredient<T>> ingredients = group.getGuiIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            return index;
        }
        int nextIndex = index;
        if (ingredients instanceof Int2ObjectMap<? extends IGuiIngredient<T>> intMap) {
            for (final Int2ObjectMap.Entry<? extends IGuiIngredient<T>> entry : intMap.int2ObjectEntrySet()) {
                final IGuiIngredient<T> ingredient = entry.getValue();
                if (ingredient == null) {
                    continue;
                }
                final GenericStack stack = firstGenericStack(converter, ingredient);
                if (ingredient.isInput()) {
                    if (nextIndex < 81 && isValid(stack)) {
                        map.put(nextIndex, PackagedPatternStacks.toItemStack(stack));
                    }
                    nextIndex++;
                } else if (isValid(stack)) {
                    output.add(PackagedPatternStacks.toItemStack(stack));
                }
            }
            return nextIndex;
        }
        for (final Map.Entry<Integer, ? extends IGuiIngredient<T>> entry : ingredients.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("PackagedAuto JEI transfer ingredient map contains a null slot index");
            }
            final IGuiIngredient<T> ingredient = entry.getValue();
            if (ingredient == null) {
                continue;
            }
            final GenericStack stack = firstGenericStack(converter, ingredient);
            if (ingredient.isInput()) {
                if (nextIndex < 81 && isValid(stack)) {
                    map.put(nextIndex, PackagedPatternStacks.toItemStack(stack));
                }
                nextIndex++;
            } else if (isValid(stack)) {
                output.add(PackagedPatternStacks.toItemStack(stack));
            }
        }
        return nextIndex;
    }

    private static <T> GenericStack firstGenericStack(final IngredientConverter<T> converter,
                                                      final IGuiIngredient<T> ingredient) {
        final T displayedIngredient = ingredient.getDisplayedIngredient();
        final GenericStack displayed = displayedIngredient == null ? null : converter.getStackFromIngredient(displayedIngredient);
        if (isValid(displayed)) {
            return displayed;
        }
        final List<T> allIngredients = ingredient.getAllIngredients();
        if (allIngredients == null) {
            return null;
        }
        for (final T candidate : allIngredients) {
            final GenericStack stack = candidate == null ? null : converter.getStackFromIngredient(candidate);
            if (isValid(stack)) {
                return stack;
            }
        }
        return null;
    }

    private static boolean isValid(final GenericStack stack) {
        return stack != null && stack.what() != null && stack.amount() > 0;
    }

}
