package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.GenericStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thelm.packagedauto.api.IPackageItem;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeInfo;

import java.util.Collections;
import java.util.List;

public final class PackagedPatternDetails implements IPatternDetails {

    private final PackagedPatternKind kind;
    private final IPackagePattern packagePattern;
    private final IRecipeInfo recipe;
    private final AEItemKey definition;
    private final List<GenericStack> outputs;
    private final IInput[] inputSlots;

    private PackagedPatternDetails(final PackagedPatternKind kind,
                                   final IPackagePattern packagePattern,
                                   final IRecipeInfo recipe,
                                   final ItemStack definition,
                                   final List<GenericStack> inputs,
                                   final List<GenericStack> outputs) {
        this.kind = kind;
        this.packagePattern = packagePattern;
        this.recipe = recipe;
        this.definition = AEItemKey.of(kind.encodeDefinition(definition));
        // Every factory transfers ownership of its freshly built output list.
        this.outputs = Collections.unmodifiableList(outputs);
        this.inputSlots = new IInput[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            this.inputSlots[i] = new Input(inputs.get(i));
        }
        if (this.definition == null) {
            throw new IllegalArgumentException("PackagedAuto pattern definition is not an item");
        }
        if (this.outputs.isEmpty()) {
            throw new IllegalArgumentException("PackagedAuto pattern has no outputs");
        }
    }

    public static PackagedPatternDetails packagePattern(final IPackagePattern pattern) {
        final List<GenericStack> outputs = new ObjectArrayList<>();
        final GenericStack output = PackagedPatternStacks.fromItemStack(pattern.getOutput());
        if (output != null) {
            outputs.add(output);
        }
        return new PackagedPatternDetails(PackagedPatternKind.PACKAGE, pattern, pattern.getRecipeInfo(), pattern.getOutput(),
            PackagedPatternStacks.fromItemStacks(pattern.getInputs()),
            outputs);
    }

    public static PackagedPatternDetails recipePattern(final IRecipeInfo recipe) {
        final ItemStack definition = recipe.getPatterns().getFirst().getOutput();
        final List<GenericStack> inputs = new ObjectArrayList<>();
        for (final IPackagePattern pattern : recipe.getPatterns()) {
            final GenericStack stack = PackagedPatternStacks.fromItemStack(pattern.getOutput());
            if (stack != null) {
                inputs.add(stack);
            }
        }
        return new PackagedPatternDetails(PackagedPatternKind.RECIPE, null, recipe, definition, inputs,
            PackagedPatternStacks.fromItemStacks(recipe.getOutputs()));
    }

    public static PackagedPatternDetails directPattern(final IRecipeInfo recipe) {
        final ItemStack definition = recipe.getPatterns().getFirst().getOutput();
        final List<GenericStack> inputs = new ObjectArrayList<>();
        for (final ItemStack stack : recipe.getInputs()) {
            if (stack.getItem() instanceof IPackageItem packageItem) {
                final IRecipeInfo subRecipe = packageItem.getRecipeInfo(stack);
                if (subRecipe != null && !subRecipe.getRecipeType().hasMachine() && subRecipe.getPatterns().size() == 1) {
                    for (final ItemStack subInput : subRecipe.getInputs()) {
                        final GenericStack genericStack = PackagedPatternStacks.fromItemStack(subInput);
                        if (genericStack != null) {
                            inputs.add(genericStack);
                        }
                    }
                    continue;
                }
            }
            final GenericStack genericStack = PackagedPatternStacks.fromItemStack(stack);
            if (genericStack != null) {
                inputs.add(genericStack);
            }
        }
        return new PackagedPatternDetails(PackagedPatternKind.DIRECT, null, recipe, definition, inputs,
            PackagedPatternStacks.fromItemStacks(recipe.getOutputs()));
    }

    public PackagedPatternKind kind() {
        return this.kind;
    }

    public IPackagePattern packagePattern() {
        return this.packagePattern;
    }

    public IRecipeInfo recipe() {
        return this.recipe;
    }

    @Override
    public AEItemKey getDefinition() {
        return this.definition;
    }

    @Override
    public IInput[] getInputs() {
        return this.inputSlots;
    }

    @Override
    public List<GenericStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof PackagedPatternDetails pattern && this.definition.equals(pattern.definition);
    }

    @Override
    public int hashCode() {
        return this.definition.hashCode();
    }

    private record Input(GenericStack stack, GenericStack[] possibleInputs) implements IInput {

        private Input(final GenericStack stack) {
            this(stack, new GenericStack[]{stack});
        }

        @Override
        public GenericStack[] possibleInputs() {
            return this.possibleInputs;
        }

        @Override
        public long getMultiplier() {
            return 1;
        }

        @Override
        public boolean isValid(final AEKey input, final World level) {
            return this.stack.what().equals(input);
        }

        @Override
        public @Nullable AEKey getRemainingKey(final AEKey template) {
            return null;
        }
    }
}
