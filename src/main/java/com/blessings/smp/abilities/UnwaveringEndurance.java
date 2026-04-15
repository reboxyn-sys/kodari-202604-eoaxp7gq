package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnwaveringEndurance {
    private final BlessingsSMP plugin;
    
    public UnwaveringEndurance(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void execute(Player player) {
        String abilityKey = "unwavering-endurance";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("endurance", "unwavering-endurance");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §7Unwavering Endurance");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("BLOCK_ANVIL_PLACE").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("endurance", "unwavering-endurance");
        int extraHearts = plugin.getConfigManager().getInt("abilities.endurance.unwavering-endurance.extra-hearts", 20);
        
        double originalMaxHealth = player.getMaxHealth();
        player.setMaxHealth(originalMaxHealth + extraHearts);
        player.setHealth(player.getMaxHealth());
        
        XPotion.matchXPotion("ABSORPTION").map(xp -> xp.buildPotionEffect(duration, 4)).ifPresent(player::addPotionEffect);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    double currentHealth = player.getHealth();
                    player.setMaxHealth(originalMaxHealth);
                    if (currentHealth > originalMaxHealth) {
                        player.setHealth(originalMaxHealth);
                    }
                }
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
                    XParticle.of("SMOKE_NORMAL").ifPresent(p -> 
                        player.getWorld().spawnParticle(p.get(), player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5)
                    );
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
}