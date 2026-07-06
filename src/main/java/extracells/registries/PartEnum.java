package extracells.registries;

import appeng.api.config.Upgrades;
import extracells.integration.Integration;
import extracells.part.PartECBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

import java.util.Map;

public enum PartEnum {
    FLUIDEXPORT("fluid.export", "fluid.IO"),
    FLUIDIMPORT("fluid.import", "fluid.IO"),
    FLUIDSTORAGE("fluid.storage"),
    FLUIDTERMINAL("fluid.terminal"),
    FLUIDLEVELEMITTER("fluid.levelemitter"),
    FLUIDPANEANNIHILATION("fluid.plane.annihilation", "fluid.plane"),
    FLUIDPANEFORMATION("fluid.plane.formation", "fluid.plane"),
    DRIVE("drive"),
    BATTERY("battery"),
    INTERFACE("interface"),
    FLUIDMONITOR("fluid.monitor"),
    FLUIDCONVERSIONMONITOR("fluid.conversion.monitor"),
    OREDICTEXPORTBUS("oredict.export"),
    GASIMPORT("gas.import", "gas.IO", Integration.Mods.MEKANISMGAS),
    GASEXPORT("gas.export", "gas.IO", Integration.Mods.MEKANISMGAS),
    GASTERMINAL("gas.terminal", Integration.Mods.MEKANISMGAS),
    GASSTORAGE("gas.storage", null, Integration.Mods.MEKANISMGAS),
    GASLEVELEMITTER("gas.levelemitter", Integration.Mods.MEKANISMGAS),
    GASMONITOR("gas.monitor", Integration.Mods.MEKANISMGAS),
    GASCONVERSIONMONITOR("gas.conversion.monitor", Integration.Mods.MEKANISMGAS);

    private final String name;
    private final String groupName;
    private final Integration.Mods mod;
    private final Object2IntMap<Upgrades> upgrades = new Object2IntOpenHashMap<>();

    PartEnum(final String name) {
        this(name, null, null);
    }

    PartEnum(final String name, final Integration.Mods mod) {
        this(name, null, mod);
    }

    PartEnum(final String name, final String groupName) {
        this(name, groupName, null);
    }

    PartEnum(final String name, final String groupName, final Integration.Mods mod) {
        this.name = name;
        this.groupName = groupName == null || groupName.isEmpty() ? null : "extracells." + groupName;
        this.mod = mod;
    }

    public static int getPartID(final Class<? extends PartECBase> partClass) {
        return -1;
    }

    public static ItemStack getPartByName(final String name) {
        final PartEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equals(name)) {
                return ItemEnum.PARTITEM.getDamagedStack(i);
            }
        }
        return null;
    }

    public static int getPartID(final PartECBase partECBase) {
        if (partECBase == null) {
            throw new IllegalArgumentException("ExtraCells part is required");
        }
        return getPartID(partECBase.getClass());
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Class<? extends PartECBase> getPartClass() {
        return null;
    }

    public String getStatName() {
        return I18n.translateToLocal("extracells.part." + this.name + ".name");
    }

    public String getTranslationKey() {
        return "extracells.part." + this.name;
    }

    public Map<Upgrades, Integer> getUpgrades() {
        return Object2IntMaps.unmodifiable(this.upgrades);
    }

    public PartECBase newInstance(final ItemStack partStack) {
        throw new UnsupportedOperationException("ExtraCells legacy part instantiation is not implemented for "
            + this.name);
    }

    public Integration.Mods getMod() {
        return this.mod;
    }
}
