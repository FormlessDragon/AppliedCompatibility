package appeng.api.implementations.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IAEWrench extends ae2.api.util.IAEWrench {

    boolean canWrench(ItemStack wrench, EntityPlayer player, BlockPos pos);

    default boolean isUsable(final ItemStack wrench, final EntityLivingBase user, final BlockPos pos) {
        return user instanceof EntityPlayer player && this.canWrench(wrench, player, pos);
    }
}
