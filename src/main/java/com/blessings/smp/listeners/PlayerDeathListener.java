package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener implements Listener {
    private final BlessingsSMP plugin;
    
    public PlayerDeathListener(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        int penalty = plugin.getConfigManager().getTokenDeathPenalty();
        plugin.getTokenManager().removeTokens(victim, penalty);
        
        if (killer != null && killer != victim) {
            int reward = plugin.getConfigManager().getTokenKillReward();
            plugin.getTokenManager().addTokens(killer, reward);
        }
        
        plugin.getArmorEffectsManager().removeAllArmorEffects(victim);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    plugin.getArmorEffectsManager().startArmorEffectTask(victim);
                }
            }
        }.runTaskLater(plugin, 40L);
    }
}