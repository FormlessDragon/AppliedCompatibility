package appeng.api.parts;

import appeng.api.util.AEPartLocation;
import net.minecraft.util.EnumFacing;

public interface IPartHost {

    IPart getPart(AEPartLocation side);

    default IPart getPart(final EnumFacing side) {
        return getPart(AEPartLocation.fromFacing(side));
    }
}
