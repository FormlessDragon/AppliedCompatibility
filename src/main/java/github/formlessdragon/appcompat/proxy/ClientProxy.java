package github.formlessdragon.appcompat.proxy;

import github.formlessdragon.appcompat.bridge.mmce.mekeng.AppCompatMekEngClientInitHooks;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        initMekEngClient();
        super.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    private void initMekEngClient() {
        if (Loader.isModLoaded("mekeng") && Loader.isModLoaded("mekanism")) {
            AppCompatMekEngClientInitHooks.init();
        }
    }

}
