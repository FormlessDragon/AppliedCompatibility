package github.formlessdragon.appcompat.util.enderioae;

import ae2.api.networking.GridHelper;
import ae2.hooks.ticking.TickHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RescanScheduler {

    private RescanScheduler() {}

    /**
     * Tracks conduit-neighbor pairs that already have a delayed rescan queued, preventing the synchronous
     * neighbor update caused by that rescan from enqueueing another identical rescan immediately.
     */
    private static final Set<RescanKey> PENDING = ConcurrentHashMap.newKeySet();

    /**
     * Schedules one delayed external-connection rescan for an ME conduit next to a new AE in-world node host.
     * The nested AE2 tick queue callback intentionally runs after AE's first-tick node creation has had a chance
     * to make the neighboring node visible to {@link GridHelper#getExposedNode(World, BlockPos, EnumFacing)}.
     */
    public static void schedule(final World world, final BlockPos conduitPos, final BlockPos neighborPos) {
        if (world.isRemote) {
            return;
        }

        final RescanKey key = new RescanKey(world.provider.getDimension(), conduitPos.toImmutable(), neighborPos.toImmutable());
        if (!PENDING.add(key)) {
            return;
        }

        TickHandler.instance().addCallable(world, ignored ->
            TickHandler.instance().addCallable(world, ignoredAgain -> {
                try {
                    if (!world.isBlockLoaded(conduitPos) || !world.isBlockLoaded(neighborPos)) {
                        return;
                    }

                    final IBlockState neighborState = world.getBlockState(neighborPos);
                    world.notifyNeighborsOfStateChange(neighborPos, neighborState.getBlock(), true);
                } finally {
                    PENDING.remove(key);
                }
            })
        );
    }

    /**
     * Identifies one pending delayed rescan by dimension, conduit bundle position, and neighboring AE host position.
     */
    private record RescanKey(int dimensionId, BlockPos conduitPos, BlockPos neighborPos){}
}
