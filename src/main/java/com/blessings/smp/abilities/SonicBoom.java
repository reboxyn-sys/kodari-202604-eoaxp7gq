package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SonicBoom {
    private final BlessingsSMP plugin;
    private final Set<UUID> chargingPlayers;
    
    public SonicBoom(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.chargingPlayers = new HashSet<>();
    }
    
    public void execute(Player player) {
        String abilityKey = "sonic-boom";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        if (chargingPlayers.contains(player.getUniqueId())) {
            releaseShockwave(player);
            return;
        }
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §bSonic Boom §7(Charging...)");
        chargingPlayers.add(player.getUniqueId());
        
        int maxChargeTime = plugin.getConfigManager().getInt("abilities.speed.sonic-boom.max-charge-time", 400);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (chargingPlayers.contains(player.getUniqueId())) {
                    releaseShockwave(player);
                }
            }
        }.runTaskLater(plugin, maxChargeTime);
    }
    
    private void releaseShockwave(Player player) {
        chargingPlayers.remove(player.getUniqueId());
        
        int cooldown = plugin.getConfigManager().getCooldown("speed", "sonic-boom");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "sonic-boom", cooldown);
        
        player.sendMessage("§b§l✦ SHOCKWAVE RELEASED!");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_GENERIC_EXPLODE").ifPresent(s -> s.play(player.getLocation(), 2.0f, 2.0f));
        }
        
        Location loc = player.getLocation();
        Vector direction = loc.getDirection().normalize();
        
        double damage = plugin.getConfigManager().getDamage("speed", "sonic-boom");
        
        for (int i = 0; i < 20; i++) {
            final int finalI = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location particleLoc = loc.clone().add(direction.clone().multiply(finalI));
                    
                    if (plugin.getConfigManager().particlesEnabled()) {
                        XParticle.of("CLOUD").ifPresent(p -> 
                            particleLoc.getWorld().spawnParticle(p.get(), particleLoc, 10, 0.5, 0.5, 0.5, 0.1)
                        );
                    }
                    
                    for (Entity entity : particleLoc.getWorld().getNearbyEntities(particleLoc, 2, 2, 2)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            LivingEntity target = (LivingEntity) entity;
                            
                            if (target instanceof Player && plugin.getTrustManager().isTrusted(player.getUniqueId(), target.getUniqueId())) {
                                continue;
                            }
                            
                            target.damage(damage);
                        }
                    }
                }
            }.runTaskLater(plugin, i);
        }
    }
}