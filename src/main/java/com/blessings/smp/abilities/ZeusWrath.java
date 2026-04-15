package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZeusWrath implements Listener {
    private final BlessingsSMP plugin;
    private final Map<UUID, Long> activeAbilities;
    
    public ZeusWrath(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.activeAbilities = new HashMap<>();
    }
    
    public void execute(Player player) {
        String abilityKey = "zeus-wrath";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("thunder", "zeus-wrath");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §eZeus's Wrath");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_LIGHTNING_BOLT_THUNDER").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("thunder", "zeus-wrath");
        activeAbilities.put(player.getUniqueId(), System.currentTimeMillis() + (duration * 50L));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                activeAbilities.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, duration);
        
        double radius = plugin.getConfigManager().getDouble("abilities.thunder.zeus-wrath.strike-radius", 15.0);
        int interval = plugin.getConfigManager().getInt("abilities.thunder.zeus-wrath.strike-interval", 20);
        double damage = plugin.getConfigManager().getDamage("thunder", "zeus-wrath");
        
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        
                        if (target instanceof Player && plugin.getTrustManager().isTrusted(player.getUniqueId(), target.getUniqueId())) {
                            continue;
                        }
                        
                        if (Math.random() < 0.3) {
                            Location strikeLoc = target.getLocation();
                            strikeLoc.getWorld().strikeLightningEffect(strikeLoc);
                            
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    target.damage(damage);
                                }
                            }.runTaskLater(plugin, 1L);
                        }
                    }
                }
                
                ticks += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player attacker = (Player) event.getDamager();
        
        if (attacker.getAttackCooldown() < 0.95F) {
            return;
        }
        
        Long expiry = activeAbilities.get(attacker.getUniqueId());
        if (expiry == null || System.currentTimeMillis() > expiry) return;
        
        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
    }
}