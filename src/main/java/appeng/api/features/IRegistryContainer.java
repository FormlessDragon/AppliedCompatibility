package appeng.api.features;

import appeng.api.movable.IMovableRegistry;
import appeng.api.networking.IGridCacheRegistry;
import appeng.api.parts.IPartModels;
import appeng.api.storage.ICellRegistry;

public interface IRegistryContainer {

    IMovableRegistry movable();

    IGridCacheRegistry gridCache();

    ISpecialComparisonRegistry specialComparison();

    IWirelessTermRegistry wireless();

    ICellRegistry cell();

    IGrinderRegistry grinder();

    IInscriberRegistry inscriber();

    IChargerRegistry charger();

    ILocatableRegistry locatable();

    IP2PTunnelRegistry p2pTunnel();

    IMatterCannonAmmoRegistry matterCannon();

    IPlayerRegistry players();

    IRecipeHandlerRegistry recipes();

    IWorldGen worldgen();

    IPartModels partModels();
}
