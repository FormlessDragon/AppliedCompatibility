package github.formlessdragon.appcompat.mixins.mc;

import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import github.formlessdragon.appcompat.common.item.AppCompatItems;
import github.formlessdragon.appcompat.common.item.ItemLegacyAeError;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRegistry.class, remap = false)
public abstract class MixinGameRegistry {

    @Inject(method = "makeItemStack", at = @At("HEAD"), cancellable = true)
    private static void appcompat$remapLegacyAeItemStack(final String itemName, final int meta, final int stackSize,
                                                        final String nbtString,
                                                        final CallbackInfoReturnable<ItemStack> cir) {
        if (!AppCompatConfig.enableLegacyAeItemIdRemapping) {
            return;
        }
        final LegacyAeItemMappings.SupplierItemStack supplier = LegacyAeItemMappings.get(itemName, meta);
        if (supplier == null) {
            return;
        }
        final ItemStack stack = supplier.get(itemName, meta);
        if (stack.isEmpty()) {
            throw new IllegalStateException("Legacy AE item mapping returned an empty stack for " + itemName
                + " @" + meta);
        }
        stack.setCount(stackSize);
        ac_applyNbt(stack, nbtString);
        cir.setReturnValue(stack);
    }

    @Unique
    private static void ac_applyNbt(final ItemStack stack, final String nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return;
        }
        if (stack.getItem() == AppCompatItems.ERROR_ITEM) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            tag.setString(ItemLegacyAeError.NBT_OLD_ITEM_NBT, nbt);
            stack.setTagCompound(tag);
            return;
        }
        try {
            stack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
        } catch (final NBTException e) {
            throw new RuntimeException("Encountered an exception parsing ItemStack NBT string " + nbt, e);
        }
    }
}
