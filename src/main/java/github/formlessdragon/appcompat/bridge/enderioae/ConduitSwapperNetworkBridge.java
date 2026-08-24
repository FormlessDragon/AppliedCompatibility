package github.formlessdragon.appcompat.bridge.enderioae;

import ae2.api.AECapabilities;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.networking.security.IActionSource;
import ae2.api.stacks.AEItemKey;
import ae2.api.stacks.AEKey;
import ae2.api.stacks.KeyCounter;
import ae2.api.storage.MEStorage;
import ae2.api.storage.StorageHelper;
import ae2.api.networking.storage.IStorageService;
import ae2.api.util.DimensionalBlockPos;
import ae2.util.Platform;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.function.BiConsumer;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public final class ConduitSwapperNetworkBridge {

    private static final String BINDING_KEY = "appcompat_ae_network";

    private ConduitSwapperNetworkBridge() {
    }

    public static EnumActionResult bind(final EntityPlayer player, final World world, final BlockPos pos,
                                        final ItemStack stack) {
        if (!player.isSneaking() || stack.isEmpty()) {
            return EnumActionResult.PASS;
        }
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        if (!Platform.hasPermissions(new DimensionalBlockPos(world, pos), player)) {
            player.sendStatusMessage(new TextComponentTranslation("appcompat.conduitswapper.bind.denied"), true);
            return EnumActionResult.FAIL;
        }
        final IInWorldGridNodeHost host = findHost(world, pos);
        if (host == null) {
            player.sendStatusMessage(new TextComponentTranslation("appcompat.conduitswapper.bind.invalid_target"), true);
            return EnumActionResult.FAIL;
        }
        final IGridNode node = findNode(host);
        if (node == null || !node.isActive()) {
            player.sendStatusMessage(new TextComponentTranslation("appcompat.conduitswapper.bind.inactive"), true);
            return EnumActionResult.FAIL;
        }
        if (isBoundTo(stack, world.provider.getDimension(), pos)) {
            stack.getTagCompound().removeTag(BINDING_KEY);
            player.sendStatusMessage(new TextComponentTranslation("appcompat.conduitswapper.bind.unbound"), true);
            return EnumActionResult.SUCCESS;
        }
        final NBTTagCompound binding = new NBTTagCompound();
        binding.setInteger("dimension", world.provider.getDimension());
        binding.setInteger("x", pos.getX());
        binding.setInteger("y", pos.getY());
        binding.setInteger("z", pos.getZ());
        binding.setString("block", String.valueOf(world.getBlockState(pos).getBlock().getRegistryName()));
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag(BINDING_KEY, binding);
        player.sendStatusMessage(new TextComponentTranslation("appcompat.conduitswapper.bind.success"), true);
        return EnumActionResult.SUCCESS;
    }

    public static boolean hasAccess(final EntityPlayer player, final ItemStack stack) {
        return player.world.isRemote ? hasBinding(stack) : context(player, stack) != null;
    }

    public static boolean appendBindingTooltip(final ItemStack stack, final java.util.List<String> tooltip) {
        if (!hasBinding(stack)) {
            return false;
        }
        final NBTTagCompound binding = stack.getTagCompound().getCompoundTag(BINDING_KEY);
        tooltip.add(TextFormatting.GREEN + new TextComponentTranslation("appcompat.conduitswapper.tooltip.bound")
            .getFormattedText());
        tooltip.add(TextFormatting.GREEN + new TextComponentTranslation("appcompat.conduitswapper.tooltip.target",
            boundBlockName(binding)).getFormattedText());
        tooltip.add(TextFormatting.GREEN + new TextComponentTranslation("appcompat.conduitswapper.tooltip.position",
            binding.getInteger("dimension"), binding.getInteger("x"), binding.getInteger("y"),
            binding.getInteger("z")).getFormattedText());
        return true;
    }

    public static BlockPos getBoundPosition(final ItemStack stack) {
        if (!hasBinding(stack)) {
            return null;
        }
        final NBTTagCompound binding = stack.getTagCompound().getCompoundTag(BINDING_KEY);
        return new BlockPos(binding.getInteger("x"), binding.getInteger("y"), binding.getInteger("z"));
    }

    public static boolean isBoundInDimension(final ItemStack stack, final int dimension) {
        return hasBinding(stack) && stack.getTagCompound().getCompoundTag(BINDING_KEY).getInteger("dimension")
            == dimension;
    }

    public static int countStack(final EntityPlayer player, final ItemStack stack, final ItemStack query) {
        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(query);
        if (context == null || key == null) {
            return 0;
        }
        final long amount = context.cachedInventory.get(key);
        return clamp(amount);
    }

    public static void forEachCandidate(final EntityPlayer player, final ItemStack stack,
                                        final Class<? extends IConduit> conduitClass, final ItemStack source,
                                        final BiConsumer<ItemStack, Integer> consumer) {
        final Context context = context(player, stack);
        if (context == null) {
            return;
        }
        for (final Object2LongMap.Entry<AEKey> entry : context.cachedInventory) {
            if (entry.getLongValue() <= 0 || !(entry.getKey() instanceof AEItemKey key)) {
                continue;
            }
            final ItemStack candidate = key.toStack(clamp(entry.getLongValue()));
            if (candidate.getItem() instanceof IConduitItem conduitItem
                && conduitItem.getBaseConduitType() == conduitClass
                && !com.enderio.core.common.util.ItemUtil.areStacksEqual(candidate, source)) {
                consumer.accept(candidate, clamp(entry.getLongValue()));
            }
        }
    }

    public static int extract(final EntityPlayer player, final ItemStack stack, final ItemStack requested,
                              final int amount) {
        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(requested);
        if (context == null || key == null || amount <= 0) {
            return 0;
        }
        final long extracted = StorageHelper.poweredExtraction(context.energy, context.storage, key, amount,
            IActionSource.ofPlayer(player));
        return clamp(extracted);
    }

    public static ItemStack store(final EntityPlayer player, final ItemStack stack, final ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final Context context = context(player, stack);
        final AEItemKey key = AEItemKey.of(input);
        if (context == null || key == null) {
            return input;
        }
        final long inserted = StorageHelper.poweredInsert(context.energy, context.storage, key, input.getCount(),
            IActionSource.ofPlayer(player));
        if (inserted >= input.getCount()) {
            return ItemStack.EMPTY;
        }
        final ItemStack remaining = input.copy();
        remaining.shrink((int) inserted);
        return remaining;
    }

    private static Context context(final EntityPlayer player, final ItemStack stack) {
        if (!hasBinding(stack)) {
            return null;
        }
        final NBTTagCompound binding = stack.getTagCompound().getCompoundTag(BINDING_KEY);
        final int dimension = binding.getInteger("dimension");
        final World world = player.world.provider.getDimension() == dimension
            ? player.world : DimensionManager.getWorld(dimension);
        if (!(world instanceof WorldServer)) {
            return null;
        }
        final BlockPos pos = new BlockPos(binding.getInteger("x"), binding.getInteger("y"), binding.getInteger("z"));
        if (!Platform.hasPermissions(new DimensionalBlockPos(world, pos), player)) {
            return null;
        }
        final IInWorldGridNodeHost host = findHost(world, pos);
        final IGridNode node = host == null ? null : findNode(host);
        if (node == null || !node.isActive()) {
            return null;
        }
        final IGrid grid = node.grid();
        return grid == null ? null : new Context(grid.getStorageService(), grid.getEnergyService());
    }

    private static IInWorldGridNodeHost findHost(final World world, final BlockPos pos) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return null;
        }
        if (tile instanceof IInWorldGridNodeHost host) {
            return host;
        }
        return tile.hasCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, null)
            ? tile.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, null) : null;
    }

    private static IGridNode findNode(final IInWorldGridNodeHost host) {
        final IGridNode internal = host.getGridNode(null);
        if (internal != null && internal.isActive()) {
            return internal;
        }
        IGridNode fallback = internal;
        for (final EnumFacing side : EnumFacing.VALUES) {
            final IGridNode node = host.getGridNode(side);
            if (node != null && node.isActive()) {
                return node;
            }
            if (fallback == null) {
                fallback = node;
            }
        }
        return fallback;
    }

    private static boolean hasBinding(final ItemStack stack) {
        return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey(BINDING_KEY, 10);
    }

    private static boolean isBoundTo(final ItemStack stack, final int dimension, final BlockPos pos) {
        if (!hasBinding(stack)) {
            return false;
        }
        final NBTTagCompound binding = stack.getTagCompound().getCompoundTag(BINDING_KEY);
        return binding.getInteger("dimension") == dimension && binding.getInteger("x") == pos.getX()
            && binding.getInteger("y") == pos.getY() && binding.getInteger("z") == pos.getZ();
    }

    private static String boundBlockName(final NBTTagCompound binding) {
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(binding.getString("block")));
        return block == null ? binding.getString("block") : block.getLocalizedName();
    }

    private static int clamp(final long amount) {
        return (int) Math.clamp(amount, 0L, Integer.MAX_VALUE);
    }

    private static final class Context {
        private final MEStorage storage;
        private final KeyCounter cachedInventory;
        private final ae2.api.networking.energy.IEnergyService energy;

        private Context(final IStorageService storageService,
                        final ae2.api.networking.energy.IEnergyService energy) {
            this.storage = storageService.getInventory();
            this.cachedInventory = storageService.getCachedInventory();
            this.energy = energy;
        }
    }
}
