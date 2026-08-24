package github.formlessdragon.appcompat.mixins.enderioae;

import com.enderio.core.common.util.stackable.Things;
import github.formlessdragon.appcompat.AppCompatConfig;
import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import github.formlessdragon.appcompat.bridge.enderioae.EnderIOThingQueue;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(value = Things.class, remap = false)
public abstract class MixinThings {

    @Shadow @Nonnull public abstract Things add(@Nullable ItemStack itemStack);

    @Shadow @Nonnull public abstract Things add(@Nullable String name);

    @Inject(method = "add(Ljava/lang/String;)Lcom/enderio/core/common/util/stackable/Things;", at = @At("HEAD"),
        cancellable = true)
    private void appcompat$remapLegacyAeThing(final String name, final CallbackInfoReturnable<Things> cir) {
        if (!AppCompatConfig.enableLegacyAeItemIdRemapping || name == null) {
            return;
        }
        final String trimmed = name.trim();
        if (trimmed.isEmpty() || trimmed.charAt(0) == '+' || trimmed.charAt(0) == '-') {
            return;
        }
        if (LegacyAeItemMappings.isInitialized()) {
            if (LegacyAeItemMappings.isLegacySpec(trimmed)) {
                Things thisThings = (Things) (Object) this;
                EnderIOThingQueue.enqueue(thisThings, trimmed);
                cir.setReturnValue(thisThings);
            }
            return;
        }

        if (trimmed.indexOf(',') >= 0) {
            if (appcompat$addCompound(trimmed)) {
                cir.setReturnValue((Things) (Object) this);
            }
            return;
        }

        if (!LegacyAeItemMappings.isLegacySpec(trimmed)) {
            return;
        }

        final ItemStack mapped = LegacyAeItemMappings.mappedSpecStackOrNull(trimmed, 1);
        if (mapped == null) {
            return;
        }
        this.add(mapped);
        cir.setReturnValue((Things) (Object) this);
    }

    @Unique
    private boolean appcompat$addCompound(final String name) {
        boolean hasMapped = false;
        int start = 0;
        final int length = name.length();
        for (int i = 0; i <= length; i++) {
            if (i != length && name.charAt(i) != ',') {
                continue;
            }
            final String part = name.substring(start, i).trim();
            start = i + 1;
            if (part.isEmpty()) {
                continue;
            }
            if (part.charAt(0) == '+' || part.charAt(0) == '-') {
                return false;
            }
            if (LegacyAeItemMappings.mappedSpecStackOrNull(part, 1) != null) {
                hasMapped = true;
            }
        }
        if (!hasMapped) {
            return false;
        }

        start = 0;
        for (int i = 0; i <= length; i++) {
            if (i != length && name.charAt(i) != ',') {
                continue;
            }
            final String part = name.substring(start, i).trim();
            start = i + 1;
            if (!part.isEmpty()) {
                this.add(part);
            }
        }
        return true;
    }
}
