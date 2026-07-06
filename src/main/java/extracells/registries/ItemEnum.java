package extracells.registries;

import extracells.integration.Integration;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public enum ItemEnum {
    PARTITEM("part.base"),
    FLUIDSTORAGE("storage.fluid"),
    PHYSICALSTORAGE("storage.physical"),
    GASSTORAGE("storage.gas", Integration.Mods.MEKANISMGAS),
    FLUIDPATTERN("pattern.fluid"),
    FLUIDWIRELESSTERMINAL("terminal.fluid.wireless"),
    STORAGECOMPONET("storage.component"),
    STORAGECASING("storage.casing"),
    FLUIDITEM("fluid.item"),
    FLUIDSTORAGEPORTABLE("storage.fluid.portable"),
    GASSTORAGEPORTABLE("storage.gas.portable", Integration.Mods.MEKANISMGAS),
    CRAFTINGPATTERN("pattern.crafting"),
    UNIVERSALTERMINAL("terminal.universal.wireless"),
    GASWIRELESSTERMINAL("terminal.gas.wireless", Integration.Mods.MEKANISMGAS),
    OCUPGRADE("oc.upgrade", Integration.Mods.OPENCOMPUTERS),
    GASITEM("gas.item", Integration.Mods.MEKANISMGAS);

    private final String internalName;
    private final Integration.Mods mod;

    ItemEnum(final String internalName) {
        this(internalName, null);
    }

    ItemEnum(final String internalName, final Integration.Mods mod) {
        this.internalName = internalName;
        this.mod = mod;
    }

    public ItemStack getDamagedStack(final int damage) {
        return LegacyAeItemMappings.stack("extracells:" + this.internalName, damage, 1);
    }

    public String getInternalName() {
        return this.internalName;
    }

    public Item getItem() {
        return this.getDamagedStack(0).getItem();
    }

    public ItemStack getSizedStack(final int size) {
        return LegacyAeItemMappings.stack("extracells:" + this.internalName, 0, size);
    }

    public String getStatName() {
        return I18n.translateToLocal("item.extracells." + this.internalName + ".name");
    }

    public Integration.Mods getMod() {
        return this.mod;
    }

    public boolean shouldRegister() {
        return this.mod == null || this.mod.isEnabled();
    }
}
