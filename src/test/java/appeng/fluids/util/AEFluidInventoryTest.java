package appeng.fluids.util;

import appeng.api.storage.data.IAEFluidStack;
import appeng.util.inv.InvOperation;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AEFluidInventoryTest {
    @BeforeAll
    static void initializeMinecraft() {
        Bootstrap.register();
    }

    private static FluidStack water(final int amount) {
        return new FluidStack(FluidRegistry.WATER, amount);
    }

    private static void onServer(final Runnable assertions) throws Exception {
        // Cleanroom derives the effective side from the thread group.
        final FutureTask<Void> task = new FutureTask<>(assertions, null);
        SidedThreadGroups.SERVER.newThread(task).start();
        task.get(10, TimeUnit.SECONDS);
    }

    @Test
    void simulationRespectsPerSlotCapacityWithoutMutationOrCallbacks() {
        final AtomicInteger changes = new AtomicInteger();
        final AEFluidInventory inventory = new AEFluidInventory((tank, slot) -> changes.incrementAndGet(), 2, 1000);
        assertEquals(1000, inventory.fill(0, water(1500), false));
        assertEquals(2000, inventory.fill(water(2500), false));
        assertNull(inventory.getFluidInSlot(0));
        assertNull(inventory.getFluidInSlot(1));
        assertEquals(0, changes.get());
        assertEquals(1000, inventory.getTankProperties()[0].getCapacity());
    }

    @Test
    void fullTankDoesNotPublishAnotherChangeAndPartialFillReportsOnlyAcceptedFluid() throws Exception {
        onServer(() -> {
            final List<Integer> accepted = new ArrayList<>();
            final IAEFluidInventory listener = new IAEFluidInventory() {
                @Override public void onFluidInventoryChanged(final IAEFluidTank tank, final int slot) { }
                @Override public void onFluidInventoryChanged(final IAEFluidTank tank, final int slot,
                        final InvOperation operation, final FluidStack added, final FluidStack removed) {
                    if (operation == InvOperation.INSERT) accepted.add(added.amount);
                }
            };
            final AEFluidInventory inventory = new AEFluidInventory(listener, 1, 1000);
            assertEquals(900, inventory.fill(water(900), true));
            assertEquals(100, inventory.fill(water(500), true));
            assertEquals(0, inventory.fill(water(500), true));
            assertEquals(List.of(900, 100), accepted);
            assertEquals(1000, inventory.getFluidInSlot(0).getStackSize());
        });
    }

    @Test
    void longStoredAmountsCannotOverflowCapacityCalculation() {
        final AEFluidInventory inventory = new AEFluidInventory(null, 1, 1000);
        inventory.setFluidInSlot(0, AEFluidStack.fromFluidStack(water(1)).setStackSize(1L << 32));
        assertEquals(0, inventory.fill(water(500), false));
        assertEquals(0, inventory.fill(water(500), true));
        assertEquals(1L << 32, inventory.getFluidInSlot(0).getStackSize());
        inventory.setCapacity(0);
        assertEquals(0, inventory.fill(water(1), true));
    }

    @Test
    void fillAndDrainPreserveTagsAndDoNotMixDifferentFluids() {
        final AEFluidInventory inventory = new AEFluidInventory(null, 3, 1000);
        final FluidStack tagged = water(1700);
        tagged.tag = new NBTTagCompound();
        tagged.tag.setString("variant", "tagged-water");
        assertEquals(1700, inventory.fill(tagged, true));
        assertEquals(1000, inventory.getFluidInSlot(0).getStackSize());
        assertEquals(700, inventory.getFluidInSlot(1).getStackSize());
        assertEquals(0, inventory.fill(0, new FluidStack(FluidRegistry.LAVA, 100), true));
        final FluidStack simulated = inventory.drain(1200, false);
        assertEquals(1200, simulated.amount);
        assertEquals(tagged.tag, simulated.tag);
        assertEquals(1000, inventory.getFluidInSlot(0).getStackSize());
        final FluidStack drained = inventory.drain(1200, true);
        assertEquals(1200, drained.amount);
        assertEquals(tagged.tag, drained.tag);
        assertNull(inventory.getFluidInSlot(0));
        assertEquals(500, inventory.getFluidInSlot(1).getStackSize());
    }

    @Test
    void invalidSlotsAndNonPositiveAmountsCannotChangeContents() {
        final AEFluidInventory inventory = new AEFluidInventory(null, 1, 1000);
        inventory.fill(water(500), true);
        assertEquals(0, inventory.fill(-1, water(1), true));
        assertEquals(0, inventory.fill(1, water(1), true));
        assertEquals(0, inventory.fill(water(-1), true));
        assertNull(inventory.drain(-1, 1, true));
        assertNull(inventory.drain(1, water(1), true));
        assertNull(inventory.drain(-1, true));
        assertNull(inventory.drain(water(0), true));
        assertEquals(500, inventory.getFluidInSlot(0).getStackSize());
    }

    @Test
    void nbtRoundtripPreservesLongAmountsAndClearsPreviouslyOccupiedSlots() {
        final AEFluidInventory source = new AEFluidInventory(null, 2);
        final IAEFluidStack stack = AEFluidStack.fromFluidStack(water(1)).setStackSize((long) Integer.MAX_VALUE + 17);
        source.setFluidInSlot(0, stack);
        final NBTTagCompound tag = new NBTTagCompound();
        source.writeToNBT(tag, "tank");
        final AEFluidInventory target = new AEFluidInventory(null, 2);
        target.setFluidInSlot(1, AEFluidStack.fromFluidStack(water(20)));
        target.readFromNBT(tag, "tank");
        assertEquals(stack.getStackSize(), target.getFluidInSlot(0).getStackSize());
        assertNull(target.getFluidInSlot(1));
    }

    @Test
    void legacyTwoArgumentListenerReceivesRealChanges() throws Exception {
        onServer(() -> {
            final AtomicInteger changes = new AtomicInteger();
            final AEFluidInventory inventory = new AEFluidInventory((tank, slot) -> changes.incrementAndGet(), 1, 1000);
            inventory.fill(water(600), true);
            inventory.drain(200, true);
            assertEquals(2, changes.get());
            assertEquals(400, inventory.getFluidInSlot(0).getStackSize());
        });
    }

    @Test
    void settingSameFluidAtDifferentAmountPublishesOneChangeAndCopiesInput() throws Exception {
        onServer(() -> {
            final AtomicInteger changes = new AtomicInteger();
            final AEFluidInventory inventory = new AEFluidInventory((tank, slot) -> changes.incrementAndGet(), 1, 1000);
            inventory.setFluidInSlot(0, AEFluidStack.fromFluidStack(water(100)));
            final IAEFluidStack replacement = AEFluidStack.fromFluidStack(water(500));
            inventory.setFluidInSlot(0, replacement);
            replacement.setStackSize(900);
            assertEquals(500, inventory.getFluidInSlot(0).getStackSize());
            assertEquals(2, changes.get());
        });
    }
}
