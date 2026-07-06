package appeng.core.api.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IParts;
import appeng.api.util.AEColoredItemDefinition;
import appeng.core.features.ColoredItemDefinition;
import appeng.core.features.ItemDefinition;
import ae2.core.definitions.AEParts;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;

public final class ApiParts implements IParts {

    private final AEColoredItemDefinition cableSmart = colored(AEParts.SMART_CABLE);
    private final AEColoredItemDefinition cableCovered = colored(AEParts.COVERED_CABLE);
    private final AEColoredItemDefinition cableGlass = colored(AEParts.GLASS_CABLE);
    private final AEColoredItemDefinition cableDenseCovered = colored(AEParts.COVERED_DENSE_CABLE);
    private final AEColoredItemDefinition cableDenseSmart = colored(AEParts.SMART_DENSE_CABLE);
    private final AEColoredItemDefinition lumenCableSmart = disabledColored();
    private final AEColoredItemDefinition lumenCableCovered = disabledColored();
    private final AEColoredItemDefinition lumenCableGlass = disabledColored();
    private final AEColoredItemDefinition lumenDenseCableSmart = disabledColored();
    private final IItemDefinition quartzFiber = item("part.quartz_fiber", AEParts.QUARTZ_FIBER);
    private final IItemDefinition toggleBus = item("part.toggle_bus", AEParts.TOGGLE_BUS);
    private final IItemDefinition invertedToggleBus = item("part.inverted_toggle_bus", AEParts.INVERTED_TOGGLE_BUS);
    private final IItemDefinition storageBus = item("part.storage_bus", AEParts.STORAGE_BUS);
    private final IItemDefinition oreDictStorageBus = item("part.storage_bus.oredict", AEParts.OD_STORAGE_BUS);
    private final IItemDefinition importBus = item("part.import_bus", AEParts.IMPORT_BUS);
    private final IItemDefinition exportBus = item("part.export_bus", AEParts.EXPORT_BUS);
    private final IItemDefinition iface = item("part.interface", AEParts.INTERFACE);
    private final IItemDefinition fluidIface = item("part.interface.fluid", AEParts.INTERFACE);
    private final IItemDefinition levelEmitter = item("part.level_emitter", AEParts.LEVEL_EMITTER);
    private final IItemDefinition annihilationPlane = item("part.annihilation_plane", AEParts.ANNIHILATION_PLANE);
    private final IItemDefinition identityAnnihilationPlane = legacyPart("part.annihilation_plane.identity", 301);
    private final IItemDefinition formationPlane = item("part.formation_plane", AEParts.FORMATION_PLANE);
    private final IItemDefinition p2PTunnelME = item("part.p2p_tunnel.me", AEParts.ME_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelRedstone = item("part.p2p_tunnel.redstone", AEParts.REDSTONE_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelItems = item("part.p2p_tunnel.item", AEParts.ITEM_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelFluids = item("part.p2p_tunnel.fluid", AEParts.FLUID_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelEU = item("part.p2p_tunnel.eu", AEParts.IC2_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelFE = item("part.p2p_tunnel.fe", AEParts.FE_P2P_TUNNEL);
    private final IItemDefinition p2PTunnelGTEU = legacyPart("part.p2p_tunnel.gteu", 470);
    private final IItemDefinition p2PTunnelLight = item("part.p2p_tunnel.light", AEParts.LIGHT_P2P_TUNNEL);
    private final IItemDefinition cableAnchor = item("part.cable_anchor", AEParts.CABLE_ANCHOR);
    private final IItemDefinition monitor = item("part.monitor", AEParts.MONITOR);
    private final IItemDefinition semiDarkMonitor = item("part.monitor.semi_dark", AEParts.SEMI_DARK_MONITOR);
    private final IItemDefinition darkMonitor = item("part.monitor.dark", AEParts.DARK_MONITOR);
    private final IItemDefinition interfaceTerminal = item("part.interface_terminal", AEParts.PATTERN_ACCESS_TERMINAL);
    private final IItemDefinition patternTerminal = item("part.pattern_terminal", AEParts.PATTERN_ENCODING_TERMINAL);
    private final IItemDefinition expandedProcessingPatternTerminal = item(
        "part.pattern_terminal.expanded_processing", AEParts.PATTERN_ENCODING_TERMINAL);
    private final IItemDefinition interfaceConfigurationTerminal = item(
        "part.interface_configuration_terminal", AEParts.PATTERN_ACCESS_TERMINAL);
    private final IItemDefinition fluidInterfaceConfigurationTerminal = item(
        "part.interface_configuration_terminal.fluid", AEParts.PATTERN_ACCESS_TERMINAL);
    private final IItemDefinition craftingTerminal = item("part.crafting_terminal", AEParts.CRAFTING_TERMINAL);
    private final IItemDefinition terminal = item("part.terminal", AEParts.TERMINAL);
    private final IItemDefinition storageMonitor = item("part.storage_monitor", AEParts.STORAGE_MONITOR);
    private final IItemDefinition conversionMonitor = item("part.conversion_monitor", AEParts.CONVERSION_MONITOR);
    private final IItemDefinition fluidTerminal = item("part.terminal.fluid", AEParts.TERMINAL);
    private final IItemDefinition fluidImportBus = item("part.import_bus.fluid", AEParts.IMPORT_BUS);
    private final IItemDefinition fluidExportBus = item("part.export_bus.fluid", AEParts.EXPORT_BUS);
    private final IItemDefinition fluidStorageBus = item("part.storage_bus.fluid", AEParts.STORAGE_BUS);
    private final IItemDefinition fluidLevelEmitter = item("part.level_emitter.fluid", AEParts.LEVEL_EMITTER);
    private final IItemDefinition fluidAnnihilationPlane = item(
        "part.annihilation_plane.fluid", AEParts.ANNIHILATION_PLANE);
    private final IItemDefinition fluidFormationnPlane = item("part.formation_plane.fluid", AEParts.FORMATION_PLANE);

    @Override
    public AEColoredItemDefinition cableSmart() { return this.cableSmart; }
    @Override
    public AEColoredItemDefinition cableCovered() { return this.cableCovered; }
    @Override
    public AEColoredItemDefinition cableGlass() { return this.cableGlass; }
    @Override
    public AEColoredItemDefinition cableDenseCovered() { return this.cableDenseCovered; }
    @Override
    public AEColoredItemDefinition cableDenseSmart() { return this.cableDenseSmart; }
    @Override
    public AEColoredItemDefinition lumenCableSmart() { return this.lumenCableSmart; }
    @Override
    public AEColoredItemDefinition lumenCableCovered() { return this.lumenCableCovered; }
    @Override
    public AEColoredItemDefinition lumenCableGlass() { return this.lumenCableGlass; }
    @Override
    public AEColoredItemDefinition lumenDenseCableSmart() { return this.lumenDenseCableSmart; }
    @Override
    public IItemDefinition quartzFiber() { return this.quartzFiber; }
    @Override
    public IItemDefinition toggleBus() { return this.toggleBus; }
    @Override
    public IItemDefinition invertedToggleBus() { return this.invertedToggleBus; }
    @Override
    public IItemDefinition storageBus() { return this.storageBus; }
    @Override
    public IItemDefinition oreDictStorageBus() { return this.oreDictStorageBus; }
    @Override
    public IItemDefinition importBus() { return this.importBus; }
    @Override
    public IItemDefinition exportBus() { return this.exportBus; }
    @Override
    public IItemDefinition iface() { return this.iface; }
    @Override
    public IItemDefinition fluidIface() { return this.fluidIface; }
    @Override
    public IItemDefinition levelEmitter() { return this.levelEmitter; }
    @Override
    public IItemDefinition annihilationPlane() { return this.annihilationPlane; }
    @Override
    public IItemDefinition identityAnnihilationPlane() { return this.identityAnnihilationPlane; }
    @Override
    public IItemDefinition formationPlane() { return this.formationPlane; }
    @Override
    public IItemDefinition p2PTunnelME() { return this.p2PTunnelME; }
    @Override
    public IItemDefinition p2PTunnelRedstone() { return this.p2PTunnelRedstone; }
    @Override
    public IItemDefinition p2PTunnelItems() { return this.p2PTunnelItems; }
    @Override
    public IItemDefinition p2PTunnelFluids() { return this.p2PTunnelFluids; }
    @Override
    public IItemDefinition p2PTunnelEU() { return this.p2PTunnelEU; }
    @Override
    public IItemDefinition p2PTunnelFE() { return this.p2PTunnelFE; }
    @Override
    public IItemDefinition p2PTunnelGTEU() { return this.p2PTunnelGTEU; }
    @Override
    public IItemDefinition p2PTunnelLight() { return this.p2PTunnelLight; }
    @Override
    public IItemDefinition cableAnchor() { return this.cableAnchor; }
    @Override
    public IItemDefinition monitor() { return this.monitor; }
    @Override
    public IItemDefinition semiDarkMonitor() { return this.semiDarkMonitor; }
    @Override
    public IItemDefinition darkMonitor() { return this.darkMonitor; }
    @Override
    public IItemDefinition interfaceTerminal() { return this.interfaceTerminal; }
    @Override
    public IItemDefinition patternTerminal() { return this.patternTerminal; }
    @Override
    public IItemDefinition expandedProcessingPatternTerminal() { return this.expandedProcessingPatternTerminal; }
    @Override
    public IItemDefinition interfaceConfigurationTerminal() { return this.interfaceConfigurationTerminal; }
    @Override
    public IItemDefinition fluidInterfaceConfigurationTerminal() { return this.fluidInterfaceConfigurationTerminal; }
    @Override
    public IItemDefinition craftingTerminal() { return this.craftingTerminal; }
    @Override
    public IItemDefinition terminal() { return this.terminal; }
    @Override
    public IItemDefinition storageMonitor() { return this.storageMonitor; }
    @Override
    public IItemDefinition conversionMonitor() { return this.conversionMonitor; }
    @Override
    public IItemDefinition fluidTerminal() { return this.fluidTerminal; }
    @Override
    public IItemDefinition fluidImportBus() { return this.fluidImportBus; }
    @Override
    public IItemDefinition fluidExportBus() { return this.fluidExportBus; }
    @Override
    public IItemDefinition fluidStorageBus() { return this.fluidStorageBus; }
    @Override
    public IItemDefinition fluidLevelEmitter() { return this.fluidLevelEmitter; }
    @Override
    public IItemDefinition fluidAnnihilationPlane() { return this.fluidAnnihilationPlane; }
    @Override
    public IItemDefinition fluidFormationnPlane() { return this.fluidFormationnPlane; }

    private static AEColoredItemDefinition colored(final ae2.core.definitions.ColoredItemDefinition<?> definition) {
        return new ColoredItemDefinition(definition);
    }

    private static AEColoredItemDefinition disabledColored() {
        return new ColoredItemDefinition(null);
    }

    private static IItemDefinition item(final String identifier,
                                        final ae2.core.definitions.ItemDefinition<?> definition) {
        return new ItemDefinition(identifier, definition);
    }

    private static IItemDefinition legacyPart(final String identifier, final int meta) {
        return LegacyAeItemMappings.itemDefinition(identifier, "part", meta);
    }
}
