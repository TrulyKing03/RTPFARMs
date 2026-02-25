package com.example.rtpfarm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RTPFarmPlugin extends JavaPlugin {

    private FarmWorldManager worldManager;
    private RTPService rtpService;
    private FarmWorldMenu menu;
    private ResetScheduler resetScheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.worldManager = new FarmWorldManager(this);
        this.worldManager.loadOrCreateWorlds();

        this.rtpService = new RTPService(this, worldManager);
        this.menu = new FarmWorldMenu(this, rtpService);
        this.resetScheduler = new ResetScheduler(this, worldManager);
        this.resetScheduler.start();

        FarmWorldCommand commandHandler = new FarmWorldCommand(this, worldManager, rtpService, menu);
        registerCommand("farmworld", commandHandler);
        registerCommand("rtp", commandHandler);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new FarmWorldMenuListener(menu), this);
        pluginManager.registerEvents(new JoinListener(this, worldManager, rtpService), this);
        pluginManager.registerEvents(new FarmWorldChunkSanitizer(worldManager), this);
    }

    @Override
    public void onDisable() {
        if (resetScheduler != null) {
            resetScheduler.stop();
        }
    }

    private void registerCommand(String name, FarmWorldCommand commandHandler) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().severe("Missing command in plugin.yml: " + name);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        command.setExecutor(commandHandler);
        command.setTabCompleter(commandHandler);
    }

    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public String msg(String path) {
        FileConfiguration config = getConfig();
        return color(config.getString("messages." + path, ""));
    }
}
