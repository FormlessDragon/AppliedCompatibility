package appeng.api.parts;

import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IPartHelper {

    EnumActionResult placeBus(ItemStack is, BlockPos pos, EnumFacing side, EntityPlayer player, EnumHand hand,
                              World world);

    CableRenderMode getCableRenderMode();

    @Nullable
    IPart getPart(World world, BlockPos pos, AEPartLocation side);

    @Nullable
    IPartHost getPartHost(World world, BlockPos pos);

    @Nullable
    IPartHost getOrPlacePartHost(World world, BlockPos pos, boolean force, @Nullable EntityPlayer player);

    boolean canPlacePartHost(World world, BlockPos pos, @Nullable EntityPlayer player);
}
