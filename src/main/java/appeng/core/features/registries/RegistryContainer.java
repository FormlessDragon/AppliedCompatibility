package appeng.core.features.registries;

import appeng.api.features.IChargerRegistry;
import appeng.api.features.IGrinderRegistry;
import appeng.api.features.IInscriberRegistry;
import appeng.api.features.ILocatableRegistry;
import appeng.api.features.IMatterCannonAmmoRegistry;
import appeng.api.features.IP2PTunnelRegistry;
import appeng.api.features.IPlayerRegistry;
import appeng.api.features.IRecipeHandlerRegistry;
import appeng.api.features.IRegistryContainer;
import appeng.api.features.ISpecialComparisonRegistry;
import appeng.api.features.IWirelessTermRegistry;
import appeng.api.features.IWorldGen;
import appeng.api.movable.IMovableRegistry;
import appeng.api.networking.IGridCacheRegistry;
import appeng.api.parts.IPartModels;
import appeng.api.storage.ICellRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class RegistryContainer implements IRegistryContainer {

    private final IPlayerRegistry players = new PlayerRegistry();
    private final UnsupportedRegistries unsupported = new UnsupportedRegistries();

    @Override
    public IMovableRegistry movable() {
        return this.unsupported;
    }

    @Override
    public IGridCacheRegistry gridCache() {
        return this.unsupported;
    }

    @Override
    public ISpecialComparisonRegistry specialComparison() {
        return this.unsupported;
    }

    @Override
    public IWirelessTermRegistry wireless() {
        return this.unsupported;
    }

    @Override
    public ICellRegistry cell() {
        return this.unsupported;
    }

    @Override
    public IGrinderRegistry grinder() {
        return this.unsupported;
    }

    @Override
    public IInscriberRegistry inscriber() {
        return this.unsupported;
    }

    @Override
    public IChargerRegistry charger() {
        return this.unsupported;
    }

    @Override
    public ILocatableRegistry locatable() {
        return this.unsupported;
    }

    @Override
    public IP2PTunnelRegistry p2pTunnel() {
        return this.unsupported;
    }

    @Override
    public IMatterCannonAmmoRegistry matterCannon() {
        return this.unsupported;
    }

    @Override
    public IPlayerRegistry players() {
        return this.players;
    }

    @Override
    public IRecipeHandlerRegistry recipes() {
        return this.unsupported;
    }

    @Override
    public IWorldGen worldgen() {
        return this.unsupported;
    }

    @Override
    public IPartModels partModels() {
        return this.unsupported;
    }

    private static final class PlayerRegistry implements IPlayerRegistry {

        @Override
        public int getID(final GameProfile profile) {
            if (profile == null || profile.getId() == null) {
                return -1;
            }
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) {
                throw new IllegalStateException("Cannot resolve AE player id without a running MinecraftServer");
            }
            return ae2.api.features.IPlayerRegistry.getMapping(server).getPlayerId(profile);
        }

        @Override
        public int getID(final EntityPlayer player) {
            if (player == null) {
                return -1;
            }
            return getID(player.getGameProfile());
        }

        @Override
        public EntityPlayer findPlayer(final int playerID) {
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) {
                throw new IllegalStateException("Cannot resolve AE player without a running MinecraftServer");
            }
            final EntityPlayerMP player = ae2.api.features.IPlayerRegistry.getConnected(server, playerID);
            return player;
        }
    }

    private static final class UnsupportedRegistries implements IMovableRegistry, IGridCacheRegistry,
        ISpecialComparisonRegistry, IWirelessTermRegistry, ICellRegistry, IGrinderRegistry, IInscriberRegistry,
        IChargerRegistry, ILocatableRegistry, IP2PTunnelRegistry, IMatterCannonAmmoRegistry,
        IRecipeHandlerRegistry, IWorldGen, IPartModels {
    }
}
