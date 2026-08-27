package github.formlessdragon.appcompat.mixins.enderioae;

import crazypants.enderio.conduits.item.conduitswapper.ItemConduitSwapper;
import github.formlessdragon.appcompat.bridge.enderioae.ConduitSwapperNetworkBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(value = ItemConduitSwapper.class, remap = false)
public abstract class MixinItemConduitSwapper extends Item {

    @Inject(method = "canAccessWireless", at = @At("HEAD"), cancellable = true)
    private void appcompat$useBoundAeNetwork(final EntityPlayer player, final ItemStack swapperStack,
                                             final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.hasAccess(player, swapperStack));
    }

    @Inject(method = "addCommonEntries", at = @At("HEAD"), cancellable = true)
    private void appcompat$showBoundAeNetwork(final ItemStack itemstack, final EntityPlayer entityplayer,
                                              final List<String> list, final boolean flag,
                                              final CallbackInfo ci) {
        ConduitSwapperNetworkBridge.appendBindingTooltip(itemstack, list);
        ci.cancel();
    }

}
