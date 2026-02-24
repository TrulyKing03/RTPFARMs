package com.example.rtpfarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FarmWorldMenu {

    private static final int MENU_SIZE = 27;
    private static final int SLOT_FARMWORLD_1 = 11;
    private static final int SLOT_FARMWORLD_2 = 15;
    private static final int SLOT_CLOSE = 22;

    private final RTPFarmPlugin plugin;
    private final RTPService rtpService;

    public FarmWorldMenu(RTPFarmPlugin plugin, RTPService rtpService) {
        this.plugin = plugin;
        this.rtpService = rtpService;
    }

    public void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, MENU_SIZE, getMenuTitle());
        fillBackground(menu);
        menu.setItem(SLOT_FARMWORLD_1, buildGlowingItem(
                Material.NETHER_STAR,
                "&d&lFarmworld 1",
                "&7Wood: &fOak, Birch, Spruce",
                "&7Ore: &fCoal, Iron, Copper",
                "&7Structures: &cDisabled",
                "&aClick to teleport"
        ));
        menu.setItem(SLOT_FARMWORLD_2, buildGlowingItem(
                Material.ENDER_EYE,
                "&5&lFarmworld 2",
                "&7Standard terrain generation",
                "&7Daily automatic reset",
                "&7RTP enabled",
                "&aClick to teleport"
        ));
        menu.setItem(SLOT_CLOSE, buildItem(Material.BARRIER, "&c&lClose Menu", "&7Click to close"));

        player.openInventory(menu);
        player.sendMessage(plugin.msg("menu-opened"));
    }

    public void handleSelection(Player player, Material material) {
        if (material == Material.NETHER_STAR) {
            rtpService.teleportPlayer(player, "farmworld1", true);
        } else if (material == Material.ENDER_EYE) {
            rtpService.teleportPlayer(player, "farmworld2", true);
        }
    }

    public boolean isMenuTitle(String inputTitle) {
        return getMenuTitle().equals(inputTitle);
    }

    private String getMenuTitle() {
        return plugin.color(plugin.getConfig().getString("menu.title", "&5&lAvertox.net"));
    }

    private void fillBackground(Inventory menu) {
        ItemStack edge = buildItem(Material.PURPLE_STAINED_GLASS_PANE, "&5 ");
        ItemStack center = buildItem(Material.BLACK_STAINED_GLASS_PANE, "&0 ");

        for (int slot = 0; slot < MENU_SIZE; slot++) {
            menu.setItem(slot, center);
        }

        for (int i = 0; i < 9; i++) {
            menu.setItem(i, edge);
            menu.setItem(18 + i, edge);
        }
        menu.setItem(9, edge);
        menu.setItem(17, edge);
    }

    private ItemStack buildGlowingItem(Material material, String name, String... loreLines) {
        ItemStack item = buildItem(material, name, loreLines);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
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
