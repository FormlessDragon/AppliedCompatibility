package github.formlessdragon.appcompat.mixins.mc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import github.formlessdragon.appcompat.common.item.AppCompatItems;
import github.formlessdragon.appcompat.common.item.ItemLegacyAeError;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingHelper.class, remap = false)
public abstract class MixinCraftingHelper {

    @Inject(method = "getItemStackBasic", at = @At("HEAD"), cancellable = true)
    private static void appcompat$remapLegacyAeBasicStack(final JsonObject json, final JsonContext context,
                                                         final CallbackInfoReturnable<ItemStack> cir) {
        if (!AppCompatConfig.enableLegacyAeItemIdRemapping) {
            return;
        }
        final ItemStack stack = appcompat$mappedStack(json, context, 1);
        if (stack != null) {
            cir.setReturnValue(stack);
        }
    }

    @Inject(method = "getItemStack", at = @At("HEAD"), cancellable = true)
    private static void appcompat$remapLegacyAeStack(final JsonObject json, final JsonContext context,
                                                    final CallbackInfoReturnable<ItemStack> cir) {
        if (!AppCompatConfig.enableLegacyAeItemIdRemapping) {
            return;
        }
        final ItemStack stack = appcompat$mappedStack(json, context, JsonUtils.getInt(json, "count", 1));
        if (stack == null) {
            return;
        }
        if (json.has("nbt")) {
            appcompat$applyNbt(stack, json.get("nbt"));
        }
        cir.setReturnValue(stack);
    }

    @Unique
    private static ItemStack appcompat$mappedStack(final JsonObject json, final JsonContext context,
                                                  final int stackSize) {
        if (context == null) {
            throw new IllegalArgumentException("Crafting recipe JsonContext cannot be null");
        }
        final String itemName = context.appendModId(JsonUtils.getString(json, "item"));
        final int meta = JsonUtils.getInt(json, "data", 0);
        return LegacyAeItemMappings.mappedStackOrNull(itemName, meta, stackSize);
    }

    @Unique
    private static void appcompat$applyNbt(final ItemStack stack, final JsonElement element) {
        final String nbt;
        if (element.isJsonObject()) {
            nbt = CraftingHelper.GSON.toJson(element);
        } else {
            nbt = JsonUtils.getString(element, "nbt");
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
