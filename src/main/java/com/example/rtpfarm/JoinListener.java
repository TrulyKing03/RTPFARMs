package com.example.rtpfarm;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final RTPFarmPlugin plugin;
    private final FarmWorldManager worldManager;
    private final RTPService rtpService;

    public JoinListener(RTPFarmPlugin plugin, FarmWorldManager worldManager, RTPService rtpService) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.rtpService = rtpService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("join-random-teleport", true)) {
            return;
        }
        Player player = event.getPlayer();
        long delay = plugin.getConfig().getLong("join-teleport-delay-ticks", 40L);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            rtpService.teleportPlayer(player, worldManager.getJoinWorldKey(), false);
            player.sendMessage(plugin.msg("join-teleport"));
        }, delay);
    }
}
