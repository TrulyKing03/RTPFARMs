package com.example.rtpfarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FarmWorldMenu {

    private final RTPFarmPlugin plugin;
    private final RTPService rtpService;

    public FarmWorldMenu(RTPFarmPlugin plugin, RTPService rtpService) {
        this.plugin = plugin;
        this.rtpService = rtpService;
    }

    public void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, getMenuTitle());
        menu.setItem(2, buildItem(Material.OAK_SAPLING, "&aFarmworld 1", "&7Oak/Birch/Spruce + Coal/Iron/Copper", "&7No structures"));
        menu.setItem(6, buildItem(Material.SPRUCE_SAPLING, "&bFarmworld 2", "&7Standard world generation", "&7Daily reset + RTP"));
        menu.setItem(8, buildItem(Material.BARRIER, "&cClose"));
        player.openInventory(menu);
        player.sendMessage(plugin.msg("menu-opened"));
    }

    public void handleSelection(Player player, Material material) {
        if (material == Material.OAK_SAPLING) {
            rtpService.teleportPlayer(player, "farmworld1", true);
        } else if (material == Material.SPRUCE_SAPLING) {
            rtpService.teleportPlayer(player, "farmworld2", true);
        }
    }

    public boolean isMenuTitle(String inputTitle) {
        return getMenuTitle().equals(inputTitle);
    }

    private String getMenuTitle() {
        return plugin.color(plugin.getConfig().getString("menu.title", "&8Farmworld Selector"));
    }

    private ItemStack buildItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.color(name));
            meta.setLore(java.util.Arrays.stream(loreLines).map(plugin::color).toList());
            item.setItemMeta(meta);
        }
        return item;
    }
}
