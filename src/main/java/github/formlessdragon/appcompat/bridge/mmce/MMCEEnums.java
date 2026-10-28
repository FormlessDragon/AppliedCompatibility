package github.formlessdragon.appcompat.bridge.mmce;

import github.kasuminova.mmce.common.tile.MEPatternProvider.WorkModeSetting;
import hellfirepvp.modularmachinery.common.CommonProxy;
import net.minecraft.util.math.MathHelper;

/** One shared cache per external enum that does not expose its own constants array. */
public final class MMCEEnums {

    private static final CommonProxy.GuiType[] GUI_TYPES = CommonProxy.GuiType.values();
    private static final WorkModeSetting[] WORK_MODES = WorkModeSetting.values();

    private MMCEEnums() {
    }

    public static CommonProxy.GuiType guiType(final int id) {
        return GUI_TYPES[MathHelper.clamp(id, 0, GUI_TYPES.length - 1)];
    }

    public static WorkModeSetting workMode(final int ordinal) {
        return WORK_MODES[MathHelper.clamp(ordinal, 0, WORK_MODES.length - 1)];
    }

    public static WorkModeSetting nextWorkMode(final WorkModeSetting current) {
        return WORK_MODES[(current.ordinal() + 1) % WORK_MODES.length];
    }
}
