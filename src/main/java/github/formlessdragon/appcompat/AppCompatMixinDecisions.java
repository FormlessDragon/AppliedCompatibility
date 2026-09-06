package github.formlessdragon.appcompat;

import com.cleanroommc.discovery.CleanroomModDiscoverer;
import net.minecraft.launchwrapper.Launch;

import java.io.IOException;

public final class AppCompatMixinDecisions {

    private static boolean enableMMCE = true;
    private static boolean enableMMCEAddons = true;
    private static boolean enableGTCEu = true;
    private static boolean enablePackagedAuto = true;
    private static boolean enablePackagingProvider = true;
    private static boolean enableEnderIOAppliedEnergistics = true;
    private static boolean enablePneumaticCraft = true;
    private static boolean enableBuildingGadgets = true;
    private static boolean enableCrT = true;
    public static final boolean mmceLoaded = CleanroomModDiscoverer.instance().isModPresent("modularmachinery");
    public static final boolean mmceaddonsLoaded = CleanroomModDiscoverer.instance().isModPresent("modularmachineryaddons");
    public static final boolean topLoaded = CleanroomModDiscoverer.instance().isModPresent("theoneprobe");
    public static final boolean mekengLoaded = CleanroomModDiscoverer.instance().isModPresent("mekeng");
    public static final boolean gtceuLoaded = CleanroomModDiscoverer.instance().isModPresent("gregtech");
    public static final boolean packagedautoLoaded = CleanroomModDiscoverer.instance().isModPresent("packagedauto");
    public static final boolean packagingproviderLoaded = CleanroomModDiscoverer.instance().isModPresent("packagingprovider");
    public static final boolean enderioaeLoaded = CleanroomModDiscoverer.instance().isModPresent("enderioconduitsappliedenergistics");
    public static final boolean pneumaticcraftLoaded = CleanroomModDiscoverer.instance().isModPresent("pneumaticcraft");
    public static final boolean buildingGadgetsLoaded = CleanroomModDiscoverer.instance().isModPresent("buildinggadgets");
    public static final boolean jeiLoaded = CleanroomModDiscoverer.instance().isModPresent("jei");
    public static final boolean crTLoaded = CleanroomModDiscoverer.instance().isModPresent("crafttweaker");
    public static final boolean recipehandlerLoaded = CleanroomModDiscoverer.instance().isModPresent("recipehandler");

    private static final String CEU_CONDUIT_SWAPPER = "crazypants.enderio.conduits.item.conduitswapper.ItemConduitSwapper";
    private static final String CEU_WIRELESS_HELPER = "crazypants.enderio.conduits.item.conduitswapper.ConduitSwapperWirelessHelper";

    private AppCompatMixinDecisions() {
    }

    public static void refreshFromEnvironment() {
        enableMMCE = AppCompatConfig.enableMMCE;
        enableMMCEAddons = AppCompatConfig.enableMMCEAddons;
        enableGTCEu = AppCompatConfig.enableGTCEu;
        enablePackagedAuto = AppCompatConfig.enablePackagedAuto;
        enablePackagingProvider = AppCompatConfig.enablePackagingProvider;
        enableEnderIOAppliedEnergistics = AppCompatConfig.enableEnderIOAppliedEnergistics;
        enablePneumaticCraft = AppCompatConfig.enablePneumaticCraft;
        enableBuildingGadgets = AppCompatConfig.enableBuildingGadgets;
        enableCrT = AppCompatConfig.enableCrT;
    }

    public static boolean shouldApply(final String mixinName) {
        final int split = mixinName.indexOf('.');
        if (split < 0) {
            return true;
        }

        final String group = mixinName.substring(0, split);

        return switch (group) {
            case "mmce" -> enableMMCE && mmceLoaded && shouldApplyMMCE(mixinName);
            case "mmceaddons" -> enableMMCEAddons && mmceaddonsLoaded && mmceLoaded;
            case "gtceu" -> enableGTCEu && gtceuLoaded;
            case "packagedauto" -> enablePackagedAuto && packagedautoLoaded && shouldApplyPackage(mixinName);
            case "packagingprovider" -> enablePackagingProvider && packagingproviderLoaded;
            case "enderioae" -> enableEnderIOAppliedEnergistics && enderioaeLoaded
                && shouldApplyEnderIOAEMixin(mixinName);
            case "pneumaticcraft" -> enablePneumaticCraft && pneumaticcraftLoaded;
            case "buildinggadgets" -> enableBuildingGadgets && buildingGadgetsLoaded;
            case "crt" -> enableCrT && crTLoaded;
            case "recipehandler" -> recipehandlerLoaded;
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
            case "enderioae.MixinItemConduitSwapper", "enderioae.MixinConduitSwapperWirelessHelper" ->
                enderioaeLoaded && hasClassBytes(CEU_CONDUIT_SWAPPER)
                    && hasClassBytes(CEU_WIRELESS_HELPER);
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
