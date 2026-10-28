package github.formlessdragon.appcompat.mixins.mmce;

import github.formlessdragon.appcompat.bridge.mmce.MMCEEnums;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEFluidInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEFluidOutputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemInputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemOutputBus;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEItemOutputBusStackSize;
import github.formlessdragon.appcompat.common.container.mmce.ContainerMEPatternProvider;
import github.kasuminova.mmce.common.tile.MEFluidInputBus;
import github.kasuminova.mmce.common.tile.MEFluidOutputBus;
import github.kasuminova.mmce.common.tile.MEItemInputBus;
import github.kasuminova.mmce.common.tile.MEItemOutputBus;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
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
            case ME_ITEM_INPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemInputBus bus) {
                    cir.setReturnValue(new ContainerMEItemInputBus(bus, player));
                }
            }
            case ME_ITEM_OUTPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemOutputBus bus) {
                    cir.setReturnValue(new ContainerMEItemOutputBus(bus, player));
                }
            }
            case ME_ITEM_OUTPUT_BUS_STACK_SIZE -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemOutputBus bus) {
                    cir.setReturnValue(new ContainerMEItemOutputBusStackSize(player.inventory, bus));
                }
            }
            case ME_FLUID_INPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEFluidInputBus bus) {
                    cir.setReturnValue(new ContainerMEFluidInputBus(bus, player));
                }
            }
            case ME_FLUID_OUTPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEFluidOutputBus bus) {
                    cir.setReturnValue(new ContainerMEFluidOutputBus(bus, player));
                }
            }
            case ME_PATTERN_PROVIDER -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEPatternProvider provider) {
                    cir.setReturnValue(new ContainerMEPatternProvider(provider, player));
                }
            }
            default -> {
            }
        }
    }
}
