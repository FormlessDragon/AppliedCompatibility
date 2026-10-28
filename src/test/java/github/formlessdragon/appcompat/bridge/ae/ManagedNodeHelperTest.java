package github.formlessdragon.appcompat.bridge.ae;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.ticking.ITickManager;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagedNodeHelperTest {
    private static <T> T proxy(final Class<T> type, final Invocation call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
            (proxy, method, args) -> call.invoke(method.getName(), args)));
    }

    @Test
    void nullAndInactiveNodesDoNotTouchGridServices() {
        assertFalse(ManagedNodeHelper.alert(null));
        final IManagedGridNode inactive = proxy(IManagedGridNode.class, (method, args) -> {
            assertEquals("isActive", method);
            return false;
        });
        assertFalse(ManagedNodeHelper.alert(inactive));
    }

    @Test
    void alertReturnsTickManagersResult() {
        final IGridNode node = proxy(IGridNode.class, (method, args) -> null);
        final AtomicInteger calls = new AtomicInteger();
        final ITickManager ticks = proxy(ITickManager.class, (method, args) -> {
            assertEquals("alertDevice", method);
            assertSame(node, args[0]);
            return calls.incrementAndGet() == 1;
        });
        final IGrid grid = proxy(IGrid.class, (method, args) -> {
            assertEquals("getTickManager", method);
            return ticks;
        });
        final IManagedGridNode managed = proxy(IManagedGridNode.class, (method, args) -> switch (method) {
            case "isActive" -> true;
            case "getNode" -> node;
            case "getGrid" -> grid;
            default -> throw new AssertionError(method);
        });
        assertTrue(ManagedNodeHelper.alert(managed));
        assertFalse(ManagedNodeHelper.alert(managed));
        assertEquals(2, calls.get());
    }

    private interface Invocation {
        Object invoke(String method, Object[] args);
    }
}
