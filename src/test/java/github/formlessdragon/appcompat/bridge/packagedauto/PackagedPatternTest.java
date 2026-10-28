package github.formlessdragon.appcompat.bridge.packagedauto;

import ae2.api.crafting.IPatternDetails;
import ae2.api.stacks.AEFluidKey;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.GenericStack;
import ae2.core.AEConfig;
import ae2.core.definitions.AEItems;
import ae2.crafting.pattern.AEProcessingPattern;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thelm.packagedauto.api.IPackageItem;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeInfo;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.api.PatternType;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackagedPatternTest {

    @BeforeAll
    static void initializeMinecraft() throws ReflectiveOperationException {
        // Plain JUnit has no Forge launcher to install the physical side delegate.
        final FMLCommonHandler handler = FMLCommonHandler.instance();
        if (handler.getSidedDelegate() == null) {
            final var delegate = FMLCommonHandler.class.getDeclaredField("sidedDelegate");
            delegate.setAccessible(true);
            delegate.set(handler, Proxy.newProxyInstance(IFMLSidedHandler.class.getClassLoader(),
                new Class<?>[]{IFMLSidedHandler.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getSide")) {
                        return Side.SERVER;
                    }
                    throw new UnsupportedOperationException(method.getName());
                }));
        }
        Bootstrap.register();
        // Use default configuration values without starting Forge's mod/config discovery.
        final var constructor = AEConfig.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var instance = AEConfig.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        instance.set(null, constructor.newInstance());
    }

    private static List<PackagedPatternDetails> patterns(final TestRecipe recipe) {
        return List.of(PackagedPatternDetails.packagePattern(recipe.pattern),
            PackagedPatternDetails.recipePattern(recipe), PackagedPatternDetails.directPattern(recipe));
    }

    private static TestRecipe largeRecipe(final int inputs) {
        final TestRecipe recipe = new TestRecipe();
        recipe.inputs.clear();
        for (int i = 0; i < inputs; i++) {
            final ItemStack ingredient = new ItemStack(Items.IRON_INGOT, i + 1);
            ingredient.getOrCreateSubCompound("test").setInteger("ingredient", i);
            recipe.inputs.add(ingredient);
        }
        recipe.patterns.clear();
        for (int start = 0; start < inputs; start += 9) {
            final int index = start / 9;
            final List<ItemStack> part = List.copyOf(recipe.inputs.subList(start, Math.min(start + 9, inputs)));
            recipe.patterns.add(new IPackagePattern() {
                @Override
                public IRecipeInfo getRecipeInfo() {
                    return recipe;
                }

                @Override
                public int getIndex() {
                    return index;
                }

                @Override
                public List<ItemStack> getInputs() {
                    return part;
                }

                @Override
                public ItemStack getOutput() {
                    final ItemStack output = new ItemStack(Items.PAPER);
                    output.getOrCreateSubCompound("test").setInteger("packageIndex", index);
                    return output;
                }
            });
        }
        return recipe;
    }

    private static AEItemKey encodePreview(final PackagedPatternDisplay.DisplayPattern display) {
        final AEItemKey definition = display.encode();
        assertTrue(AEItems.PROCESSING_PATTERN.is(definition));
        return definition;
    }

    @Test
    void definitionsSeparateModesWithoutChangingRealItems() {
        final TestRecipe recipe = new TestRecipe();
        final List<PackagedPatternDetails> patterns = patterns(recipe);
        assertEquals(3, new HashSet<>(patterns.stream().map(IPatternDetails::getDefinition).toList()).size());
        for (final PackagedPatternDetails pattern : patterns) {
            assertEquals(pattern.kind(), PackagedPatternKind.fromStack(pattern.getDefinition().toStack()));
            assertEquals(1, pattern.getDefinition().toStack().getCount());
        }
        assertNull(PackagedPatternKind.fromStack(recipe.pattern.getOutput()));
        assertNull(PackagedPatternKind.fromStack(patterns.getFirst().getPrimaryOutput().what() instanceof AEItemKey key
            ? key.toStack() : ItemStack.EMPTY));
        assertNull(PackagedPatternKind.fromStack(recipe.getInputs().getFirst()));
    }

    @Test
    void nativeDecoderRoundTripsAllModesAndKeepsEqualityStable() {
        for (final PackagedPatternDetails original : patterns(new TestRecipe())) {
            assertTrue(PackagedPatternDecoder.INSTANCE.isEncodedPattern(original.getDefinition().toStack()));
            final IPatternDetails decoded = PackagedPatternDecoder.INSTANCE.decodePattern(original.getDefinition(), null);
            assertInstanceOf(PackagedPatternDetails.class, decoded);
            assertEquals(original, decoded);
            assertEquals(original.hashCode(), decoded.hashCode());
            assertEquals(original.getOutputs(), decoded.getOutputs());
            assertArrayEquals(original.getInputs()[0].possibleInputs(), decoded.getInputs()[0].possibleInputs());
        }
    }

    @Test
    void decoderRejectsOrdinaryItemsAndMalformedDefinitions() {
        final TestRecipe recipe = new TestRecipe();
        final ItemStack ordinary = recipe.pattern.getOutput();
        assertFalse(PackagedPatternDecoder.INSTANCE.isEncodedPattern(ordinary));
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(AEItemKey.of(ordinary), null));
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern((AEItemKey) null, null));
        final ItemStack unknown = ordinary.copy();
        unknown.getOrCreateSubCompound("appcompat").setString("PatternKind", "unknown");
        assertFalse(PackagedPatternDecoder.INSTANCE.isEncodedPattern(unknown));
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(AEItemKey.of(unknown), null));
        final ItemStack wrongItem = PackagedPatternKind.PACKAGE.encodeDefinition(new ItemStack(Items.APPLE));
        assertFalse(PackagedPatternDecoder.INSTANCE.isEncodedPattern(wrongItem));
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(AEItemKey.of(wrongItem), null));
        final ItemStack invalidIndex = PackagedPatternKind.PACKAGE.encodeDefinition(ordinary);
        invalidIndex.getTagCompound().setInteger("Index", -1);
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(AEItemKey.of(invalidIndex), null));
        recipe.patterns.clear();
        assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(AEItemKey.of(invalidIndex), null));
    }

    @Test
    void decoderRejectsUnavailableRecipes() {
        final TestRecipe recipe = new TestRecipe();
        final List<PackagedPatternDetails> patterns = patterns(recipe);
        recipe.valid = false;
        for (final PackagedPatternDetails pattern : patterns) {
            assertNull(PackagedPatternDecoder.INSTANCE.decodePattern(pattern.getDefinition(), null));
        }
    }

    @Test
    void virtualInventoryRejectsEveryWritePathAndReturnsDefensiveCopies() {
        final PackagedPatternDetails pattern = patterns(new TestRecipe()).getFirst();
        final PackagedPatternInventory inventory = new PackagedPatternInventory(() -> List.of(pattern));
        final ItemStack expected = inventory.getStackInSlot(0);
        final ItemStack offered = new ItemStack(Items.APPLE, 12);
        assertFalse(inventory.isItemValid(0, offered));
        for (final boolean simulate : new boolean[]{true, false}) {
            assertSame(offered, inventory.insertItem(0, offered, simulate));
            assertTrue(inventory.extractItem(0, 1, simulate).isEmpty());
        }
        inventory.setItemDirect(0, offered);
        inventory.clear();
        inventory.toContainer().clear();
        inventory.toItemHandler().insertItem(0, offered, false);
        assertTrue(inventory.toItemHandler().extractItem(0, 1, false).isEmpty());
        inventory.getStackInSlot(0).getOrCreateSubCompound("appcompat").setString("PatternKind", "corrupted");
        assertTrue(ItemStack.areItemStacksEqual(expected, inventory.getStackInSlot(0)));
        assertEquals(1, inventory.getSlotLimit(0));
        assertTrue(inventory.contains(pattern.getDefinition()));
        assertFalse(inventory.contains(null));
        assertFalse(inventory.contains(AEItemKey.of(Items.APPLE)));
        assertEquals(0, inventory.getContentsVersion());
    }

    @Test
    void inventoryVersionDoesNotReadSlotsAndInvalidationRefreshesAdditionsAndRemovals() {
        final AtomicInteger reads = new AtomicInteger();
        final List<IPatternDetails> patterns = new ArrayList<>(patterns(new TestRecipe()));
        final PackagedPatternInventory inventory = new PackagedPatternInventory(() -> {
            reads.incrementAndGet();
            return patterns;
        });
        assertEquals(0, inventory.getContentsVersion());
        assertEquals(0, reads.get());
        assertEquals(3, inventory.size());
        inventory.getStackInSlot(0);
        assertEquals(1, reads.get());
        patterns.removeLast();
        assertEquals(3, inventory.size());
        inventory.invalidate();
        assertEquals(1, inventory.getContentsVersion());
        assertEquals(1, reads.get());
        assertEquals(2, inventory.size());
        patterns.clear();
        inventory.invalidate();
        assertEquals(0, inventory.size());
        assertEquals(2, inventory.getContentsVersion());
        patterns.addAll(patterns(new TestRecipe()));
        inventory.invalidate();
        assertEquals(3, inventory.size());
    }

    @Test
    void terminalAndCraftingShareOneImmutableSnapshotUntilInvalidated() {
        final AtomicInteger reads = new AtomicInteger();
        final List<IPatternDetails> source = new ArrayList<>(patterns(new TestRecipe()));
        final PackagedPatternInventory inventory = new PackagedPatternInventory(() -> {
            reads.incrementAndGet();
            return source;
        });
        final List<IPatternDetails> first = inventory.getPatterns();
        for (int i = 0; i < 100; i++) {
            assertSame(first, inventory.getPatterns());
            assertTrue(inventory.contains(first.getFirst().getDefinition()));
            assertEquals(first.size(), inventory.size());
        }
        assertEquals(1, reads.get());
        assertThrows(UnsupportedOperationException.class, first::clear);
        source.removeFirst();
        assertEquals(3, first.size());
        inventory.invalidate();
        assertNotSame(first, inventory.getPatterns());
        assertFalse(inventory.contains(first.getFirst().getDefinition()));
        assertEquals(2, inventory.size());
        assertEquals(2, reads.get());
    }

    @Test
    void packagingPreviewCombinesAllPackageIndicesAndMoreThanNineInputs() {
        final TestRecipe recipe = largeRecipe(18);
        final List<IPatternDetails> actual = recipe.getPatterns().stream()
                                                   .map(pattern -> (IPatternDetails) PackagedPatternDetails.packagePattern(pattern)).toList();
        final List<PackagedPatternDisplay.DisplayPattern> displays = PackagedPatternDisplay.build(actual);
        assertEquals(1, displays.size());
        assertEquals(18, displays.getFirst().inputs().size());
        assertEquals(2, displays.getFirst().outputs().size());
        assertNotEquals(displays.getFirst().outputs().get(0).what(), displays.getFirst().outputs().get(1).what());
        final AEProcessingPattern decoded = new AEProcessingPattern(encodePreview(displays.getFirst()));
        assertEquals(18, decoded.getSparseInputs().size());
        assertEquals(2, decoded.getSparseOutputs().size());
        assertEquals(displays.getFirst().inputs(), decoded.getSparseInputs());
        assertEquals(displays.getFirst().outputs(), decoded.getSparseOutputs());
        assertEquals(2, actual.size(), "The crafting CPU must still receive two separate package patterns");
        assertEquals(9, actual.getFirst().getInputs().length);
    }

    @Test
    void repeatedCraftingDefinitionsDoNotMultiplyPreviewAmounts() {
        final TestRecipe recipe = largeRecipe(18);
        final List<IPatternDetails> actual = recipe.getPatterns().stream()
                                                   .map(pattern -> (IPatternDetails) PackagedPatternDetails.packagePattern(pattern)).toList();
        final List<IPatternDetails> repeated = new ArrayList<>(actual);
        repeated.addAll(actual);
        assertEquals(PackagedPatternDisplay.build(actual), PackagedPatternDisplay.build(repeated));
    }

    @Test
    void directAndUnpackagingPreviewsPreserveEveryInputAndOutput() {
        final TestRecipe recipe = largeRecipe(18);
        recipe.outputs.add(new ItemStack(Items.DIAMOND));
        final List<IPatternDetails> actual = List.of(PackagedPatternDetails.directPattern(recipe),
            PackagedPatternDetails.recipePattern(recipe));
        final List<PackagedPatternDisplay.DisplayPattern> displays = PackagedPatternDisplay.build(actual);
        assertEquals(2, displays.size());
        assertEquals(18, displays.get(0).inputs().size());
        assertEquals(2, displays.get(1).inputs().size());
        for (final var display : displays) {
            assertEquals(2, display.outputs().size());
            final AEProcessingPattern decoded = new AEProcessingPattern(encodePreview(display));
            assertEquals(display.inputs(), decoded.getSparseInputs());
            assertEquals(display.outputs(), decoded.getSparseOutputs());
        }
    }

    @Test
    void processingInventoryKeepsDisplayAndCraftingDefinitionsSeparateAndLazy() {
        final TestRecipe recipe = largeRecipe(18);
        final List<IPatternDetails> actual = recipe.getPatterns().stream()
                                                   .map(pattern -> (IPatternDetails) PackagedPatternDetails.packagePattern(pattern)).toList();
        final AtomicInteger encodes = new AtomicInteger();
        final PackagedPatternInventory inventory = new PackagedPatternInventory(() -> actual, details -> {
            encodes.incrementAndGet();
            return PackagedPatternDisplay.build(details).stream().map(PackagedPatternTest::encodePreview).toList();
        });
        assertEquals(2, inventory.getPatterns().size());
        assertTrue(inventory.containsCraftingPattern(actual.getFirst().getDefinition()));
        assertEquals(0, encodes.get());
        assertEquals(1, inventory.size());
        final AEItemKey preview = AEItemKey.of(inventory.getStackInSlot(0));
        assertTrue(inventory.contains(preview));
        assertFalse(inventory.containsCraftingPattern(preview));
        assertEquals(18, new AEProcessingPattern(preview).getSparseInputs().size());
        inventory.getStackInSlot(0);
        assertEquals(1, encodes.get());
        inventory.invalidate();
        inventory.size();
        assertEquals(2, encodes.get());
    }

    @Test
    void eightyOneInputPreviewIncludesEveryPackageAndDoesNotMarkRealStacks() {
        final TestRecipe recipe = largeRecipe(81);
        final List<IPatternDetails> actual = recipe.getPatterns().stream()
                                                   .map(pattern -> (IPatternDetails) PackagedPatternDetails.packagePattern(pattern)).toList();
        final var display = PackagedPatternDisplay.build(actual).getFirst();
        final AEItemKey definition = encodePreview(display);
        final AEProcessingPattern decoded = new AEProcessingPattern(definition);
        assertEquals(81, decoded.getSparseInputs().size());
        assertEquals(9, decoded.getSparseOutputs().size());
        assertEquals(display.inputs(), decoded.getSparseInputs());
        assertEquals(display.outputs(), decoded.getSparseOutputs());
        for (final IPackagePattern pattern : recipe.getPatterns()) {
            assertNull(PackagedPatternKind.fromStack(pattern.getOutput()));
        }
    }

    @Test
    void processingPreviewPreservesFluidKeysAndLongAmounts() {
        final TestRecipe recipe = new TestRecipe();
        final GenericStack input = new GenericStack(AEFluidKey.of(FluidRegistry.WATER), 5_000_000_000L);
        final GenericStack output = new GenericStack(AEFluidKey.of(FluidRegistry.LAVA), 3_000_000_000L);
        recipe.inputs.clear();
        recipe.inputs.add(GenericStack.wrapInItemStack(input));
        recipe.outputs.add(GenericStack.wrapInItemStack(output));
        final var display = PackagedPatternDisplay.build(List.of(PackagedPatternDetails.directPattern(recipe))).getFirst();
        final AEProcessingPattern decoded = new AEProcessingPattern(encodePreview(display));
        assertEquals(List.of(input), decoded.getSparseInputs());
        assertEquals(display.outputs(), decoded.getSparseOutputs());
        assertEquals(output, decoded.getSparseOutputs().getLast());
    }

    private static final class PackageItem extends Item implements IPackageItem {
        private final TestRecipe recipe;

        private PackageItem(final TestRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public IRecipeInfo getRecipeInfo(final ItemStack stack) {
            return this.recipe;
        }

        @Override
        public int getIndex(final ItemStack stack) {
            return stack.hasTagCompound() ? stack.getTagCompound().getInteger("Index") : 0;
        }

        @Override
        public PatternType getPatternType(final ItemStack stack) {
            return null;
        }
    }

    private static final class TestRecipe implements IRecipeInfo {
        private final List<ItemStack> inputs = new ArrayList<>(List.of(new ItemStack(Items.IRON_INGOT, 2)));
        private final List<ItemStack> outputs = new ArrayList<>(List.of(new ItemStack(Items.GOLD_INGOT)));
        private final Item packageItem = new PackageItem(this);
        private final IPackagePattern pattern = new IPackagePattern() {
            @Override
            public IRecipeInfo getRecipeInfo() {
                return TestRecipe.this;
            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public List<ItemStack> getInputs() {
                return TestRecipe.this.getInputs();
            }

            @Override
            public ItemStack getOutput() {
                return new ItemStack(packageItem);
            }
        };
        private final List<IPackagePattern> patterns = new ArrayList<>(List.of(this.pattern));
        private boolean valid = true;

        @Override
        public void readFromNBT(final NBTTagCompound nbt) {
        }

        @Override
        public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
            return nbt;
        }

        @Override
        public IRecipeType getRecipeType() {
            return (IRecipeType) Proxy.newProxyInstance(IRecipeType.class.getClassLoader(),
                new Class<?>[]{IRecipeType.class}, (proxy, method, args) -> false);
        }

        @Override
        public boolean isValid() {
            return this.valid;
        }

        @Override
        public List<IPackagePattern> getPatterns() {
            return this.patterns;
        }

        @Override
        public List<ItemStack> getInputs() {
            return this.inputs;
        }

        @Override
        public List<ItemStack> getOutputs() {
            return this.outputs;
        }

        @Override
        public void generateFromStacks(final List<ItemStack> input, final List<ItemStack> output, final World world) {
        }

        @Override
        public Int2ObjectMap<ItemStack> getEncoderStacks() {
            return new Int2ObjectOpenHashMap<>();
        }
    }
}
