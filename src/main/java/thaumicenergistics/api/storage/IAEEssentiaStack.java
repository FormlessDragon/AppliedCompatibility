package thaumicenergistics.api.storage;

import appeng.api.storage.data.IAEStack;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.EssentiaStack;

public interface IAEEssentiaStack extends IAEStack<IAEEssentiaStack> {

    Aspect getAspect();

    EssentiaStack getStack();
}
