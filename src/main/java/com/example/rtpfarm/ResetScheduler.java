package com.example.rtpfarm;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.LocalTime;

public class ResetScheduler {

    private final RTPFarmPlugin plugin;
    private final FarmWorldManager worldManager;
    private BukkitTask task;
    private LocalDate lastResetDate;

    public ResetScheduler(RTPFarmPlugin plugin, FarmWorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
    }

    public void start() {
        stop();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L * 30L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        int resetHour = plugin.getConfig().getInt("daily-reset-hour", 2);
        int resetMinute = plugin.getConfig().getInt("daily-reset-minute", 0);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (lastResetDate != null && lastResetDate.equals(today)) {
            return;
        }

        if (now.getHour() == resetHour && now.getMinute() == resetMinute) {
            worldManager.resetAllFarmWorlds();
            lastResetDate = today;
            plugin.getServer().broadcastMessage(plugin.color("&a[rtpfarm] Farmworld reset completed."));
        }
    }
}
