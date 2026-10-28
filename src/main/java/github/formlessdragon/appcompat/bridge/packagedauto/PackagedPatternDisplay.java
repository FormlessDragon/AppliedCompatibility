package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.GenericStack;
import ae2.api.stacks.KeyAmounts;
import ae2.core.definitions.AEItems;
import ae2.crafting.pattern.AEProcessingPattern;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import thelm.packagedauto.api.IRecipeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Processing-pattern previews are independent of the patterns dispatched by crafting CPUs.
 */
public final class PackagedPatternDisplay {

    private PackagedPatternDisplay() {
    }

    public static List<AEItemKey> encode(final List<IPatternDetails> patterns) {
        final List<DisplayPattern> displays = build(patterns);
        final var definitions = new ObjectArrayList<AEItemKey>(displays.size());
        for (final DisplayPattern display : displays) {
            definitions.add(display.encode());
        }
        return ObjectLists.unmodifiable(definitions);
    }

    public static List<DisplayPattern> build(final List<? extends IPatternDetails> patterns) {
        final List<Builder> displays = new ObjectArrayList<>(patterns.size());
        final Map<IRecipeInfo, Builder> packages = new Object2ObjectOpenHashMap<>();
        final Set<AEItemKey> seen = new ObjectOpenHashSet<>(patterns.size());
        for (final IPatternDetails pattern : patterns) {
            if (!seen.add(pattern.getDefinition())) {
                continue;
            }
            final Builder display;
            if (pattern instanceof PackagedPatternDetails packaged && packaged.kind() == PackagedPatternKind.PACKAGE) {
                display = packages.computeIfAbsent(packaged.recipe(), recipe -> {
                    final Builder builder = new Builder();
                    displays.add(builder);
                    return builder;
                });
            } else {
                display = new Builder();
                displays.add(display);
            }
            for (final IPatternDetails.IInput input : pattern.getInputs()) {
                final GenericStack stack = input.possibleInputs()[0];
                display.inputs.add(new GenericStack(stack.what(),
                    KeyAmounts.multiply(stack.what(), stack.amount(), input.getMultiplier())));
            }
            display.outputs.addAll(pattern.getOutputs());
        }
        final var result = new ObjectArrayList<DisplayPattern>(displays.size());
        for (final Builder builder : displays) {
            result.add(new DisplayPattern(builder.inputs, builder.outputs));
        }
        return ObjectLists.unmodifiable(result);
    }

    public record DisplayPattern(List<GenericStack> inputs, List<GenericStack> outputs) {
        public DisplayPattern {
            inputs = ObjectLists.unmodifiable(new ObjectArrayList<>(inputs));
            outputs = ObjectLists.unmodifiable(new ObjectArrayList<>(outputs));
        }

        public AEItemKey encode() {
            final ItemStack encoded = AEItems.PROCESSING_PATTERN.stack();
            AEProcessingPattern.encode(encoded, this.inputs, this.outputs);
            return AEItemKey.of(encoded);
        }
    }

    private static final class Builder {
        private final List<GenericStack> inputs = new ObjectArrayList<>();
        private final List<GenericStack> outputs = new ObjectArrayList<>();
    }
}
