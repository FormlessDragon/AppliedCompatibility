package appeng.core.api;

import appeng.api.parts.CableRenderMode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHelper;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ApiPart implements IPartHelper {

    @Override
    public EnumActionResult placeBus(final ItemStack is, final BlockPos pos, final EnumFacing side,
                                     final EntityPlayer player, final EnumHand hand, final World world) {
        return ae2.api.parts.PartHelper.usePartItem(is, player, world, pos, hand, side, 0.5F, 0.5F, 0.5F);
    }

    @Override
    public CableRenderMode getCableRenderMode() {
        return CableRenderMode.valueOf(ae2.api.parts.PartHelper.getCableRenderMode().name());
    }

    @Nullable
    @Override
    public IPart getPart(final World world, final BlockPos pos, final AEPartLocation side) {
        final ae2.api.parts.IPart part = ae2.api.parts.PartHelper.getPart(world, pos, side.getFacing());
        return part == null ? null : new PartFacade(part);
    }

    @Nullable
    @Override
    public IPartHost getPartHost(final World world, final BlockPos pos) {
        final ae2.api.parts.IPartHost host = ae2.api.parts.PartHelper.getPartHost(world, pos);
        return host == null ? null : new PartHostFacade(host);
    }

    @Nullable
    @Override
    public IPartHost getOrPlacePartHost(final World world, final BlockPos pos, final boolean force,
                                        @Nullable final EntityPlayer player) {
        final ae2.api.parts.IPartHost host = ae2.api.parts.PartHelper.getOrPlacePartHost(world, pos, force, player);
        return host == null ? null : new PartHostFacade(host);
    }

    @Override
    public boolean canPlacePartHost(final World world, final BlockPos pos, @Nullable final EntityPlayer player) {
        return ae2.api.parts.PartHelper.canPlacePartHost(player, world, pos);
    }

    private record PartFacade(ae2.api.parts.IPart delegate) implements IPart {
    }

    private record PartHostFacade(ae2.api.parts.IPartHost delegate) implements IPartHost {

        @Override
        public IPart getPart(final AEPartLocation side) {
            final ae2.api.parts.IPart part = this.delegate.getPart(side.getFacing());
            return part == null ? null : new PartFacade(part);
        }
    }
}
