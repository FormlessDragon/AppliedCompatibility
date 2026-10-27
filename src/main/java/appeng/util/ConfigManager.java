package appeng.util;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Set;

public class ConfigManager implements IConfigManager {

    private static final Logger LOGGER = LogManager.getLogger(ConfigManager.class);

    private final Reference2ObjectMap<Settings, Enum<?>> settings = new Reference2ObjectOpenHashMap<>();
    private final IConfigManagerHost host;

    public ConfigManager(final IConfigManagerHost host) {
        this.host = Objects.requireNonNull(host, "host");
    }

    @Override
    public Set<Settings> getSettings() {
        return this.settings.keySet();
    }

    @Override
    public void registerSetting(final Settings settingName, final Enum<?> defaultValue) {
        if (!settingName.getPossibleValues().contains(defaultValue)) {
            throw new IllegalArgumentException("Invalid value " + defaultValue + " for setting " + settingName);
        }
        this.settings.put(settingName, defaultValue);
    }

    @Override
    public Enum<?> getSetting(final Settings settingName) {
        final Enum<?> value = this.settings.get(settingName);
        if (value == null) {
            throw new IllegalStateException("Setting is not registered: " + settingName);
        }
        return value;
    }

    @Override
    public Enum<?> putSetting(final Settings settingName, final Enum<?> newValue) {
        final Enum<?> previous = this.getSetting(settingName);
        if (!settingName.getPossibleValues().contains(newValue)) {
            throw new IllegalArgumentException("Invalid value " + newValue + " for setting " + settingName);
        }
        this.settings.put(settingName, newValue);
        this.host.updateSetting(this, settingName, newValue);
        return previous;
    }

    @Override
    public void writeToNBT(final NBTTagCompound destination) {
        final var entries = Reference2ObjectMaps.fastIterator(this.settings);
        while (entries.hasNext()) {
            final var entry = entries.next();
            destination.setString(entry.getKey().name(), entry.getValue().name());
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound src) {
        for (final Settings setting : this.settings.keySet()) {
            if (!src.hasKey(setting.name())) {
                continue;
            }
            final String value = src.getString(setting.name());
            Enum<?> loaded = null;
            for (final Enum<?> candidate : setting.getPossibleValues()) {
                if (candidate.name().equals(value)) {
                    loaded = candidate;
                    break;
                }
            }
            if (loaded == null) {
                LOGGER.warn("Cannot load legacy AE setting {} from value '{}'; keeping {}",
                    setting, value, this.settings.get(setting));
            } else {
                this.putSetting(setting, loaded);
            }
        }
    }
}
