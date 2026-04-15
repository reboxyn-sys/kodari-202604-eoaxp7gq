package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PridefulSlam {
    private final BlessingsSMP plugin;
    
    public PridefulSlam(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "prideful-slam";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("strength", "prideful-slam");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §cPrideful Slam");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_ENDER_DRAGON_GROWL").ifPresent(s -> s.play(player));
        }
        
        int height = plugin.getConfigManager().getInt("abilities.strength.prideful-slam.launch-height", 20);
        player.setVelocity(new Vector(0, height * 0.15, 0));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnGround() || !player.isValid()) {
                    performSlam(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 10L, 2L);
    }
    
    private void performSlam(Player player) {
        Location loc = player.getLocation();
        
        if (plugin.getConfigManager().particlesEnabled()) {
            XParticle.of("EXPLOSION_LARGE").ifPresent(p -> 
                loc.getWorld().spawnParticle(p.get(), loc, 10, 2, 0.5, 2)
            );
        }
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_GENERIC_EXPLODE").ifPresent(s -> s.play(player.getLocation(), 2.0f, 0.8f));
        }
        
        double damage = plugin.getConfigManager().getDamage("strength", "prideful-slam");
        int duration = plugin.getConfigManager().getInt("abilities.strength.prideful-slam.slowness-duration", 400);
        
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;
                
                if (target instanceof Player && plugin.getTrustManager().isTrusted(player.getUniqueId(), target.getUniqueId())) {
                    continue;
                }
                
                target.damage(damage);
                
                Vector knockback = target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2);
                knockback.setY(0.5);
                target.setVelocity(knockback);
                
                XPotion.matchXPotion("SLOW").map(xp -> xp.buildPotionEffect(duration, 1)).ifPresent(target::addPotionEffect);
            }
        }
        
        player.setFallDistance(0);
    }
}