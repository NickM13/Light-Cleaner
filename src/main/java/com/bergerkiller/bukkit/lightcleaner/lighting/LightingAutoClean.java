package com.bergerkiller.bukkit.lightcleaner.lighting;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.lightcleaner.LightCleaner;

/**
 * Handles the automatic cleanup of chunk lighting when chunks are generated
 */
public class LightingAutoClean {
    private static HashMap<World, HashSet<IntVector2>> queues = new HashMap<World, HashSet<IntVector2>>();
    private static Task autoCleanTask = null;

    /**
     * Checks all neighbouring chunks to see if they are fully surrounded by chunks (now), and
     * schedules lighting repairs. This function only does anything when automatic cleaning is activated.
     * 
     * @param world the chunk is in
     * @param chunkX coordinate
     * @param chunkZ coordinate
     */
    public static void handleChunkGenerated(World world, int chunkX, int chunkZ) {
        if (!LightCleaner.autoCleanEnabled) {
            return;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (!WorldUtil.isChunkAvailable(world, chunkX + dx, chunkZ + dz)) {
                    continue;
                }

                // Check that all chunks surrounding this chunk are all available
                boolean allNeighboursLoaded = true;
                for (int dx2 = -1; dx2 <= 1 && allNeighboursLoaded; dx2++) {
                    for (int dz2 = -1; dz2 <= 1 && allNeighboursLoaded; dz2++) {
                        if (dx2 == 0 && dz2 == 0) {
                            continue; // ignore self
                        }
                        if (dx2 == -dx && dz2 == -dz) {
                            continue; // ignore the original generated chunk
                        }
                        allNeighboursLoaded &= WorldUtil.isChunkAvailable(world, chunkX + dx + dx2, chunkZ + dz + dz2);
                    }
                }

                // If all neighbours are available, schedule it for fixing
                if (allNeighboursLoaded) {
                    schedule(world, chunkX + dx, chunkZ + dz);
                }
            }
        }
    }

    public static void schedule(World world, int chunkX, int chunkZ) {
        HashSet<IntVector2> queue = queues.get(world);
        if (queue == null) {
            queue = new HashSet<IntVector2>(9);
            queues.put(world, queue);
        }

        // Queue this chunk, and all its neighbours
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                queue.add(new IntVector2(chunkX + dx, chunkZ + dz));
            }
        }

        // Initialize clean task if it hasn't been yet
        if (autoCleanTask == null) {
            autoCleanTask = new Task(LightCleaner.plugin) {
                @Override
                public void run() {
                    while (queues.size() > 0) {
                        World world = queues.keySet().iterator().next();
                        HashSet<IntVector2> chunks = queues.remove(world);
                        LightingService.schedule(world, chunks);
                    }
                }
            };
        }

        // Postpone the tick task while there are less than 100 chunks in the queue
        if (queue.size() < 100) {
            autoCleanTask.stop().start(80);
        }
    }
}