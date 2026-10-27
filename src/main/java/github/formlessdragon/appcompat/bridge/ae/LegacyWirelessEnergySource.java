package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.config.Actionable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface LegacyWirelessEnergySource {

    double appcompat$extractWirelessPower(EntityPlayer player, ItemStack stack, double amount, Actionable mode);
}
