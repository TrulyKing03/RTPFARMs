package com.example.rtpfarm;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RTPService {

    private final RTPFarmPlugin plugin;
    private final FarmWorldManager worldManager;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public RTPService(RTPFarmPlugin plugin, FarmWorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
    }

    public void teleportPlayer(Player player, String worldKey, boolean applyCooldown) {
        World world = worldManager.getWorldByKey(worldKey);
        if (world == null) {
            player.sendMessage(plugin.color("&cWorld is not loaded: " + worldKey));
            return;
        }

        if (applyCooldown && !player.hasPermission("rtpfarm.bypasscooldown")) {
            int cooldownSeconds = plugin.getConfig().getInt("rtp.cooldown-seconds", 30);
            long now = System.currentTimeMillis();
            long nextAllowed = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (nextAllowed > now) {
                long remain = (long) Math.ceil((nextAllowed - now) / 1000.0);
                player.sendMessage(plugin.msg("cooldown").replace("%seconds%", String.valueOf(remain)));
                return;
            }
            cooldowns.put(player.getUniqueId(), now + (cooldownSeconds * 1000L));
        }

        player.sendMessage(plugin.msg("searching").replace("%world%", world.getName()));
        Location target = findSafeLocation(world);
        if (target == null) {
            if (applyCooldown && !player.hasPermission("rtpfarm.bypasscooldown")) {
                cooldowns.remove(player.getUniqueId());
            }
            player.sendMessage(plugin.msg("failed"));
            return;
        }

        player.teleport(target);
        player.sendMessage(plugin.msg("success")
                .replace("%world%", world.getName())
                .replace("%x%", String.valueOf(target.getBlockX()))
                .replace("%y%", String.valueOf(target.getBlockY()))
                .replace("%z%", String.valueOf(target.getBlockZ())));
    }

    private Location findSafeLocation(World world) {
        int minRadius = Math.max(0, plugin.getConfig().getInt("rtp.min-radius", 200));
        int maxRadius = Math.max(minRadius + 1, plugin.getConfig().getInt("rtp.max-radius", 2500));
        int maxAttempts = Math.max(1, plugin.getConfig().getInt("rtp.max-attempts", 30));
        int yOffset = plugin.getConfig().getInt("rtp.safe-y-offset", 1);
        boolean avoidWater = plugin.getConfig().getBoolean("rtp.avoid-water", true);
        boolean avoidLava = plugin.getConfig().getBoolean("rtp.avoid-lava", true);

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = randomCoordinate(minRadius, maxRadius) * (ThreadLocalRandom.current().nextBoolean() ? -1 : 1);
            int z = randomCoordinate(minRadius, maxRadius) * (ThreadLocalRandom.current().nextBoolean() ? -1 : 1);
            int y = world.getHighestBlockYAt(x, z) + yOffset;

            if (y >= world.getMaxHeight() - 1) {
                continue;
            }

            Block ground = world.getBlockAt(x, y - 1, z);
            Block feet = world.getBlockAt(x, y, z);
            Block head = world.getBlockAt(x, y + 1, z);

            if (!ground.getType().isSolid()) {
                continue;
            }
            if (avoidWater && ground.getType() == Material.WATER) {
                continue;
            }
            if (avoidLava && ground.getType() == Material.LAVA) {
                continue;
            }
            if (!isPassable(feet) || !isPassable(head)) {
                continue;
            }

            return new Location(world, x + 0.5, y, z + 0.5);
        }
        return null;
    }

    private int randomCoordinate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private boolean isPassable(Block block) {
        Material type = block.getType();
        return type.isAir() || type == Material.CAVE_AIR || type == Material.VOID_AIR;
    }
}
