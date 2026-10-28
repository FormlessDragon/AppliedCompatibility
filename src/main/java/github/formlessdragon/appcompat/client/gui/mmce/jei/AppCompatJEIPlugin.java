package github.formlessdragon.appcompat.client.gui.mmce.jei;

import github.formlessdragon.appcompat.AppCompatMixinDecisions;
import github.formlessdragon.appcompat.client.gui.mmce.GuiMEItemInputBus;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import org.jspecify.annotations.NonNull;

@JEIPlugin
public class AppCompatJEIPlugin implements IModPlugin {

    @Override
    public void register(final @NonNull IModRegistry registry) {
        if (AppCompatMixinDecisions.shouldApply("mmce.MixinMEItemInputBus")) {
            for (final DynamicMachine machine : MachineRegistry.getRegistry()) {
                registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                    new AppCompatInputRecipeTransferHandler(),
                    "modularmachinery.recipes." + machine.getRegistryName().getPath());
            }
            registry.addGhostIngredientHandler(GuiMEItemInputBus.class, new AppCompatInputGhostHandler());
        }
    }
}
