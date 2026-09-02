package github.formlessdragon.appcompat.mixins.crt;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BracketHandlerItem.class, remap = false)
public class MixinBracketHandlerItem {

    @Inject(method = "getItem", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private static void appcompat$remapLegacyAEItem(final String name, final int meta, CallbackInfoReturnable<IItemStack> cir) {
        if(name == null) {
            return;
        }

        final String trimmed = name.trim();
        if(trimmed.isEmpty() || !LegacyAeItemMappings.isInitialized() || !LegacyAeItemMappings.isLegacySpec(name)) {
            return;
        }

        final ItemStack mapped = LegacyAeItemMappings.mappedStackOrNull(trimmed, meta, 1);
        if(mapped == null) {
            return;
        }
        cir.setReturnValue(CraftTweakerMC.getIItemStackForMatching(mapped, true));
    }

    @ModifyExpressionValue(method = "find", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean appcompat$prepareRemap(boolean original, @Local(name = "itemName") String itemName) {
        return original || (AppCompatConfig.enableLegacyAeItemIdRemapping && LegacyAeItemMappings.isLegacySpec(itemName));
    }

}
