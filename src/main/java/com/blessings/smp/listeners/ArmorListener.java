package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorListener implements Listener {
    private final BlessingsSMP plugin;
    
    public ArmorListener(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    plugin.getArmorEffectsManager().startArmorEffectTask(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getArmorEffectsManager().startArmorEffectTask(player);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getArmorEffectsManager().startArmorEffectTask(player);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getArmorEffectsManager().stopArmorEffectTask(event.getPlayer().getUniqueId());
    }
}