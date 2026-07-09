package appeng.api.features;

import net.minecraft.item.Item;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface IChargerRegistry {

    @Nonnegative
    double getChargeRate(@Nonnull Item item);

    void addChargeRate(@Nonnull Item item, @Nonnegative double chargeRate);

    void removeChargeRate(@Nonnull Item item);
}
