package github.formlessdragon.appcompat;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AppCompatLoadingPlugin implements IFMLLoadingPlugin {
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
