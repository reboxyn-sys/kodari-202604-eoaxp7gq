package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorEffectsManager {
    private final BlessingsSMP plugin;
    private final Map<UUID, BukkitTask> armorTasks;
    private final Map<UUID, Integer> tickCounters;
    
    public ArmorEffectsManager(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.armorTasks = new HashMap<>();
        this.tickCounters = new HashMap<>();
    }
    
    public void startArmorEffectTask(Player player) {
        stopArmorEffectTask(player.getUniqueId());
        tickCounters.put(player.getUniqueId(), 0);
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                stopArmorEffectTask(player.getUniqueId());
                return;
            }
            
            checkAndApplyArmorEffects(player);
            
            int tickCount = tickCounters.getOrDefault(player.getUniqueId(), 0);
            tickCount++;
            tickCounters.put(player.getUniqueId(), tickCount);
            
            if (tickCount % 4 == 0) {
                PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
                if (data.getBlessing() != null) {
                    plugin.getBlessingManager().applyPassiveEffects(player, data.getBlessing());
                }
            }
        }, 0L, 20L);
        
        armorTasks.put(player.getUniqueId(), task);
    }
    
    public void stopArmorEffectTask(UUID uuid) {
        BukkitTask task = armorTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        tickCounters.remove(uuid);
    }
    
    public void removeAllArmorEffects(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        XPotion.matchXPotion("WATER_BREATHING").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        XPotion.matchXPotion("DOLPHINS_GRACE").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        
        if (data.isHasAncientChestplate()) {
            double currentHealth = player.getHealth();
            player.setMaxHealth(20.0);
            if (currentHealth > 20.0) {
                player.setHealth(20.0);
            }
            data.setHasAncientChestplate(false);
        }
        
        XPotion.matchXPotion("SPEED").ifPresent(xp -> {
            player.removePotionEffect(xp.getPotionEffectType());
            reapplyBlessingSpeed(player, data);
        });
        
        XPotion.matchXPotion("DAMAGE_RESISTANCE").ifPresent(xp -> {
            player.removePotionEffect(xp.getPotionEffectType());
            reapplyBlessingResistance(player, data);
        });
    }
    
    private void checkAndApplyArmorEffects(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        
        boolean hasHelmet = plugin.getMythicItemManager().isAncientHelmet(helmet);
        boolean hasChestplate = plugin.getMythicItemManager().isAncientChestplate(chestplate);
        boolean hasLeggings = plugin.getMythicItemManager().isAncientLeggings(leggings);
        boolean hasBoots = plugin.getMythicItemManager().isAncientBoots(boots);
        
        if (hasHelmet) {
            XPotion.matchXPotion("WATER_BREATHING").map(xp -> xp.buildPotionEffect(80, 0)).ifPresent(player::addPotionEffect);
            XPotion.matchXPotion("DAMAGE_RESISTANCE").map(xp -> xp.buildPotionEffect(80, 0)).ifPresent(player::addPotionEffect);
        } else {
            XPotion.matchXPotion("WATER_BREATHING").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
            if (!hasChestplate) {
                XPotion.matchXPotion("DAMAGE_RESISTANCE").ifPresent(xp -> {
                    player.removePotionEffect(xp.getPotionEffectType());
                    reapplyBlessingResistance(player, data);
                });
            }
        }
        
        if (hasChestplate) {
            if (!data.isHasAncientChestplate()) {
                player.setMaxHealth(40.0);
                double currentHealth = player.getHealth();
                if (currentHealth < 40.0) {
                    player.setHealth(Math.min(currentHealth + 20.0, 40.0));
                }
                data.setHasAncientChestplate(true);
            }
        } else {
            if (data.isHasAncientChestplate()) {
                double currentHealth = player.getHealth();
                player.setMaxHealth(20.0);
                if (currentHealth > 20.0) {
                    player.setHealth(20.0);
                }
                data.setHasAncientChestplate(false);
            }
        }
        
        if (hasLeggings) {
            XPotion.matchXPotion("SPEED").map(xp -> xp.buildPotionEffect(80, 1)).ifPresent(player::addPotionEffect);
        } else {
            XPotion.matchXPotion("SPEED").ifPresent(xp -> {
                player.removePotionEffect(xp.getPotionEffectType());
                reapplyBlessingSpeed(player, data);
            });
        }
        
        if (hasBoots) {
            XPotion.matchXPotion("DOLPHINS_GRACE").map(xp -> xp.buildPotionEffect(80, 0)).ifPresent(player::addPotionEffect);
        } else {
            XPotion.matchXPotion("DOLPHINS_GRACE").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        }
    }
    
    private void reapplyBlessingSpeed(Player player, PlayerData data) {
        if (data.getBlessing() == BlessingType.SPEED) {
            XPotion.matchXPotion("SPEED").map(xp -> xp.buildPotionEffect(80, 1)).ifPresent(player::addPotionEffect);
        } else if (data.getBlessing() == BlessingType.THUNDER) {
            XPotion.matchXPotion("SPEED").map(xp -> xp.buildPotionEffect(80, 0)).ifPresent(player::addPotionEffect);
        }
    }
    
    private void reapplyBlessingResistance(Player player, PlayerData data) {
        if (data.getBlessing() == BlessingType.ENDURANCE) {
            XPotion.matchXPotion("DAMAGE_RESISTANCE").map(xp -> xp.buildPotionEffect(80, 0)).ifPresent(player::addPotionEffect);
        }
    }
}