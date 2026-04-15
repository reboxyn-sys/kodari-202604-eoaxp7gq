package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
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

public class ThunderAssertion implements Listener {
    private final BlessingsSMP plugin;
    private final Map<UUID, Long> activeAbilities;
    
    public ThunderAssertion(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.activeAbilities = new HashMap<>();
    }
    
    public void execute(Player player) {
        String abilityKey = "thunder-assertion";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("thunder", "thunder-assertion");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §eThunder's Assertion");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_LIGHTNING_BOLT_THUNDER").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("thunder", "thunder-assertion");
        activeAbilities.put(player.getUniqueId(), System.currentTimeMillis() + (duration * 50L));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                activeAbilities.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, duration);
        
        if (plugin.getConfigManager().particlesEnabled()) {
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks++ >= duration / 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    XParticle.of("FIREWORKS_SPARK").ifPresent(p -> 
                        player.getWorld().spawnParticle(p.get(), player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5)
                    );
                }
            }.runTaskTimer(plugin, 0L, 5L);
        }
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
        
        double multiplier = plugin.getConfigManager().getDouble("abilities.thunder.thunder-assertion.damage-multiplier", 2.0);
        event.setDamage(event.getDamage() * multiplier);
        
        double chainRadius = plugin.getConfigManager().getDouble("abilities.thunder.thunder-assertion.chain-radius", 5.0);
        double chainDamage = plugin.getConfigManager().getDouble("abilities.thunder.thunder-assertion.chain-damage", 6.0);
        
        for (Entity entity : event.getEntity().getNearbyEntities(chainRadius, chainRadius, chainRadius)) {
            if (entity instanceof LivingEntity && entity != event.getEntity() && entity != attacker) {
                LivingEntity target = (LivingEntity) entity;
                
                if (target instanceof Player && plugin.getTrustManager().isTrusted(attacker.getUniqueId(), target.getUniqueId())) {
                    continue;
                }
                
                target.getWorld().strikeLightningEffect(target.getLocation());
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.damage(chainDamage);
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }
}