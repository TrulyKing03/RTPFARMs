package com.example.rtpfarm;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class FarmWorld1BiomeProvider extends BiomeProvider {

    private static final List<Biome> ALLOWED_BIOMES = List.of(
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.TAIGA
    );

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        long hash = (x * 341873128712L) ^ (z * 132897987541L) ^ worldInfo.getSeed();
        int index = (int) Math.floorMod(hash, ALLOWED_BIOMES.size());
        return ALLOWED_BIOMES.get(index);
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return ALLOWED_BIOMES;
    }
}
