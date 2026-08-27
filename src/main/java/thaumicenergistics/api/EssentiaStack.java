package thaumicenergistics.api;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;

public class EssentiaStack extends thaumicenergistics.api.stacks.EssentiaStack {

    public EssentiaStack(final Aspect aspect, final int amount) {
        super(aspect, amount);
    }

    public EssentiaStack(final String aspect, final int amount) {
        super(aspect, amount);
    }

    public static EssentiaStack readFromNBT(final NBTTagCompound tag) {
        final thaumicenergistics.api.stacks.EssentiaStack stack =
            thaumicenergistics.api.stacks.EssentiaStack.readFromNBT(tag);
        return stack == null ? null : new EssentiaStack(stack.getAspectTag(), stack.getAmount());
    }

    @Override
    public EssentiaStack copy() {
        return new EssentiaStack(getAspectTag(), getAmount());
    }
}
