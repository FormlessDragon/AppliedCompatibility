package appeng.api.features;

import net.minecraft.item.ItemStack;

public interface INetworkEncodable {

    String getEncryptionKey(ItemStack item);

    void setEncryptionKey(ItemStack item, String encKey, String name);
}
