package com.example.rtpfarm;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class FarmWorldManager {

    private static final List<String> WORLD_KEYS = List.of("farmworld1", "farmworld2");

    private final RTPFarmPlugin plugin;

    public FarmWorldManager(RTPFarmPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadOrCreateWorlds() {
        for (String key : WORLD_KEYS) {
            createWorldIfNeeded(key);
        }
    }

    public boolean isFarmWorld(World world) {
        if (world == null) {
            return false;
        }
        String name = world.getName();
        for (String key : WORLD_KEYS) {
            if (name.equalsIgnoreCase(getWorldName(key))) {
                return true;
            }
        }
        return false;
    }

    public boolean isChunkSanitizedWorld(World world) {
        return world != null && world.getName().equalsIgnoreCase(getWorldName("farmworld1"))
                && plugin.getConfig().getBoolean("worlds.farmworld1.sanitize-new-chunks", true);
    }

    public World getWorldByKey(String key) {
        return Bukkit.getWorld(getWorldName(key));
    }

    public String getJoinWorldKey() {
        return plugin.getConfig().getString("join-world", "farmworld2").toLowerCase();
    }

    public String getWorldName(String key) {
        return plugin.getConfig().getString("worlds." + key + ".name", key);
    }

    public void resetAllFarmWorlds() {
        for (String key : WORLD_KEYS) {
            resetWorld(key);
        }
        plugin.getLogger().info("Farmworld reset complete at "
                + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void resetWorld(String key) {
        String worldName = getWorldName(key);
        World world = Bukkit.getWorld(worldName);
        World fallback = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);

        if (world != null && fallback != null) {
            for (Player player : world.getPlayers()) {
                player.teleport(fallback.getSpawnLocation());
            }
        }

        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        Path worldPath = Path.of(Bukkit.getWorldContainer().getAbsolutePath(), worldName);
        if (Files.exists(worldPath)) {
            try {
                Files.walk(worldPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException exception) {
                                throw new RuntimeException(exception);
                            }
                        });
            } catch (RuntimeException | IOException exception) {
                plugin.getLogger().warning("Failed to delete world folder for " + worldName + ": " + exception.getMessage());
            }
        }

        createWorldIfNeeded(key);
    }

    private void createWorldIfNeeded(String key) {
        String worldName = getWorldName(key);
        if (Bukkit.getWorld(worldName) != null) {
            return;
        }

        boolean structures = plugin.getConfig().getBoolean("worlds." + key + ".generate-structures", true);
        WorldCreator creator = WorldCreator.name(worldName).generateStructures(structures);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);
        creator.createWorld();
    }
}
