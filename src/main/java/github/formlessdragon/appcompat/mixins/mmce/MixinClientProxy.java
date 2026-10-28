package github.formlessdragon.appcompat.mixins.mmce;

import github.formlessdragon.appcompat.bridge.mmce.MMCEEnums;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEFluidInputBus;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEFluidOutputBus;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEItemInputBus;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEItemOutputBus;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEItemOutputBusStackSize;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEPatternProvider;
import github.kasuminova.mmce.common.tile.MEFluidInputBus;
import github.kasuminova.mmce.common.tile.MEFluidOutputBus;
import github.kasuminova.mmce.common.tile.MEItemInputBus;
import github.kasuminova.mmce.common.tile.MEItemOutputBus;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import hellfirepvp.modularmachinery.client.ClientProxy;
import hellfirepvp.modularmachinery.common.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinClientProxy {

    @Inject(method = "getClientGuiElement", at = @At("HEAD"), cancellable = true)
    private void appcompat$getClientGuiElement(final int ID, final EntityPlayer player, final World world,
                                               final int x, final int y, final int z,
                                               final CallbackInfoReturnable<Object> cir) {
        final CommonProxy.GuiType type = MMCEEnums.guiType(ID);
        if (type.requiredTileEntity == null) return;

        switch (type) {
            case ME_ITEM_INPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemInputBus bus) {
                    cir.setReturnValue(new GuiMEItemInputBus(bus, player));
                }
            }
            case ME_ITEM_OUTPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemOutputBus bus) {
                    cir.setReturnValue(new GuiMEItemOutputBus(bus, player));
                }
            }
            case ME_ITEM_OUTPUT_BUS_STACK_SIZE -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEItemOutputBus bus) {
                    cir.setReturnValue(new GuiMEItemOutputBusStackSize(bus, player));
                }
            }
            case ME_FLUID_INPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEFluidInputBus bus) {
                    cir.setReturnValue(new GuiMEFluidInputBus(bus, player));
                }
            }
            case ME_FLUID_OUTPUT_BUS -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEFluidOutputBus bus) {
                    cir.setReturnValue(new GuiMEFluidOutputBus(bus, player));
                }
            }
            case ME_PATTERN_PROVIDER -> {
                final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof MEPatternProvider provider) {
                    cir.setReturnValue(new GuiMEPatternProvider(provider, player));
                }
            }
            default -> {
            }
        }
    }
}
