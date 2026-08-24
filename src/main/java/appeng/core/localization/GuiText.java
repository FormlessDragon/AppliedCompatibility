package appeng.core.localization;

import net.minecraft.util.text.TextComponentTranslation;

public enum GuiText {
    Config,
    StoredItems,
    StoredFluids,
    Unlinked,
    inventory;

    public String getLocal() {
        return this == Unlinked
            ? new TextComponentTranslation("appcompat.conduitswapper.tooltip.unlinked").getFormattedText()
            : name();
    }
}
