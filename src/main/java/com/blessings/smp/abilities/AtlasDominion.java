package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AtlasDominion {
    private final BlessingsSMP plugin;
    
    public AtlasDominion(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "atlas-dominion";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("endurance", "atlas-dominion");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §7Atlas's Dominion");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_ENDER_DRAGON_GROWL").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("endurance", "atlas-dominion");
        int resistanceLevel = plugin.getConfigManager().getInt("abilities.endurance.atlas-dominion.resistance-level", 5);
        
        XPotion.matchXPotion("DAMAGE_RESISTANCE").map(xp -> xp.buildPotionEffect(duration, resistanceLevel - 1)).ifPresent(player::addPotionEffect);

        XAttribute.of("knockback_resistance").ifPresent(attr -> {
            AttributeInstance knockbackResistance = player.getAttribute(attr.get());
            if (knockbackResistance != null) {
                double originalValue = knockbackResistance.getBaseValue();
                knockbackResistance.setBaseValue(1.0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline() && knockbackResistance != null) {
                            knockbackResistance.setBaseValue(originalValue);
                        }
                    }
                }.runTaskLater(plugin, duration);
            }
        });
        
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
                        player.getWorld().spawnParticle(p.get(), player.getLocation(), 20, 1, 1, 1, 0.1)
                    );
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
}