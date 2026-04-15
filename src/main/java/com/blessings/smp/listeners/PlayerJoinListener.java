package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final BlessingsSMP plugin;
    
    public PlayerJoinListener(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        if (plugin.getConfigManager().isSMPStarted() && data.getBlessing() == null) {
            plugin.getBlessingManager().assignRandomBlessing(player);
        }
        
        if (data.getBlessing() != null) {
            plugin.getBlessingManager().setBlessing(player, data.getBlessing(), false);
        }
        
        plugin.getArmorEffectsManager().startArmorEffectTask(player);
    }
}