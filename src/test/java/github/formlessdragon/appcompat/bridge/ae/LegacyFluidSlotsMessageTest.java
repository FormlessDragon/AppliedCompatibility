package github.formlessdragon.appcompat.bridge.ae;

import appeng.fluids.util.AEFluidStack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyFluidSlotsMessageTest {
    @BeforeAll
    static void initializeMinecraft() {
        Bootstrap.register();
    }

    @Test
    void commonNetworkInitializationCanEncodeSnapshotsWithoutClientProxy() {
        final var network = appeng.core.sync.network.NetworkHandler.instance();
        network.initialize();
        network.initialize();
        assertNotNull(network.channel().getPacketFrom(new LegacyFluidSlotsMessage(7, Map.of())));
    }

    @Test
    void snapshotPreservesWindowLongAmountsTagsAndExplicitEmptySlots() {
        final FluidStack fluid = new FluidStack(FluidRegistry.WATER, 1);
        fluid.tag = new NBTTagCompound();
        fluid.tag.setString("variant", "tagged-water");
        final var stack = AEFluidStack.fromFluidStack(fluid).setStackSize(1L << 32);
        final var slots = new LinkedHashMap<Integer, appeng.api.storage.data.IAEFluidStack>();
        slots.put(40, stack);
        slots.put(41, null);
        final var message = new LegacyFluidSlotsMessage(7, slots);
        stack.setStackSize(10);
        final ByteBuf buffer = Unpooled.buffer();
        try {
            message.toBytes(buffer);
            final var decoded = new LegacyFluidSlotsMessage();
            decoded.fromBytes(buffer);
            assertEquals(7, decoded.windowId);
            assertEquals(1L << 32, decoded.fluids.get(40).getStackSize());
            assertEquals(fluid.tag, decoded.fluids.get(40).getFluidStack().tag);
            assertTrue(decoded.fluids.containsKey(41));
            assertNull(decoded.fluids.get(41));
        } finally {
            buffer.release();
        }
    }

    @Test
    void reusedDecoderReplacesPreviousSnapshot() {
        final var decoded = new LegacyFluidSlotsMessage(1,
            Map.of(3, AEFluidStack.fromFluidStack(new FluidStack(FluidRegistry.WATER, 1))));
        final ByteBuf buffer = Unpooled.buffer();
        try {
            new LegacyFluidSlotsMessage(2, Map.of()).toBytes(buffer);
            decoded.fromBytes(buffer);
            assertEquals(2, decoded.windowId);
            assertTrue(decoded.fluids.isEmpty());
        } finally {
            buffer.release();
        }
    }

    @Test
    void invalidSlotCountsAreRejectedBeforeReadingEntries() {
        for (int count : new int[]{-1, 1025}) {
            final ByteBuf buffer = Unpooled.buffer().writeInt(1).writeInt(count);
            try {
                assertThrows(IllegalArgumentException.class,
                    () -> new LegacyFluidSlotsMessage().fromBytes(buffer));
            } finally {
                buffer.release();
            }
        }
    }
}
