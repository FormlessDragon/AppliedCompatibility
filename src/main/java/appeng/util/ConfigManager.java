package appeng.util;

import java.util.Map;
import java.util.Set;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings({"unused", "rawtypes"})
public class ConfigManager implements IConfigManager {

    private final IConfigManagerHost host;
    private final Map<Settings, Enum> settings = new java.util.EnumMap<>(Settings.class);

    public ConfigManager(final IConfigManagerHost host) {
        this.host = host;
    }

    public void registerSetting(final Settings setting, final Enum defaultValue) {
        if (setting != null && defaultValue != null) {
            this.settings.putIfAbsent(setting, defaultValue);
        }
    }

    public Enum getSetting(final Settings setting) {
        return setting == null ? null : this.settings.get(setting);
    }

    public void putSetting(final Settings setting, final Enum value) {
        if (setting == null || value == null) {
            return;
        }
        final Enum previous = this.settings.put(setting, value);
        if (previous != value && this.host != null) {
            this.host.updateSetting(this, setting, value);
        }
    }

    public Set<Settings> getSettings() {
        return this.settings.keySet();
    }

    public void writeToNBT(final NBTTagCompound tag) {
        if (tag == null) {
            return;
        }
        for (final Map.Entry<Settings, Enum> entry : this.settings.entrySet()) {
            tag.setString(entry.getKey().name(), entry.getValue().name());
        }
    }

    public void readFromNBT(final NBTTagCompound tag) {
        if (tag == null) {
            return;
        }
        for (final Settings setting : Settings.values()) {
            final String stored = tag.getString(setting.name());
            if (stored == null || stored.isEmpty()) {
                continue;
            }
            final Enum current = this.settings.get(setting);
            if (current == null) {
                continue;
            }
            this.settings.put(setting, parseEnum(current, stored));
        }
    }

    @SuppressWarnings({"unchecked"})
    private static Enum parseEnum(final Enum current, final String name) {
        if (current.name().equals(name)) {
            return current;
        }
        try {
            return Enum.valueOf(current.getDeclaringClass(), name);
        } catch (final IllegalArgumentException ignored) {
            return current;
        }
    }
}
