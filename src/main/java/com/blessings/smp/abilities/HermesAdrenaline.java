package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HermesAdrenaline {
    private final BlessingsSMP plugin;
    
    public HermesAdrenaline(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "hermes-adrenaline";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("speed", "hermes-adrenaline");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §bHermes Adrenaline");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_LIGHTNING_BOLT_THUNDER").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("speed", "hermes-adrenaline");
        int speedLevel = plugin.getConfigManager().getInt("abilities.speed.hermes-adrenaline.speed-level", 4);
        int hasteLevel = plugin.getConfigManager().getInt("abilities.speed.hermes-adrenaline.haste-level", 3);
        
        XPotion.matchXPotion("SPEED").map(xp -> xp.buildPotionEffect(duration, speedLevel - 1)).ifPresent(player::addPotionEffect);
        XPotion.matchXPotion("FAST_DIGGING").map(xp -> xp.buildPotionEffect(duration, hasteLevel - 1)).ifPresent(player::addPotionEffect);
        
        if (plugin.getConfigManager().particlesEnabled()) {
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks++ >= duration / 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    XParticle.of("CRIT").ifPresent(p -> 
                        player.getWorld().spawnParticle(p.get(), player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1)
                    );
                }
            }.runTaskTimer(plugin, 0L, 5L);
        }
    }
}