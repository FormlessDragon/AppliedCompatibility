package github.formlessdragon.appcompat.mixins.enderioae;

import ae2.api.AECapabilities;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOInWorldGridNodeHostAdapter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileConduitBundle.class, remap = false)
public abstract class MixinTileConduitBundle extends TileEntityEio {

    @Unique
    private EnderIOInWorldGridNodeHostAdapter appcompat$aeHostAdapter;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void appcompat$registerAeCapability(final CallbackInfo ci) {
        this.addICap(AECapabilities.IN_WORLD_GRID_NODE_HOST, facing -> {
            if (this.appcompat$aeHostAdapter == null) {
                this.appcompat$aeHostAdapter = new EnderIOInWorldGridNodeHostAdapter((TileConduitBundle) (Object) this);
            }
            return this.appcompat$aeHostAdapter;
        });
    }
}
