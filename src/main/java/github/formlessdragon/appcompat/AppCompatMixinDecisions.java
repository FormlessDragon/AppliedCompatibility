package github.formlessdragon.appcompat;

import net.minecraftforge.fml.common.Loader;

public final class AppCompatMixinDecisions {

    private static boolean enableMMCE = true;
    private static boolean enableGTCEu = true;
    private static boolean enablePackagedAuto = true;
    private static boolean enablePackagingProvider = true;
    public static final boolean mmceLoaded = Loader.isModLoaded("modularmachinery");
    public static final boolean topLoaded = Loader.isModLoaded("theoneprobe");
    public static final boolean mekengLoaded = Loader.isModLoaded("mekeng");
    public static final boolean gtceuLoaded = Loader.isModLoaded("gregtech");
    public static final boolean packagedautoLoaded = Loader.isModLoaded("packagedauto");
    public static final boolean packagingproviderLoaded = Loader.isModLoaded("packagingprovider");
    public static final boolean jeiLoaded = Loader.isModLoaded("jei");

    private AppCompatMixinDecisions() {
    }

    public static void refreshFromEnvironment() {
        enableMMCE = AppCompatConfig.enableMMCE;
        enableGTCEu = AppCompatConfig.enableGTCEu;
        enablePackagedAuto = AppCompatConfig.enablePackagedAuto;
        enablePackagingProvider = AppCompatConfig.enablePackagingProvider;
    }

    public static boolean shouldApply(final String mixinName) {
        final int split = mixinName.indexOf('.');
        if (split < 0) {
            return true;
        }

        final String group = mixinName.substring(0, split);

        return switch (group) {
            case "mmce" -> enableMMCE && mmceLoaded && shouldApplyMMCE(mixinName);
            case "gtceu" -> enableGTCEu && gtceuLoaded;
            case "packagedauto" -> enablePackagedAuto && packagedautoLoaded && shouldApplyPackage(mixinName);
            case "packagingprovider" -> enablePackagingProvider && packagingproviderLoaded;
            default -> true;
        };
    }

    private static boolean shouldApplyMMCE(final String mixinName) {
        if (mixinName.startsWith("mmce.top")) {
            return topLoaded;
        }
        if (mixinName.startsWith("mmce.mekeng")) {
            return mekengLoaded;
        }
        return true;
    }

    private static boolean shouldApplyPackage(final String mixinName) {
        if (mixinName.startsWith("packagedauto.jei")) {
            return jeiLoaded;
        }
        return true;
    }
}
