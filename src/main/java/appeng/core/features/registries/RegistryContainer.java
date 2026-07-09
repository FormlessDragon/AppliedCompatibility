package appeng.core.features.registries;

import appeng.api.features.IChargerRegistry;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IGrinderRecipeBuilder;
import appeng.api.features.IGrinderRegistry;
import appeng.api.features.IInscriberRegistry;
import appeng.api.features.ILocatableRegistry;
import appeng.api.features.IMatterCannonAmmoRegistry;
import appeng.api.features.IP2PTunnelRegistry;
import appeng.api.features.IPlayerRegistry;
import appeng.api.features.IRecipeHandlerRegistry;
import appeng.api.features.IRegistryContainer;
import appeng.api.features.ISpecialComparisonRegistry;
import appeng.api.features.IWirelessTermRegistry;
import appeng.api.features.IWorldGen;
import appeng.api.movable.IMovableRegistry;
import appeng.api.networking.IGridCacheRegistry;
import appeng.api.parts.IPartModels;
import appeng.api.storage.ICellGuiHandler;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellRegistry;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import com.mojang.authlib.GameProfile;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeCellInventoryHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class RegistryContainer implements IRegistryContainer {

    private final IPlayerRegistry players = new PlayerRegistry();
    private final ICellRegistry cell = new CellRegistry();
    private final IGrinderRegistry grinder = new GrinderRegistry();
    private final IChargerRegistry charger = new ChargerRegistry();
    private final UnsupportedRegistries unsupported = new UnsupportedRegistries();

    @Override
    public IMovableRegistry movable() {
        return this.unsupported;
    }

    @Override
    public IGridCacheRegistry gridCache() {
        return this.unsupported;
    }

    @Override
    public ISpecialComparisonRegistry specialComparison() {
        return this.unsupported;
    }

    @Override
    public IWirelessTermRegistry wireless() {
        return this.unsupported;
    }

    @Override
    public ICellRegistry cell() {
        return this.cell;
    }

    @Override
    public IGrinderRegistry grinder() {
        return this.grinder;
    }

    @Override
    public IInscriberRegistry inscriber() {
        return this.unsupported;
    }

    @Override
    public IChargerRegistry charger() {
        return this.charger;
    }

    @Override
    public ILocatableRegistry locatable() {
        return this.unsupported;
    }

    @Override
    public IP2PTunnelRegistry p2pTunnel() {
        return this.unsupported;
    }

    @Override
    public IMatterCannonAmmoRegistry matterCannon() {
        return this.unsupported;
    }

    @Override
    public IPlayerRegistry players() {
        return this.players;
    }

    @Override
    public IRecipeHandlerRegistry recipes() {
        return this.unsupported;
    }

    @Override
    public IWorldGen worldgen() {
        return this.unsupported;
    }

    @Override
    public IPartModels partModels() {
        return this.unsupported;
    }

    private static final class PlayerRegistry implements IPlayerRegistry {

        @Override
        public int getID(final GameProfile profile) {
            if (profile == null || profile.getId() == null) {
                return -1;
            }
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) {
                throw new IllegalStateException("Cannot resolve AE player id without a running MinecraftServer");
            }
            return ae2.api.features.IPlayerRegistry.getMapping(server).getPlayerId(profile);
        }

        @Override
        public int getID(final EntityPlayer player) {
            if (player == null) {
                return -1;
            }
            return getID(player.getGameProfile());
        }

        @Override
        public EntityPlayer findPlayer(final int playerID) {
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) {
                throw new IllegalStateException("Cannot resolve AE player without a running MinecraftServer");
            }
            final EntityPlayerMP player = ae2.api.features.IPlayerRegistry.getConnected(server, playerID);
            return player;
        }
    }

    private static final class CellRegistry implements ICellRegistry {

        private final it.unimi.dsi.fastutil.objects.ObjectArrayList<ICellHandler> handlers =
            new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();
        private final it.unimi.dsi.fastutil.objects.ObjectArrayList<ICellGuiHandler> guiHandlers =
            new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();

        @Override
        public void addCellHandler(final ICellHandler handler) {
            Objects.requireNonNull(handler, "Cannot register null old AE cell handler");
            if (this.handlers.contains(handler)) {
                throw new IllegalArgumentException("Old AE cell handler is already registered: " + handler.getClass().getName());
            }
            this.handlers.add(handler);
        }

        @Override
        public void addCellGuiHandler(final ICellGuiHandler handler) {
            Objects.requireNonNull(handler, "Cannot register null old AE cell GUI handler");
            if (this.guiHandlers.contains(handler)) {
                throw new IllegalArgumentException("Old AE cell GUI handler is already registered: " + handler.getClass().getName());
            }
            this.guiHandlers.add(handler);
        }

        @Override
        public boolean isCellHandled(final ItemStack is) {
            if (is == null || is.isEmpty()) {
                return false;
            }
            for (final ICellHandler handler : this.handlers) {
                if (handler.isCell(is)) {
                    return true;
                }
            }
            return ae2.api.storage.StorageCells.isCellHandled(is);
        }

        @Override
        public ICellHandler getHandler(final ItemStack is) {
            if (is == null || is.isEmpty()) {
                return null;
            }
            for (final ICellHandler handler : this.handlers) {
                if (handler.isCell(is)) {
                    return handler;
                }
            }
            if (!ae2.api.storage.StorageCells.isCellHandled(is)) {
                return null;
            }
            return NewAeCellHandler.INSTANCE;
        }

        @Override
        public <T extends IAEStack<T>> ICellGuiHandler getGuiHandler(final IStorageChannel<T> channel,
                                                                     final ItemStack is) {
            Objects.requireNonNull(channel, "Cannot resolve old AE cell GUI handler for null channel");
            ICellGuiHandler fallback = null;
            for (final ICellGuiHandler handler : this.guiHandlers) {
                if (handler.isHandlerFor(channel)) {
                    if (handler.isSpecializedFor(is)) {
                        return handler;
                    }
                    if (fallback == null) {
                        fallback = handler;
                    }
                }
            }
            return fallback;
        }

        @Override
        public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(final ItemStack is,
                                                                                 final ISaveProvider host,
                                                                                 final IStorageChannel<T> chan) {
            if (is == null || is.isEmpty()) {
                return null;
            }
            Objects.requireNonNull(chan, "Cannot resolve old AE cell inventory for null channel");
            for (final ICellHandler handler : this.handlers) {
                if (handler.isCell(is)) {
                    final ICellInventoryHandler<T> inventory = handler.getCellInventory(is, host, chan);
                    if (inventory != null) {
                        return inventory;
                    }
                }
            }
            final ae2.api.storage.cells.StorageCell cell = ae2.api.storage.StorageCells.getCellInventory(is,
                host == null ? null : host::saveChanges);
            return cell == null ? null : new LegacyAeCellInventoryHandler<>(cell, chan);
        }
    }

    private enum NewAeCellHandler implements ICellHandler {
        INSTANCE;

        @Override
        public boolean isCell(final ItemStack is) {
            return is != null && !is.isEmpty() && ae2.api.storage.StorageCells.isCellHandled(is);
        }

        @Override
        public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(final ItemStack is,
                                                                                 final ISaveProvider host,
                                                                                 final IStorageChannel<T> channel) {
            if (is == null || is.isEmpty()) {
                return null;
            }
            final ae2.api.storage.cells.StorageCell cell = ae2.api.storage.StorageCells.getCellInventory(is,
                host == null ? null : host::saveChanges);
            return cell == null ? null : new LegacyAeCellInventoryHandler<>(cell, channel);
        }
    }

    private static final class ChargerRegistry implements IChargerRegistry {

        private static final double DEFAULT_CHARGE_RATE = 160.0D;
        private static final double MAX_CHARGE_RATE = 16000.0D;

        private final Reference2DoubleOpenHashMap<Item> chargeRates = new Reference2DoubleOpenHashMap<>();

        @Override
        public double getChargeRate(final Item item) {
            Objects.requireNonNull(item, "Cannot resolve old AE charger rate for null item");
            return this.chargeRates.containsKey(item) ? this.chargeRates.getDouble(item) : DEFAULT_CHARGE_RATE;
        }

        @Override
        public void addChargeRate(final Item item, final double chargeRate) {
            Objects.requireNonNull(item, "Cannot register old AE charger rate for null item");
            if (chargeRate <= 0.0D) {
                throw new IllegalArgumentException("Old AE charger rate must be positive: " + chargeRate);
            }
            this.chargeRates.put(item, Math.min(chargeRate, MAX_CHARGE_RATE));
        }

        @Override
        public void removeChargeRate(final Item item) {
            Objects.requireNonNull(item, "Cannot remove old AE charger rate for null item");
            this.chargeRates.removeDouble(item);
        }
    }

    private static final class GrinderRegistry implements IGrinderRegistry {

        private final Object2ObjectOpenHashMap<GrinderCacheKey, IGrinderRecipe> recipes =
            new Object2ObjectOpenHashMap<>();
        private final Object2IntOpenHashMap<String> dustRatios = new Object2IntOpenHashMap<>();

        @Override
        public IGrinderRecipeBuilder builder() {
            return new GrinderRecipeBuilder();
        }

        @Override
        public Collection<IGrinderRecipe> getRecipes() {
            return Collections.unmodifiableCollection(this.recipes.values());
        }

        @Override
        public boolean addRecipe(final IGrinderRecipe recipe) {
            Objects.requireNonNull(recipe, "Cannot register null old AE grinder recipe");
            final GrinderCacheKey key = new GrinderCacheKey(recipe.getInput());
            if (this.recipes.containsKey(key)) {
                return false;
            }
            this.recipes.put(key, recipe);
            return true;
        }

        @Override
        public boolean removeRecipe(final IGrinderRecipe recipe) {
            Objects.requireNonNull(recipe, "Cannot remove null old AE grinder recipe");
            return this.recipes.remove(new GrinderCacheKey(recipe.getInput())) != null;
        }

        @Override
        public IGrinderRecipe getRecipeForInput(final ItemStack input) {
            requireStack(input, "Cannot look up old AE grinder recipe for empty input");
            return this.recipes.get(new GrinderCacheKey(input));
        }

        @Override
        public void addDustRatio(final String oredictName, final int ratio) {
            Objects.requireNonNull(oredictName, "Cannot register old AE grinder dust ratio for null ore name");
            if (ratio <= 0) {
                throw new IllegalArgumentException("Old AE grinder dust ratio must be positive: " + ratio);
            }
            this.dustRatios.put(oredictName, ratio);
        }

        @Override
        public boolean removeDustRatio(final String oredictName) {
            Objects.requireNonNull(oredictName, "Cannot remove old AE grinder dust ratio for null ore name");
            if (!this.dustRatios.containsKey(oredictName)) {
                return false;
            }
            this.dustRatios.removeInt(oredictName);
            return true;
        }
    }

    private static final class GrinderRecipeBuilder implements IGrinderRecipeBuilder {

        private ItemStack input;
        private ItemStack output;
        private ItemStack optionalOutput;
        private ItemStack secondOptionalOutput;
        private float optionalChance;
        private float secondOptionalChance;
        private int turns = 8;

        @Override
        public IGrinderRecipeBuilder withInput(final ItemStack input) {
            this.input = requireStack(input, "Old AE grinder recipe input cannot be empty").copy();
            return this;
        }

        @Override
        public IGrinderRecipeBuilder withOutput(final ItemStack output) {
            this.output = requireStack(output, "Old AE grinder recipe output cannot be empty").copy();
            return this;
        }

        @Override
        public IGrinderRecipeBuilder withFirstOptional(final ItemStack optional, final float chance) {
            validateChance(chance);
            this.optionalOutput = requireStack(optional, "Old AE grinder first optional output cannot be empty").copy();
            this.optionalChance = chance;
            return this;
        }

        @Override
        public IGrinderRecipeBuilder withSecondOptional(final ItemStack optional, final float chance) {
            validateChance(chance);
            this.secondOptionalOutput = requireStack(optional, "Old AE grinder second optional output cannot be empty").copy();
            this.secondOptionalChance = chance;
            return this;
        }

        @Override
        public IGrinderRecipeBuilder withTurns(final int turns) {
            if (turns <= 0) {
                throw new IllegalArgumentException("Old AE grinder recipe turns must be positive: " + turns);
            }
            this.turns = turns;
            return this;
        }

        @Override
        public IGrinderRecipe build() {
            if (this.input == null) {
                throw new IllegalStateException("Old AE grinder recipe input is not defined");
            }
            if (this.output == null) {
                throw new IllegalStateException("Old AE grinder recipe output is not defined");
            }
            return new GrinderRecipe(this.input.copy(), this.output.copy(), copyOrNull(this.optionalOutput),
                copyOrNull(this.secondOptionalOutput), this.optionalChance, this.secondOptionalChance, this.turns);
        }

        private static void validateChance(final float chance) {
            if (chance < 0.0F || chance > 1.0F) {
                throw new IllegalArgumentException("Old AE grinder optional chance must be between 0 and 1: " + chance);
            }
        }
    }

    private static final class GrinderRecipe implements IGrinderRecipe {

        private final ItemStack input;
        private final ItemStack output;
        private final Optional<ItemStack> optionalOutput;
        private final Optional<ItemStack> secondOptionalOutput;
        private final float optionalChance;
        private final float secondOptionalChance;
        private final int turns;

        private GrinderRecipe(final ItemStack input, final ItemStack output, final ItemStack optionalOutput,
                              final ItemStack secondOptionalOutput, final float optionalChance,
                              final float secondOptionalChance, final int turns) {
            this.input = input;
            this.output = output;
            this.optionalOutput = Optional.ofNullable(optionalOutput);
            this.secondOptionalOutput = Optional.ofNullable(secondOptionalOutput);
            this.optionalChance = optionalChance;
            this.secondOptionalChance = secondOptionalChance;
            this.turns = turns;
        }

        @Override
        public ItemStack getInput() {
            return this.input;
        }

        @Override
        public ItemStack getOutput() {
            return this.output;
        }

        @Override
        public Optional<ItemStack> getOptionalOutput() {
            return this.optionalOutput;
        }

        @Override
        public Optional<ItemStack> getSecondOptionalOutput() {
            return this.secondOptionalOutput;
        }

        @Override
        public float getOptionalChance() {
            return this.optionalChance;
        }

        @Override
        public float getSecondOptionalChance() {
            return this.secondOptionalChance;
        }

        @Override
        public int getRequiredTurns() {
            return this.turns;
        }
    }

    private static final class GrinderCacheKey {

        private final Item item;
        private final int meta;

        private GrinderCacheKey(final ItemStack stack) {
            requireStack(stack, "Old AE grinder recipe key cannot be empty");
            this.item = stack.getItem();
            this.meta = stack.getItemDamage();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GrinderCacheKey key)) {
                return false;
            }
            return this.item == key.item && this.meta == key.meta;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(this.item) + this.meta;
        }
    }

    private static ItemStack requireStack(final ItemStack stack, final String message) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return stack;
    }

    private static ItemStack copyOrNull(final ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    private static final class UnsupportedRegistries implements IMovableRegistry, IGridCacheRegistry,
        ISpecialComparisonRegistry, IWirelessTermRegistry, IInscriberRegistry,
        ILocatableRegistry, IP2PTunnelRegistry, IMatterCannonAmmoRegistry,
        IRecipeHandlerRegistry, IWorldGen, IPartModels {
    }
}
