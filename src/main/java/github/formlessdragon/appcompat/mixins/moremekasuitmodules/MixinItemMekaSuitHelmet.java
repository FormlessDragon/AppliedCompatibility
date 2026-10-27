package github.formlessdragon.appcompat.mixins.moremekasuitmodules;

import ae2.api.config.Actionable;
import ae2.api.implementations.guiobjects.IGuiItem;
import ae2.api.implementations.guiobjects.ItemGuiHost;
import ae2.core.gui.locator.ItemGuiHostLocator;
import github.formlessdragon.appcompat.bridge.ae.LegacyWirelessEnergySource;
import github.formlessdragon.appcompat.bridge.moremekasuitmodules.MekaSuitWirelessBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "mekanism.common.item.armor.ItemMekaSuitHelmet", remap = false, priority = 900)
public abstract class MixinItemMekaSuitHelmet implements IGuiItem, LegacyWirelessEnergySource {

    @Override
    public ItemGuiHost<?> getGuiHost(final EntityPlayer player, final ItemGuiHostLocator locator,
                                    final RayTraceResult hitResult) {
        return MekaSuitWirelessBridge.createHost(player, locator, null);
    }

    @Override
    public double appcompat$extractWirelessPower(final EntityPlayer player, final ItemStack stack,
                                                final double amount, final Actionable mode) {
        return MekaSuitWirelessBridge.extractPower(player, stack, amount, mode);
    }

    @Dynamic("Added by MoreMekaSuitModules' helmet mixin")
    @Inject(method = "addaemodule", at = @At("HEAD"), cancellable = true)
    private void appcompat$wirelessTooltip(final ItemStack stack, final List<String> tooltip, final CallbackInfo ci) {
        MekaSuitWirelessBridge.addTooltip(stack, tooltip);
        ci.cancel();
    }

    @Dynamic("Added by MoreMekaSuitModules' helmet mixin")
    @Inject(method = "hasPower", at = @At("HEAD"), cancellable = true)
    private void appcompat$hasPower(final EntityPlayer player, final double amount, final ItemStack stack,
                                    final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MekaSuitWirelessBridge.hasPower(player, stack, amount));
    }

    @Dynamic("Added by MoreMekaSuitModules' helmet mixin")
    @Inject(method = "usePower", at = @At("HEAD"), cancellable = true)
    private void appcompat$consumePower(final EntityPlayer player, final double amount, final ItemStack stack,
                                        final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MekaSuitWirelessBridge.hasPower(player, stack, amount)
            && MekaSuitWirelessBridge.extractPower(player, stack, amount, Actionable.MODULATE) >= amount);
    }
}
