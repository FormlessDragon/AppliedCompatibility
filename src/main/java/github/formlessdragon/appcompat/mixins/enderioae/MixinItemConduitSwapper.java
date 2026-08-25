package github.formlessdragon.appcompat.mixins.enderioae;

import crazypants.enderio.conduits.item.conduitswapper.ItemConduitSwapper;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import github.formlessdragon.appcompat.bridge.enderioae.ConduitSwapperNetworkBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ItemConduitSwapper.class, remap = false)
public abstract class MixinItemConduitSwapper {

    @Inject(method = "onItemUseFirst", at = @At("HEAD"), cancellable = true)
    private void appcompat$bindAeNetwork(final EntityPlayer player, final World world, final BlockPos pos,
                                         final EnumFacing side, final float hitX, final float hitY,
                                         final float hitZ, final EnumHand hand,
                                         final CallbackInfoReturnable<EnumActionResult> cir) {
        if (player.isSneaking() && !(world.getTileEntity(pos) instanceof TileConduitBundle)) {
            cir.setReturnValue(ConduitSwapperNetworkBridge.bind(player, world, pos, player.getHeldItem(hand)));
        }
    }

    @Inject(method = "canAccessWireless", at = @At("HEAD"), cancellable = true)
    private void appcompat$useBoundAeNetwork(final EntityPlayer player, final ItemStack swapperStack,
                                             final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.hasAccess(player, swapperStack));
    }

    @Inject(method = "addCommonEntries", at = @At("HEAD"), cancellable = true)
    private void appcompat$showBoundAeNetwork(final ItemStack itemstack, final EntityPlayer entityplayer,
                                              final List<String> list, final boolean flag,
                                              final CallbackInfo ci) {
        if (ConduitSwapperNetworkBridge.appendBindingTooltip(itemstack, list)) {
            ci.cancel();
        }
    }
}
