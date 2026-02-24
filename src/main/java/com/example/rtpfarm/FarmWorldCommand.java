package com.example.rtpfarm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FarmWorldCommand implements CommandExecutor, TabCompleter {

    private final RTPFarmPlugin plugin;
    private final FarmWorldManager worldManager;
    private final RTPService rtpService;
    private final FarmWorldMenu menu;

    public FarmWorldCommand(RTPFarmPlugin plugin, FarmWorldManager worldManager, RTPService rtpService, FarmWorldMenu menu) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.rtpService = rtpService;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.msg("only-player"));
            return true;
        }
        if (!player.hasPermission("rtpfarm.use")) {
            player.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("rtp")) {
            String worldKey = args.length > 0 ? args[0].toLowerCase() : worldManager.getJoinWorldKey();
            rtpService.teleportPlayer(player, normalizeWorldKey(worldKey), true);
            return true;
        }

        if (args.length == 0) {
            menu.open(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("tp")) {
            String worldKey = args.length > 1 ? args[1].toLowerCase() : worldManager.getJoinWorldKey();
            rtpService.teleportPlayer(player, normalizeWorldKey(worldKey), true);
            return true;
        }

        if (sub.equals("reload") || sub.equals("reset")) {
            if (!player.hasPermission("rtpfarm.admin")) {
                player.sendMessage(plugin.msg("no-permission"));
                return true;
            }
            if (sub.equals("reload")) {
                plugin.reloadConfig();
                player.sendMessage(plugin.msg("reloaded"));
            } else {
                worldManager.resetAllFarmWorlds();
                player.sendMessage(plugin.msg("reset-done"));
            }
            return true;
        }

        menu.open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("rtp")) {
            if (args.length == 1) {
                suggestions.add("farmworld1");
                suggestions.add("farmworld2");
            }
            return suggestions;
        }

        if (args.length == 1) {
            suggestions.add("tp");
            if (sender.hasPermission("rtpfarm.admin")) {
                suggestions.add("reload");
                suggestions.add("reset");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            suggestions.add("farmworld1");
            suggestions.add("farmworld2");
        }
        return suggestions;
    }

    private String normalizeWorldKey(String input) {
        if (input.equalsIgnoreCase(worldManager.getWorldName("farmworld1"))) {
            return "farmworld1";
        }
        if (input.equalsIgnoreCase(worldManager.getWorldName("farmworld2"))) {
            return "farmworld2";
        }
        if (input.equals("farmworld1") || input.equals("farmworld2")) {
            return input;
        }
        return worldManager.getJoinWorldKey();
    }
}
