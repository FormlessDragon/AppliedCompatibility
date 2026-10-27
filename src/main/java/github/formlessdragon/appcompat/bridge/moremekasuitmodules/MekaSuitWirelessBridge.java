package github.formlessdragon.appcompat.bridge.moremekasuitmodules;

import ae2.api.config.Actionable;
import ae2.api.features.HotkeyAction;
import ae2.container.AEBaseContainer;
import ae2.container.GuiIds;
import ae2.container.me.common.ContainerMEStorage;
import ae2.container.me.items.ContainerCraftingTerm;
import ae2.container.me.patternencode.ContainerPatternEncodingTerm;
import ae2.core.gui.GuiOpener;
import ae2.core.gui.locator.GuiHostLocators;
import ae2.core.gui.locator.ItemGuiHostLocator;
import ae2.core.localization.GuiText;
import ae2.items.tools.powered.WirelessTerminals;
import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import github.formlessdragon.appcompat.bridge.ae.LegacyWirelessTerminalHost;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.IModule;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public final class MekaSuitWirelessBridge {

    private static boolean initialized;

    private MekaSuitWirelessBridge() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        HotkeyAction.register(player -> open(player, ModuleSmartWirelessUnit.WirelessTerminal.WIRELESS_TERMINAL),
            HotkeyAction.WIRELESS_TERMINAL);
        HotkeyAction.register(player -> open(player, ModuleSmartWirelessUnit.WirelessTerminal.WIRELESS_CRAFTING_TERMINAL),
            "wireless_crafting_terminal");
        HotkeyAction.register(player -> open(player, ModuleSmartWirelessUnit.WirelessTerminal.WIRELESS_PATTERN_TERMINAL),
            "wireless_pattern_encoding_terminal");
        HotkeyAction.register(player -> open(player, ModuleSmartWirelessUnit.WirelessTerminal.WIRELESS_FLUID_TERMINAL),
            "wireless_fluid_terminal");
    }

    private static IModule<ModuleSmartWirelessUnit> module(final ItemStack stack) {
        if (stack.getItem() instanceof IModuleContainerItem item) {
            return item.getModule(stack, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
        }
        return null;
    }

    public static LegacyWirelessTerminalHost createHost(final EntityPlayer player, final ItemGuiHostLocator locator,
                                                       final GuiIds.GuiKey requested) {
        final ItemStack stack = locator.locateItem(player);
        final IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(stack);
        final IModule<ModuleSmartWirelessUnit> module = module(stack);
        if (handler == null || module == null || !module.isEnabled()) {
            return null;
        }
        final GuiIds.GuiKey key = requested == null ? guiKey(module.getCustomInstance().getMode()) : requested;
        return new LegacyWirelessTerminalHost(stack.getItem(), player, locator, handler, key);
    }

    private static GuiIds.GuiKey guiKey(final ModuleSmartWirelessUnit.WirelessTerminal mode) {
        return switch (mode) {
            case WIRELESS_CRAFTING_TERMINAL -> GuiIds.GuiKey.WIRELESS_CRAFTING_TERMINAL;
            case WIRELESS_PATTERN_TERMINAL -> GuiIds.GuiKey.WIRELESS_PATTERN_ENCODING_TERMINAL;
            default -> GuiIds.GuiKey.WIRELESS_TERMINAL;
        };
    }

    private static boolean open(final EntityPlayerMP player, final ModuleSmartWirelessUnit.WirelessTerminal requested) {
        final ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        final IModule<ModuleSmartWirelessUnit> module = module(stack);
        if (module == null || !module.isEnabled()) {
            return false;
        }
        final ModuleSmartWirelessUnit custom = module.getCustomInstance();
        if (custom.getMode() != requested) {
            if (custom.getMatching()) {
                return false;
            }
            custom.changeMode(requested.ordinal());
            player.inventory.markDirty();
        }
        final ItemGuiHostLocator locator = GuiHostLocators.forInventorySlot(39);
        final LegacyWirelessTerminalHost host = createHost(player, locator, guiKey(requested));
        if (host == null) {
            return false;
        }
        if (!host.getLinkStatus().connected()) {
            final ITextComponent error = host.getLinkStatus().statusDescription();
            if (error != null) {
                player.sendStatusMessage(error, false);
            }
            return false;
        }
        return GuiOpener.openItemGui(player, guiKey(requested), locator);
    }

    public static boolean handlesGui(final int id, final EntityPlayer player, final int slot) {
        final GuiIds.GuiKey key = GuiIds.GuiKey.fromId(id);
        if (key != GuiIds.GuiKey.WIRELESS_TERMINAL && key != GuiIds.GuiKey.WIRELESS_CRAFTING_TERMINAL
            && key != GuiIds.GuiKey.WIRELESS_PATTERN_ENCODING_TERMINAL) {
            return false;
        }
        return slot == 39 && module(player.inventory.getStackInSlot(slot)) != null;
    }

    public static AEBaseContainer createContainer(final int id, final EntityPlayer player, final int slot) {
        final GuiIds.GuiKey key = GuiIds.GuiKey.fromId(id);
        final ItemGuiHostLocator locator = GuiHostLocators.forInventorySlot(slot);
        final LegacyWirelessTerminalHost host = createHost(player, locator, key);
        if (host == null || !host.isValid()) {
            return null;
        }
        final AEBaseContainer container = switch (key) {
            case WIRELESS_CRAFTING_TERMINAL -> new ContainerCraftingTerm(key, player.inventory, host, true) {

            };
            case WIRELESS_PATTERN_ENCODING_TERMINAL -> new ContainerPatternEncodingTerm(key, player.inventory, host, true);
            default -> new ContainerMEStorage(key, player.inventory, host);
        };
        container.setLocator(locator);
        container.setReturnedFromSubScreen(GuiIds.isReturnedFromSubScreen(id));
        return container;
    }

    public static double extractPower(final EntityPlayer player, final ItemStack stack, final double amount,
                                      final Actionable mode) {
        if (!(amount > 0) || !Double.isFinite(amount) || player.world.isRemote
            || player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != stack
            || !(stack.getItem() instanceof IEnergizedItem energy)) {
            return 0;
        }
        final IModule<ModuleSmartWirelessUnit> module = module(stack);
        if (module == null || !module.isEnabled()) {
            return 0;
        }
        if (player.capabilities.isCreativeMode) {
            return amount;
        }
        final double available = energy.getEnergy(stack);
        final double extracted = Math.clamp(available, 0, amount);
        if (mode == Actionable.MODULATE && extracted > 0) {
            energy.setEnergy(stack, available - extracted);
            player.inventory.markDirty();
        }
        return extracted;
    }

    public static boolean hasPower(final EntityPlayer player, final ItemStack stack, final double amount) {
        if (amount < 0 || !Double.isFinite(amount) || player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != stack
            || !(stack.getItem() instanceof IEnergizedItem energy)) {
            return false;
        }
        final IModule<ModuleSmartWirelessUnit> module = module(stack);
        return module != null && module.isEnabled() && (player.capabilities.isCreativeMode
            || (amount == 0 ? energy.getEnergy(stack) > 0 : energy.getEnergy(stack) >= amount));
    }

    public static void addTooltip(final ItemStack stack, final List<String> tooltip) {
        final IModule<ModuleSmartWirelessUnit> module = module(stack);
        if (module != null && module.isEnabled()) {
            final boolean linked = stack.hasTagCompound() && stack.getTagCompound().hasKey(WirelessTerminals.TAG_LINK, 10);
            tooltip.add((linked ? GuiText.Linked : GuiText.Unlinked).text().getFormattedText());
        }
    }
}
