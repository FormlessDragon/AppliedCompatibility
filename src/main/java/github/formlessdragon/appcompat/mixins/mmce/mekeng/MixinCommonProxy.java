package github.formlessdragon.appcompat.mixins.mmce.mekeng;

import github.formlessdragon.appcompat.bridge.mmce.MMCEEnums;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEGasInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEGasOutputBus;
import github.kasuminova.mmce.common.tile.MEGasInputBus;
import github.kasuminova.mmce.common.tile.MEGasOutputBus;
import hellfirepvp.modularmachinery.common.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommonProxy.class, remap = false)
public abstract class MixinCommonProxy {

    @Inject(method = "getServerGuiElement", at = @At("HEAD"), cancellable = true)
    private void appcompat$getServerGuiElement(final int ID, final EntityPlayer player, final World world,
                                               final int x, final int y, final int z,
                                               final CallbackInfoReturnable<Object> cir) {
        final CommonProxy.GuiType type = MMCEEnums.guiType(ID);
        if (type.requiredTileEntity == null) return;

        switch (type) {
            case ME_GAS_INPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEGasInputBus bus) {
                    cir.setReturnValue(new ContainerMEGasInputBus(bus, player));
                }
            }
            case ME_GAS_OUTPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEGasOutputBus bus) {
                    cir.setReturnValue(new ContainerMEGasOutputBus(bus, player));
                }
            }
            default -> {
            }
        }
    }
}
