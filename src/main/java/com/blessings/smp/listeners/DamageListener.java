package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DamageListener implements Listener {
    private final BlessingsSMP plugin;
    private final Random random;
    
    public DamageListener(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            ItemStack boots = player.getInventory().getBoots();
            if (plugin.getMythicItemManager().isAncientBoots(boots)) {
                event.setCancelled(true);
                return;
            }
        }
        
        if (data.getBlessing() == null) return;
        
        if (data.getBlessing() == BlessingType.ENDURANCE) {
            double reduction = plugin.getConfigManager().getDouble("abilities.endurance.titan-skin.damage-reduction", 0.20);
            event.setDamage(event.getDamage() * (1.0 - reduction));
        }
        
        if (data.getBlessing() == BlessingType.SPEED) {
            double dodgeChance = plugin.getConfigManager().getDouble("abilities.speed.swift-reflexes.dodge-chance", 0.15);
            if (random.nextDouble() < dodgeChance) {
                event.setCancelled(true);
                player.sendMessage("§b§l✦ DODGED!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        
        Player attacker = (Player) event.getDamager();
        
        if (attacker.getAttackCooldown() < 0.95F) {
            return;
        }
        
        PlayerData data = plugin.getDataManager().getPlayerData(attacker.getUniqueId());
        
        if (data.getBlessing() == BlessingType.STRENGTH) {
            int duration = plugin.getConfigManager().getInt("abilities.strength.warrior-rage.hit-duration", 60);
            int amplifier = plugin.getConfigManager().getInt("abilities.strength.warrior-rage.hit-strength", 2) - 1;
            
            com.cryptomorin.xseries.XPotion.matchXPotion("INCREASE_DAMAGE")
                .map(xp -> xp.buildPotionEffect(duration, amplifier))
                .ifPresent(attacker::addPotionEffect);
        }
        
        if (data.getBlessing() == BlessingType.THUNDER) {
            if (random.nextDouble() < 0.2) {
                event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
            }
        }
    }
}