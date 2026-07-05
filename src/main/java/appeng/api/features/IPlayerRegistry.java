package appeng.api.features;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

public interface IPlayerRegistry {

    int getID(GameProfile profile);

    int getID(EntityPlayer player);

    EntityPlayer findPlayer(int playerID);
}
