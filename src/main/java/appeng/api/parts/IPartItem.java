package appeng.api.parts;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IPartItem<P extends IPart> {

    @Nullable
    P createPartFromItemStack(ItemStack is);
}
