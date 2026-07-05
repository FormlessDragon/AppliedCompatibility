package github.formlessdragon.appcompat.bridge.gtceu;

import ae2.core.definitions.AEBlocks;
import ae2.core.definitions.AEItems;
import gregtech.api.util.Mods;
import net.minecraft.item.ItemStack;

public final class GtceuAppliedEnergisticsRegistryBridge {

    private GtceuAppliedEnergisticsRegistryBridge() {
    }

    public static ItemStack resolve(final Mods mod, final String name, final int meta, final int amount, final String nbt) {
        if (mod != Mods.AppliedEnergistics2 || amount <= 0) {
            return ItemStack.EMPTY;
        }
        if ("interface".equals(name) || "fluid_interface".equals(name)) {
            return AEBlocks.INTERFACE.stack(amount);
        }
        if ("material".equals(name) && meta == 30) {
            return AEItems.SPEED_CARD.stack(amount);
        }
        return ItemStack.EMPTY;
    }
}
