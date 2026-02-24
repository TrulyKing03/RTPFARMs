package com.example.rtpfarm;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FarmWorldMenuListener implements Listener {

    private final FarmWorldMenu menu;

    public FarmWorldMenuListener(FarmWorldMenu menu) {
        this.menu = menu;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player player)) {
            return;
        }
        if (event.getView().getTitle() == null || !menu.isMenuTitle(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        Material clicked = event.getCurrentItem().getType();
        if (clicked == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        menu.handleSelection(player, clicked);
        player.closeInventory();
    }
}
