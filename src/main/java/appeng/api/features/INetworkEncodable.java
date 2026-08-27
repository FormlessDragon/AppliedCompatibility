package appeng.api.features;

import net.minecraft.item.ItemStack;

public interface INetworkEncodable {

    String getEncryptionKey(ItemStack stack);

    void setEncryptionKey(ItemStack stack, String encryptionKey, String mode);
}
