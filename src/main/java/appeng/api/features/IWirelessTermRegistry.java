package appeng.api.features;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWirelessTermRegistry {

    void registerWirelessHandler(IWirelessTermHandler handler);

    boolean isWirelessTerminal(ItemStack stack);

    IWirelessTermHandler getWirelessTerminalHandler(ItemStack stack);

    void openWirelessTerminalGui(ItemStack stack, World world, EntityPlayer player);

    boolean checkRange(ItemStack stack, EntityPlayer player);

    boolean performCheck(ItemStack stack, EntityPlayer player);
}
