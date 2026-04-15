package com.blessings.smp.abilities;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GaiaProduce implements Listener {
    private final BlessingsSMP plugin;
    private final Set<UUID> activePlayers;
    
    public GaiaProduce(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.activePlayers = new HashSet<>();
    }
    
    public void execute(Player player) {
        String abilityKey = "gaia-produce";
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), abilityKey)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), abilityKey);
            player.sendMessage("§c✦ Cooldown: " + remaining + "s");
            return;
        }
        
        int cooldown = plugin.getConfigManager().getCooldown("life", "gaia-produce");
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), abilityKey, cooldown);
        
        player.sendMessage("§6§l✦ ʏᴏᴜ ᴜꜱᴇᴅ ✦ §aGaia's Produce");
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("BLOCK_GRASS_BREAK").ifPresent(s -> s.play(player));
        }
        
        int duration = plugin.getConfigManager().getDuration("life", "gaia-produce");
        activePlayers.add(player.getUniqueId());
        
        new BukkitRunnable() {
            @Override
            public void run() {
                activePlayers.remove(player.getUniqueId());
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
                    XParticle.of("VILLAGER_HAPPY").ifPresent(p -> 
                        player.getWorld().spawnParticle(p.get(), player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5)
                    );
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
    
    @EventHandler
    public void onFoodEat(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        
        if (!activePlayers.contains(player.getUniqueId())) return;
        
        if (event.getFoodLevel() > player.getFoodLevel()) {
            int absorptionBonus = plugin.getConfigManager().getInt("abilities.life.gaia-produce.absorption-bonus", 4);
            
            XPotion.matchXPotion("REGENERATION").map(xp -> xp.buildPotionEffect(200, 1)).ifPresent(player::addPotionEffect);
            XPotion.matchXPotion("ABSORPTION").map(xp -> xp.buildPotionEffect(2400, absorptionBonus - 1)).ifPresent(player::addPotionEffect);
            XPotion.matchXPotion("DAMAGE_RESISTANCE").map(xp -> xp.buildPotionEffect(6000, 0)).ifPresent(player::addPotionEffect);
            
            player.sendMessage("§a§l✦ ENCHANTED GOLDEN APPLE EFFECTS!");
        }
    }
}