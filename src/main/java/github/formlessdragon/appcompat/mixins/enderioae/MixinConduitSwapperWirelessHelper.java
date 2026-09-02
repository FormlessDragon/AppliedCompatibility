package github.formlessdragon.appcompat.mixins.enderioae;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.conduits.init.ConduitObject;
import crazypants.enderio.conduits.item.conduitswapper.ItemConduitSwapper;
import github.formlessdragon.appcompat.bridge.enderioae.ConduitSwapperNetworkBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Pseudo
@Mixin(targets = "crazypants.enderio.conduits.item.conduitswapper.ConduitSwapperWirelessHelper", remap = false)
public abstract class MixinConduitSwapperWirelessHelper {

    @Inject(method = "registerWirelessHandler", at = @At("HEAD"), cancellable = true)
    private static void appcompat$registerGridLinkable(final CallbackInfo ci) {
        ConduitSwapperNetworkBridge.registerGridLinkable(ConduitObject.item_conduit_swapper.getItemNN());
        ci.cancel();
    }

    @Inject(method = "hasAccess", at = @At("HEAD"), cancellable = true)
    private static void appcompat$hasAccess(final ItemConduitSwapper handler, final EntityPlayer player,
                                            final ItemStack swapperStack, final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.hasAccess(player, swapperStack));
    }

    @Inject(method = "countStack", at = @At("HEAD"), cancellable = true)
    private static void appcompat$countStack(final ItemConduitSwapper handler, final EntityPlayer player,
                                             final ItemStack swapperStack, final ItemStack template,
                                             final CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.countStack(player, swapperStack, template));
    }

    @Inject(method = "forEachCandidate", at = @At("HEAD"), cancellable = true)
    private static void appcompat$forEachCandidate(final ItemConduitSwapper handler, final EntityPlayer player,
                                                   final ItemStack swapperStack, final Class<? extends IConduit> baseType,
                                                   final ItemStack sourceStack, final BiConsumer<ItemStack, Integer> consumer,
                                                   final CallbackInfo ci) {
        ConduitSwapperNetworkBridge.forEachCandidate(player, swapperStack, baseType, sourceStack, consumer);
        ci.cancel();
    }

    @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
    private static void appcompat$extract(final ItemConduitSwapper handler, final EntityPlayer player,
                                          final ItemStack swapperStack, final ItemStack template, final int count,
                                          final CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.extract(player, swapperStack, template, count));
    }

    @Inject(method = "store", at = @At("HEAD"), cancellable = true)
    private static void appcompat$store(final ItemConduitSwapper handler, final EntityPlayer player,
                                        final ItemStack swapperStack, final ItemStack stackToStore,
                                        final CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(ConduitSwapperNetworkBridge.store(player, swapperStack, stackToStore));
    }
}
