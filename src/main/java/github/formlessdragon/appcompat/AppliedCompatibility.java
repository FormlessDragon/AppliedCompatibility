package github.formlessdragon.appcompat;

import github.formlessdragon.appcompat.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "appliedenergistics2", name = Tags.MOD_NAME, version = Tags.VERSION, acceptedMinecraftVersions = "[1.12.2]",
    dependencies = "required-after:ae2@[1.0.9,);after:mekeng;"
)
public class AppliedCompatibility {

    public static final String LEGACY_AE_MOD_ID = "appliedenergistics2";
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(clientSide = "github.formlessdragon.appcompat.proxy.ClientProxy", serverSide = "github.formlessdragon.appcompat.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
