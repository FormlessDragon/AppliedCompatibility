package github.formlessdragon.appcompat.mixins.enderioae;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import crazypants.enderio.conduit.me.conduit.IMEConduit;
import crazypants.enderio.conduit.me.conduit.MEConduitGrid;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOGridBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumSet;

@Mixin(value = MEConduitGrid.class, remap = false)
public abstract class MixinMEConduitGrid implements EnderIOGridBlockAccess {

    @Shadow
    @Final
    private IMEConduit conduit;

    @Shadow
    public abstract double getIdlePowerUsage();

    @Shadow
    public abstract AEColor getGridColor();

    @Shadow
    public abstract EnumSet<EnumFacing> getConnectableSides();

    @Shadow
    public abstract ItemStack getMachineRepresentation();

    @Shadow
    public abstract EnumSet<GridFlags> getFlags();

    @Shadow
    public abstract boolean isWorldAccessible();

    @Shadow
    public abstract DimensionalCoord getLocation();

    @Shadow
    public abstract void onGridNotification(GridNotification notification);

    @Shadow
    public abstract void setNetworkStatus(IGrid grid, int channelsInUse);

    @Shadow
    public abstract IGridHost getMachine();

    @Shadow
    public abstract void gridChanged();

    @Override
    public ItemStack appcompat$visualItemStack() {
        return getMachineRepresentation();
    }

    @Override
    public EnumSet<GridFlags> appcompat$legacyFlags() {
        return getFlags();
    }

    @Override
    public EnumSet<EnumFacing> appcompat$connectableSides() {
        return getConnectableSides();
    }

    @Override
    public World appcompat$world() {
        return this.conduit.getBundle().getBundleworld();
    }

    @Override
    public BlockPos appcompat$pos() {
        return this.conduit.getBundle().getLocation();
    }

    @Override
    public void appcompat$gridChanged() {
        gridChanged();
    }
}
