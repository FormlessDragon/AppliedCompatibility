package github.formlessdragon.appcompat.proxy;

import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.AppCompatAEHooks;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOThingQueue;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatMMCEHooks;
import github.formlessdragon.appcompat.bridge.mmce.mekeng.AppCompatMekEngInitHooks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static github.formlessdragon.appcompat.AppCompatMixinDecisions.mekengLoaded;
import static github.formlessdragon.appcompat.AppCompatMixinDecisions.mmceLoaded;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        if(AppCompatConfig.enableLegacyAeItemIdRemapping) {
            LegacyAeItemMappings.init();
            EnderIOThingQueue.flush();
        }
    }

    public void init(FMLInitializationEvent event) {
        AppCompatAEHooks.init();
        if (AppCompatConfig.enableMMCE) {
            if (mmceLoaded) AppCompatMMCEHooks.init();
            if (mmceLoaded && mekengLoaded) AppCompatMekEngInitHooks.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}
