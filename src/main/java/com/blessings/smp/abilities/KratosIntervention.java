package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KratosIntervention {
    private final BlessingsSMP plugin;
    
    public KratosIntervention(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "kratos-intervention";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("strength", "kratos-intervention");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §cKratos's Intervention");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_WITHER_SPAWN").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("strength", "kratos-intervention");
        int strengthLevel = plugin.getConfigManager().getInt("abilities.strength.kratos-intervention.strength-level", 3);
        int speedLevel = plugin.getConfigManager().getInt("abilities.strength.kratos-intervention.speed-level", 1);
        
        XPotion.matchXPotion("INCREASE_DAMAGE").map(xp -> xp.buildPotionEffect(duration, strengthLevel - 1)).ifPresent(player::addPotionEffect);
        XPotion.matchXPotion("SPEED").map(xp -> xp.buildPotionEffect(duration, speedLevel - 1)).ifPresent(player::addPotionEffect);
        
        if (plugin.getConfigManager().particlesEnabled()) {
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks++ >= duration / 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    XParticle.of("REDSTONE").ifPresent(p -> 
                        player.getWorld().spawnParticle(p.get(), player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5)
                    );
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
}