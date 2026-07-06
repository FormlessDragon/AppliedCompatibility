package github.formlessdragon.appcompat.client;

import github.formlessdragon.appcompat.AppliedCompatibility;
import github.formlessdragon.appcompat.common.item.AppCompatItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = AppliedCompatibility.LEGACY_AE_MOD_ID, value = Side.CLIENT)
public final class AppCompatItemModels {

    private AppCompatItemModels() {
    }

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(AppCompatItems.ERROR_ITEM, 0,
            new ModelResourceLocation(AppCompatItems.ERROR_ITEM.getRegistryName(), "inventory"));
    }
}
