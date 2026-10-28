package appeng.util;

import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.core.sync.GuiBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Platform {

    public static boolean isClient() {
        return false;
    }

    public static boolean isServer() {
        return false;
    }

    public static void notifyBlocksOfNeighbors(final World world, final BlockPos pos) {
    }

    public static void openGUI(final EntityPlayer p, final TileEntity tile, final AEPartLocation side, final GuiBridge type) {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IAEStack poweredExtraction(final IEnergySource energy, final IMEInventory cell, final IAEStack request, final IActionSource src) {
        return cell.extractItems(request, Actionable.MODULATE, src);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IAEStack poweredInsert(final IEnergySource energy, final IMEInventory cell, final IAEStack input, final IActionSource src) {
        return cell.injectItems(input, Actionable.MODULATE, src);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IAEStack poweredInsert(final IEnergySource energy, final IMEInventory cell, final IAEStack input, final IActionSource src, final Actionable mode) {
        return cell.injectItems(input, mode, src);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IAEStack poweredExtraction(final IEnergySource energy, final IMEInventory cell, final IAEStack request, final IActionSource src, final Actionable mode) {
        return cell.extractItems(request, mode, src);
    }
}
