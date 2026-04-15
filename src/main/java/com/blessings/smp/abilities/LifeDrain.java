package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class LifeDrain {
    private final BlessingsSMP plugin;
    
    public LifeDrain(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "life-drain";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        double range = plugin.getConfigManager().getDouble("abilities.life.life-drain.range", 10.0);
        RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getLocation().getDirection(),
            range,
            entity -> entity instanceof LivingEntity && entity != player
        );
        
        if (result == null || !(result.getHitEntity() instanceof LivingEntity)) {
            player.sendMessage("§cNo target found!");
            return;
        }
        
        LivingEntity target = (LivingEntity) result.getHitEntity();
        
        if (target instanceof Player && plugin.getTrustManager().isTrusted(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage("§cCannot drain trusted player!");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("life", "life-drain");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §aLife Drain");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_WITHER_HURT").ifPresent(s -> s.play(player));
        }
        
        double minDamage = plugin.getConfigManager().getDouble("abilities.life.life-drain.min-damage", 4.0);
        double maxDamage = plugin.getConfigManager().getDouble("abilities.life.life-drain.max-damage", 12.0);
        double damage = minDamage + Math.random() * (maxDamage - minDamage);
        
        target.damage(damage);
        
        double healAmount = damage;
        if (player.getHealth() + healAmount > player.getMaxHealth()) {
            healAmount = player.getMaxHealth() - player.getHealth();
        }
        player.setHealth(player.getHealth() + healAmount);
        
        if (plugin.getConfigManager().particlesEnabled()) {
            Location start = target.getEyeLocation();
            Location end = player.getEyeLocation();
            
            double distance = start.distance(end);
            for (double i = 0; i < distance; i += 0.5) {
                double ratio = i / distance;
                Location particleLoc = start.clone().add(
                    end.toVector().subtract(start.toVector()).multiply(ratio)
                );
                XParticle.of("VILLAGER_HAPPY").ifPresent(p -> 
                    particleLoc.getWorld().spawnParticle(p.get(), particleLoc, 1, 0, 0, 0, 0)
                );
            }
        }
    }
}