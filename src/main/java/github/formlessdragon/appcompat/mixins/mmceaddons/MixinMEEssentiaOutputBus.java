package github.formlessdragon.appcompat.mixins.mmceaddons;

import ae2.api.networking.IGridNode;
import ae2.api.networking.ticking.IGridTickable;
import ae2.api.networking.ticking.TickRateModulation;
import ae2.api.networking.ticking.TickingRequest;
import ae2.api.stacks.KeyAmounts;
import ae2.me.helpers.IGridConnectedTile;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.MEEssentiaBus;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.MEEssentiaOutputBus;
import github.alecsio.mmceaddons.common.hatch.thaumcraft.ae2.essentia.RequirementEssentia;
import github.formlessdragon.appcompat.bridge.ae.ManagedNodeHelper;
import github.formlessdragon.appcompat.bridge.mmceaddons.AppCompatEssentiaBridge;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(value = MEEssentiaOutputBus.class, remap = false, priority = 999)
public abstract class MixinMEEssentiaOutputBus
    extends MEEssentiaBus
    implements IGridTickable {

    @Shadow
    private Map<String, Long> aspectAmounts;

    @Overwrite
    protected void updateSnapshot() {
        this.markNoUpdateSync();
        if (!this.aspectAmounts.isEmpty()) {
            ManagedNodeHelper.alert(((IGridConnectedTile) this).getMainNode());
        }
    }

    @Overwrite
    protected CraftCheck checkSnapshot(final RequirementEssentia requirement) {
        return this.aspectAmounts.containsKey(requirement.getEssentiaStack().getAspectTag())
            ? CraftCheck.failure("error.modularmachineryaddons.requirement.missing.essentia.output")
            : CraftCheck.success();
    }

    @Overwrite
    public void handle(final RequirementEssentia requirement) {
        final long requested = requirement.getEssentiaStack().getAmount();
        final long inserted = AppCompatEssentiaBridge.insert(this, requirement, requested);
        final long remaining = requested - inserted;
        if (remaining > 0) {
            this.lock.writeLock().lock();
            try {
                this.aspectAmounts.merge(requirement.getEssentiaStack().getAspectTag(), remaining,
                    (left, right) -> KeyAmounts.add(AppCompatEssentiaBridge.key(requirement), left, right));
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
        this.lock.writeLock().lock();
        try {
            final boolean changed = AppCompatEssentiaBridge.insertPending(this, this.aspectAmounts);
            if (changed) {
                this.markNoUpdateSync();
                return TickRateModulation.FASTER;
            }
            return this.aspectAmounts.isEmpty() ? TickRateModulation.SLEEP : TickRateModulation.SLOWER;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
