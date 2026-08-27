package github.formlessdragon.appcompat.mixins.mmceaddons;

import ae2.api.networking.IGridNode;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.api.storage.MEStorage;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.MEEssentiaBus;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.MEEssentiaInputBus;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.RequirementEssentia;
import github.formlessdragon.appcompat.bridge.mmceaddons.AppCompatEssentiaBridge;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

@Mixin(value = MEEssentiaInputBus.class, remap = false, priority = 999)
public abstract class MixinMEEssentiaInputBus
    extends MEEssentiaBus
    implements IGridTickable {

    @Shadow
    protected ReadWriteLock lock;
    @Shadow
    private Map<String, Long> aspectAmounts;

    @Overwrite
    protected void updateSnapshot() {
        final MEStorage storage = AppCompatEssentiaBridge.getStorage(this);
        this.lock.writeLock().lock();
        try {
            AppCompatEssentiaBridge.updateSnapshot(this.aspectAmounts, storage);
            this.markNoUpdateSync();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Overwrite
    protected CraftCheck checkSnapshot(final RequirementEssentia requirement) {
        final long available = this.aspectAmounts.getOrDefault(requirement.getEssentiaStack().getAspectTag(), 0L);
        return available >= requirement.getEssentiaStack().getAmount()
            ? CraftCheck.success()
            : CraftCheck.failure("error.modularmachineryaddons.requirement.missing.essentia.input");
    }

    @Overwrite
    public void handle(final RequirementEssentia requirement) {
        final long extracted = AppCompatEssentiaBridge.extract(this, requirement);
        if (extracted > 0) {
            this.lock.writeLock().lock();
            try {
                this.aspectAmounts.merge(requirement.getEssentiaStack().getAspectTag(), extracted, Long::sum);
                this.markNoUpdateSync();
            } finally {
                this.lock.writeLock().unlock();
            }
        }
        super.handle(requirement);
    }

    @Override
    @Unique
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(10, 120, false);
    }

    @Override
    @Unique
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        this.updateSnapshot();
        return TickRateModulation.SLOWER;
    }
}
