package extracells.registries;

import extracells.integration.Integration;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public enum BlockEnum {
    CERTUSTANK("certustank"),
    WALRUS("walrus"),
    FLUIDCRAFTER("fluidcrafter"),
    ECBASEBLOCK("ecbaseblock"),
    FILLER("fluidfiller"),
    BLASTRESISTANTMEDRIVE("hardmedrive"),
    VIBRANTCHAMBERFLUID("vibrantchamberfluid");

    private final String internalName;

    BlockEnum(final String internalName) {
        this.internalName = internalName;
    }

    public Block getBlock() {
        final ItemStack stack = LegacyAeItemMappings.stack("extracells:" + this.internalName, 0, 1);
        if (stack.getItem() instanceof ItemBlock itemBlock) {
            return itemBlock.getBlock();
        }
        return net.minecraft.init.Blocks.BARRIER;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public ItemBlock getItem() {
        final Item item = Item.getItemFromBlock(this.getBlock());
        if (item instanceof ItemBlock itemBlock) {
            return itemBlock;
        }
        return null;
    }

    public String getStatName() {
        return I18n.translateToLocal("tile.extracells.block." + this.internalName + ".name");
    }

    public Integration.Mods getMod() {
        return null;
    }
}
