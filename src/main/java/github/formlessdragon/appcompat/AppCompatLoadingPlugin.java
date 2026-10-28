package github.formlessdragon.appcompat;

import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IMixinConfigHijacker;

import java.util.Map;
import java.util.Set;

public class AppCompatLoadingPlugin implements IFMLLoadingPlugin, IMixinConfigHijacker {

    @Override
    public Set<String> getHijackedMixinConfigs() {
        return ObjectSets.singleton("mixins.moremekasuitmodules_appliedenergistics2.json");
    }

    @Override
    public @Nullable String[] getASMTransformerClass() {
        return new String[]{"github.formlessdragon.appcompat.transformers.JechCallHookTransformer"};
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }
}
