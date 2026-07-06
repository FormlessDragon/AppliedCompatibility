package github.formlessdragon.appcompat.common.item;

import github.formlessdragon.appcompat.AppliedCompatibility;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ItemLegacyAeError extends Item {

    public static final String ID = "error_item";
    public static final String NBT_OLD_ITEM_NAME = "LegacyAeItemName";
    public static final String NBT_OLD_ITEM_META = "LegacyAeItemMeta";
    public static final String NBT_OLD_ITEM_NBT = "LegacyAeItemNbt";

    public ItemLegacyAeError() {
        this.setRegistryName(new ResourceLocation(AppliedCompatibility.LEGACY_AE_MOD_ID, ID));
        this.setTranslationKey(AppliedCompatibility.LEGACY_AE_MOD_ID + "." + ID);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip,
                               final ITooltipFlag flagIn) {
        final NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_OLD_ITEM_NAME)) {
            final ResourceLocation registryName = stack.getItem().getRegistryName();
            tooltip.add("原物品: " + (registryName == null ? "未注册物品" : registryName.toString()) + " @"
                + stack.getMetadata());
            tooltip.add("缺少旧物品映射数据");
            return;
        }
        tooltip.add("原物品: " + normalizeItemName(tag.getString(NBT_OLD_ITEM_NAME)) + " @"
            + tag.getInteger(NBT_OLD_ITEM_META));
    }

    public static ItemStack create(final String oldItemName, final int oldMeta) {
        final ItemStack stack = new ItemStack(AppCompatItems.ERROR_ITEM);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString(NBT_OLD_ITEM_NAME, normalizeItemName(oldItemName));
        tag.setInteger(NBT_OLD_ITEM_META, oldMeta);
        stack.setTagCompound(tag);
        return stack;
    }

    private static String normalizeItemName(final String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Legacy AE item name is required");
        }
        if (itemName.indexOf(':') >= 0) {
            return itemName;
        }
        return AppliedCompatibility.LEGACY_AE_MOD_ID + ':' + itemName;
    }
}
