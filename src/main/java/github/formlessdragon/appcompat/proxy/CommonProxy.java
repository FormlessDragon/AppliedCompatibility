package github.formlessdragon.appcompat.proxy;

import ae2.api.crafting.PatternDetailsHelper;
import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.AppCompatAEHooks;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOThingQueue;
import github.formlessdragon.appcompat.bridge.mmce.AppCompatMMCEHooks;
import github.formlessdragon.appcompat.bridge.mmce.mekeng.AppCompatMekEngInitHooks;
import github.formlessdragon.appcompat.bridge.moremekasuitmodules.MekaSuitWirelessBridge;
import github.formlessdragon.appcompat.bridge.packagedauto.PackagedPatternDecoder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static github.formlessdragon.appcompat.AppCompatMixinDecisions.mekengLoaded;
import static github.formlessdragon.appcompat.AppCompatMixinDecisions.mmceLoaded;
import static github.formlessdragon.appcompat.AppCompatMixinDecisions.moreMekaSuitModulesLoaded;
import static github.formlessdragon.appcompat.AppCompatMixinDecisions.packagedautoLoaded;
import static github.formlessdragon.appcompat.AppCompatMixinDecisions.packagingproviderLoaded;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        if(AppCompatConfig.enableLegacyAeItemIdRemapping) {
            LegacyAeItemMappings.init();
            EnderIOThingQueue.flush();
        }
    }

    public void init(FMLInitializationEvent event) {
        AppCompatAEHooks.init();
        if (packagedautoLoaded && (AppCompatConfig.enablePackagedAuto
            || packagingproviderLoaded && AppCompatConfig.enablePackagingProvider)) {
            PatternDetailsHelper.registerDecoder(PackagedPatternDecoder.INSTANCE);
        }
        if (AppCompatConfig.enableMMCE) {
            if (mmceLoaded) AppCompatMMCEHooks.init();
            if (mmceLoaded && mekengLoaded) AppCompatMekEngInitHooks.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (moreMekaSuitModulesLoaded) {
            MekaSuitWirelessBridge.init();
        }
    }

}
