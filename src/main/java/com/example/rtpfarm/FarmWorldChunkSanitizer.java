package com.example.rtpfarm;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Set;

public class FarmWorldChunkSanitizer implements Listener {

    private static final Set<Material> ALLOWED_ORES = Set.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE
    );

    private final FarmWorldManager worldManager;

    public FarmWorldChunkSanitizer(FarmWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) {
            return;
        }
        if (!worldManager.isChunkSanitizedWorld(event.getWorld())) {
            return;
        }
        sanitizeChunk(event.getChunk());
    }

    private void sanitizeChunk(Chunk chunk) {
        int minY = chunk.getWorld().getMinHeight();
        int maxY = chunk.getWorld().getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();

                    if (isDisallowedOre(type)) {
                        block.setType(type.name().startsWith("DEEPSLATE_") ? Material.DEEPSLATE : Material.STONE, false);
                        continue;
                    }

                    if (isWoodLike(type) && !isAllowedWood(type)) {
                        block.setType(mapWoodFallback(type), false);
                    }
                }
            }
        }
    }

    private boolean isDisallowedOre(Material material) {
        return material.name().endsWith("_ORE") && !ALLOWED_ORES.contains(material);
    }

    private boolean isWoodLike(Material material) {
        String name = material.name();
        return name.endsWith("_LOG") || name.endsWith("_WOOD") || name.endsWith("_LEAVES") || name.endsWith("_SAPLING");
    }

    private boolean isAllowedWood(Material material) {
        String name = material.name();
        return name.startsWith("OAK_") || name.startsWith("BIRCH_") || name.startsWith("SPRUCE_");
    }

    private Material mapWoodFallback(Material input) {
        String name = input.name();
        if (name.endsWith("_LEAVES")) {
            return Material.OAK_LEAVES;
        }
        if (name.endsWith("_SAPLING")) {
            return Material.OAK_SAPLING;
        }
        if (name.endsWith("_WOOD")) {
            return Material.OAK_WOOD;
        }
        return Material.OAK_LOG;
    }
}
