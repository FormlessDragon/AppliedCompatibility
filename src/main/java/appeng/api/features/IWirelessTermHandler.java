package appeng.api.features;

import appeng.api.util.IConfigManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.IGuiHandler;

public interface IWirelessTermHandler {

    boolean canHandle(ItemStack is);

    boolean usePower(EntityPlayer player, double amount, ItemStack is);

    boolean hasPower(EntityPlayer player, double amount, ItemStack is);

    IConfigManager getConfigManager(ItemStack target);

    IGuiHandler getGuiHandler(ItemStack is);
}
