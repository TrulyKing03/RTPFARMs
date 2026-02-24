package com.example.rtpfarm;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class TreeBreakListener implements Listener {

    private final RTPFarmPlugin plugin;
    private final FarmWorldManager worldManager;

    public TreeBreakListener(RTPFarmPlugin plugin, FarmWorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onTreeBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !plugin.getConfig().getBoolean("tree-break.enabled", true)) {
            return;
        }
        Block start = event.getBlock();
        if (!isWood(start.getType())) {
            return;
        }
        if (!worldManager.isFarmWorld(start.getWorld())) {
            return;
        }

        event.setCancelled(true);
        int maxBlocks = Math.max(1, plugin.getConfig().getInt("tree-break.max-blocks", 256));
        boolean drops = plugin.getConfig().getBoolean("tree-break.drop-items", true);

        Set<Location> visited = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();
        queue.add(start);

        int broken = 0;
        while (!queue.isEmpty() && broken < maxBlocks) {
            Block block = queue.poll();
            if (block == null || !visited.add(block.getLocation())) {
                continue;
            }
            if (!isWood(block.getType())) {
                continue;
            }

            Material type = block.getType();
            World world = block.getWorld();
            Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);
            block.setType(Material.AIR, false);
            if (drops) {
                world.dropItemNaturally(dropLoc, new ItemStack(type, 1));
            }
            broken++;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        queue.add(block.getRelative(dx, dy, dz));
                    }
                }
            }
        }
    }

    private boolean isWood(Material material) {
        String name = material.name();
        return name.endsWith("_LOG") || name.endsWith("_WOOD");
    }
}
