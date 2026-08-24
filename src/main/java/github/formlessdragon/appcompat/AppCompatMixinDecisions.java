package github.formlessdragon.appcompat;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;

public final class AppCompatMixinDecisions {

    private static boolean enableMMCE = true;
    private static boolean enableGTCEu = true;
    private static boolean enablePackagedAuto = true;
    private static boolean enablePackagingProvider = true;
    private static boolean enableEnderIOAppliedEnergistics = true;
    private static boolean enablePneumaticCraft = true;
    private static boolean enableBuildingGadgets = true;
    public static final boolean mmceLoaded = Loader.isModLoaded("modularmachinery");
    public static final boolean topLoaded = Loader.isModLoaded("theoneprobe");
    public static final boolean mekengLoaded = Loader.isModLoaded("mekeng");
    public static final boolean gtceuLoaded = Loader.isModLoaded("gregtech");
    public static final boolean packagedautoLoaded = Loader.isModLoaded("packagedauto");
    public static final boolean packagingproviderLoaded = Loader.isModLoaded("packagingprovider");
    public static final boolean enderioaeLoaded = Loader.isModLoaded("enderioconduitsappliedenergistics");
    public static final boolean pneumaticcraftLoaded = Loader.isModLoaded("pneumaticcraft");
    public static final boolean buildingGadgetsLoaded = Loader.isModLoaded("buildinggadgets");
    public static final boolean jeiLoaded = Loader.isModLoaded("jei");

    private static final String CEU_CONDUIT_SWAPPER = "crazypants.enderio.conduits.item.conduitswapper.ItemConduitSwapper";
    private static final String CEU_WIRELESS_HELPER = "crazypants.enderio.conduits.item.conduitswapper.ConduitSwapperWirelessHelper";
    public static final boolean enderioCEuConduitSwapperLoaded = hasClassBytes(CEU_CONDUIT_SWAPPER) && hasClassBytes(CEU_WIRELESS_HELPER);

    private AppCompatMixinDecisions() {
    }

    public static void refreshFromEnvironment() {
        enableMMCE = AppCompatConfig.enableMMCE;
        enableGTCEu = AppCompatConfig.enableGTCEu;
        enablePackagedAuto = AppCompatConfig.enablePackagedAuto;
        enablePackagingProvider = AppCompatConfig.enablePackagingProvider;
        enableEnderIOAppliedEnergistics = AppCompatConfig.enableEnderIOAppliedEnergistics;
        enablePneumaticCraft = AppCompatConfig.enablePneumaticCraft;
        enableBuildingGadgets = AppCompatConfig.enableBuildingGadgets;
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
            case "enderioae" -> enableEnderIOAppliedEnergistics && enderioaeLoaded && shouldApplyEnderIOAEMixin(mixinName);
            case "pneumaticcraft" -> enablePneumaticCraft && pneumaticcraftLoaded;
            case "buildinggadgets" -> enableBuildingGadgets && buildingGadgetsLoaded;
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

    private static boolean shouldApplyEnderIOAEMixin(final String mixinName) {
        return switch (mixinName) {
            case "enderioae.MixinItemConduitSwapper", "enderioae.MixinConduitSwapperWirelessHelper" -> enderioCEuConduitSwapperLoaded;
            default -> true;
        };
    }

    private static boolean hasClassBytes(final String className) {
        try {
            return Launch.classLoader.getClassBytes(className) != null;
        } catch (final IOException e) {
            AppliedCompatibility.LOGGER.error("Failed to inspect EnderIO class {}", className, e);
            return false;
        }
    }
}
