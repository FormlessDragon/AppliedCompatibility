package github.formlessdragon.appcompat.mixins.ae;

import ae2.api.inventories.InternalInventory;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemPowerStorageBridge;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "ae2.tile.misc.TileCharger$ChargerInvFilter", remap = false)
public abstract class MixinTileChargerInventoryFilter {

    @Inject(method = "allowInsert", at = @At("HEAD"), cancellable = true)
    private void appcompat$allowLegacyPoweredItemInsert(final InternalInventory inv, final int slot,
                                                        final ItemStack stack,
                                                        final CallbackInfoReturnable<Boolean> cir) {
        if (LegacyAeItemPowerStorageBridge.isChargeable(stack)
            && !(stack.getItem() instanceof ae2.api.implementations.items.IAEItemPowerStorage)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "allowExtract", at = @At("HEAD"), cancellable = true)
    private void appcompat$allowLegacyPoweredItemExtract(final InternalInventory inv, final int slot, final int amount,
                                                         final CallbackInfoReturnable<Boolean> cir) {
        final ItemStack stack = inv.getStackInSlot(slot);
        if (LegacyAeItemPowerStorageBridge.isChargeable(stack)
            && !(stack.getItem() instanceof ae2.api.implementations.items.IAEItemPowerStorage)) {
            cir.setReturnValue(LegacyAeItemPowerStorageBridge.isFull(stack));
        }
    }
}
