package appeng.api.util;

import appeng.api.config.Settings;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

public interface IConfigManager {

    Set<Settings> getSettings();

    void registerSetting(Settings settingName, Enum<?> defaultValue);

    Enum<?> getSetting(Settings settingName);

    Enum<?> putSetting(Settings settingName, Enum<?> newValue);

    void writeToNBT(NBTTagCompound destination);

    void readFromNBT(NBTTagCompound src);
}
