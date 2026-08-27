package github.formlessdragon.appcompat.mixins.buildinggadgets;

import ae2.api.AECapabilities;
import ae2.api.networking.IInWorldGridNodeHost;
import com.direwolf20.buildinggadgets.common.integration.mods.AppliedEnergistics2;
import com.direwolf20.buildinggadgets.common.tools.NetworkIO;
import github.formlessdragon.appcompat.bridge.buildinggadgets.BuildingGadgetsNetworkBridge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AppliedEnergistics2.class, remap = false)
public abstract class MixinAppliedEnergistics2 {

    @Inject(method = "getWrappedNetworkInternal", at = @At("HEAD"), cancellable = true)
    private void appcompat$useNewAeNetwork(final TileEntity te, final EntityPlayer player,
                                           final NetworkIO.Operation operation,
                                           final CallbackInfoReturnable<IItemHandler> cir) {
        final IInWorldGridNodeHost host;
        if (te instanceof IInWorldGridNodeHost directHost) {
            host = directHost;
        } else if (te.hasCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, null)) {
            host = te.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, null);
        } else {
            return;
        }

        if (host != null) {
            cir.setReturnValue(BuildingGadgetsNetworkBridge.createHandler(te, host, player, operation));
        }
    }
}
