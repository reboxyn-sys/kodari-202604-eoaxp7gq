package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MythicItemListener implements Listener {
    private final BlessingsSMP plugin;
    private final Map<UUID, Integer> thunderboltHits;
    private final Map<UUID, Integer> axeHits;
    
    public MythicItemListener(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.thunderboltHits = new HashMap<>();
        this.axeHits = new HashMap<>();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Player attacker = (Player) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        
        if (attacker.getAttackCooldown() < 0.95F) {
            return;
        }
        
        if (plugin.getMythicItemManager().isZeusThunderbolt(weapon)) {
            handleThunderbolt(attacker, victim);
        }
        
        if (plugin.getMythicItemManager().isAncientAxe(weapon)) {
            handleAxe(attacker, victim);
        }
    }
    
    private void handleThunderbolt(Player attacker, LivingEntity victim) {
        int hits = thunderboltHits.getOrDefault(attacker.getUniqueId(), 0) + 1;
        int requiredHits = plugin.getConfigManager().getInt("mythic-items.zeus-thunderbolt.hit-count", 3);
        
        if (hits >= requiredHits) {
            thunderboltHits.put(attacker.getUniqueId(), 0);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (plugin.getTrustManager().isTrusted(attacker.getUniqueId(), victim.getUniqueId())) {
                        return;
                    }
                    
                    victim.getWorld().strikeLightningEffect(victim.getLocation());
                    double damage = plugin.getConfigManager().getDouble("mythic-items.zeus-thunderbolt.lightning-damage", 8.0);
                    victim.damage(damage);
                    
                    plugin.getConfigManager().log("Zeus's Thunderbolt lightning triggered by " + attacker.getName());
                }
            }.runTaskLater(plugin, 1L);
        } else {
            thunderboltHits.put(attacker.getUniqueId(), hits);
        }
    }
    
    private void handleAxe(Player attacker, LivingEntity victim) {
        int hits = axeHits.getOrDefault(attacker.getUniqueId(), 0) + 1;
        int requiredHits = plugin.getConfigManager().getInt("mythic-items.ancient-axe.freeze-hit-count", 10);
        double freezeChance = plugin.getConfigManager().getDouble("mythic-items.ancient-axe.freeze-chance", 0.30);
        
        if (hits >= requiredHits || Math.random() < freezeChance) {
            axeHits.put(attacker.getUniqueId(), 0);
            
            int duration = plugin.getConfigManager().getInt("mythic-items.ancient-axe.freeze-duration", 100);
            XPotion.matchXPotion("SLOW").map(xp -> xp.buildPotionEffect(duration, 10)).ifPresent(victim::addPotionEffect);
            XPotion.matchXPotion("JUMP").map(xp -> xp.buildPotionEffect(duration, 128)).ifPresent(victim::addPotionEffect);
            
            attacker.sendMessage("§b§l✦ FROZEN!");
            plugin.getConfigManager().log("Ancient Axe froze " + victim.getName());
        } else {
            axeHits.put(attacker.getUniqueId(), hits);
        }
    }
}