package github.formlessdragon.appcompat.common.item;

import github.formlessdragon.appcompat.AppliedCompatibility;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AppliedCompatibility.LEGACY_AE_MOD_ID)
public final class AppCompatItems {

    public static final ItemLegacyAeError ERROR_ITEM = new ItemLegacyAeError();

    private AppCompatItems() {
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ERROR_ITEM);
    }
}
