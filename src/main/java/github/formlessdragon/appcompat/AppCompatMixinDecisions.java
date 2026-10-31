package github.formlessdragon.appcompat;

import com.cleanroommc.discovery.CleanroomModDiscoverer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;

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
    public static final boolean moreMekaSuitModulesLoaded = isModLoaded("moremekasuitmodules");
    public static final boolean mmceLoaded = isModLoaded("modularmachinery");
    public static final boolean mmceaddonsLoaded = isModLoaded("modularmachineryaddons");
    public static final boolean thaumicEnergisticsLoaded = isModLoaded("thaumicenergistics");
    public static final boolean topLoaded = isModLoaded("theoneprobe");
    public static final boolean mekengLoaded = isModLoaded("mekeng");
    public static final boolean gtceuLoaded = isModLoaded("gregtech");
    public static final boolean packagedautoLoaded = isModLoaded("packagedauto");
    public static final boolean packagingproviderLoaded = isModLoaded("packagingprovider");
    public static final boolean enderioaeLoaded = isModLoaded("enderioconduitsappliedenergistics");
    public static final boolean pneumaticcraftLoaded = isModLoaded("pneumaticcraft");
    public static final boolean buildingGadgetsLoaded = isModLoaded("buildinggadgets");
    public static final boolean jeiLoaded = isModLoaded("jei");
    public static final boolean crTLoaded = isModLoaded("crafttweaker");
    public static final boolean recipehandlerLoaded = isModLoaded("recipehandler");

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
            case "mmceaddons" ->
                enableMMCE && enableMMCEAddons && mmceaddonsLoaded && mmceLoaded && thaumicEnergisticsLoaded;
            case "gtceu" -> enableGTCEu && gtceuLoaded;
            case "packagedauto" -> packagedautoLoaded && shouldApplyPackage(mixinName)
                && (enablePackagedAuto || mixinName.equals("packagedauto.MixinTileBase")
                && enablePackagingProvider && packagingproviderLoaded);
            case "packagingprovider" -> enablePackagingProvider && packagingproviderLoaded && packagedautoLoaded;
            case "enderioae" -> enableEnderIOAppliedEnergistics && enderioaeLoaded
                && shouldApplyEnderIOAEMixin(mixinName);
            case "pneumaticcraft" -> enablePneumaticCraft && pneumaticcraftLoaded;
            case "buildinggadgets" -> enableBuildingGadgets && buildingGadgetsLoaded;
            case "crt" -> enableCrT && crTLoaded;
            case "recipehandler" -> recipehandlerLoaded;
            case "moremekasuitmodules" -> moreMekaSuitModulesLoaded;
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

    private static boolean isModLoaded(final String modid) {
        try {
            return CleanroomModDiscoverer.instance().isModPresent(modid);
        } catch (Throwable e) {
            return Loader.isModLoaded(modid);
        }
    }
}
