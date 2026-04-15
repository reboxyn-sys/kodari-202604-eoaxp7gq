package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlessingManager {
    private final BlessingsSMP plugin;
    private final Random random;
    
    public BlessingManager(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    public void assignRandomBlessing(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        BlessingType currentBlessing = data.getBlessing();
        
        List<BlessingType> available = new ArrayList<>();
        for (BlessingType type : BlessingType.values()) {
            if (type != currentBlessing) {
                available.add(type);
            }
        }
        
        if (available.isEmpty()) {
            available.add(BlessingType.STRENGTH);
        }
        
        BlessingType newBlessing = available.get(random.nextInt(available.size()));
        setBlessing(player, newBlessing, true);
    }
    
    public void setBlessing(Player player, BlessingType blessing, boolean announce) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        removeOldBlessingEffects(player, data.getBlessing());
        
        data.setBlessing(blessing);
        
        applyPassiveEffects(player, blessing);
        
        if (announce) {
            String blessingColor = getBlessingColor(blessing);
            player.sendTitle("§c§lBlessed By", blessingColor + formatBlessingName(blessing), 10, 70, 20);
            player.sendMessage("§c§l✦ ʙʟᴇꜱꜱᴇᴅ ʙʏ ✦");
            player.sendMessage("§eʙʟᴇꜱꜱɪɴɢ: " + blessingColor + formatBlessingName(blessing));
            
            if (plugin.getConfigManager().soundsEnabled()) {
                playSoundForBlessing(player);
            }
        }
        
        plugin.getConfigManager().log("Player " + player.getName() + " received blessing: " + blessing);
    }
    
    private void removeOldBlessingEffects(Player player, BlessingType oldBlessing) {
        if (oldBlessing == null) return;
        
        XPotion.matchXPotion("INCREASE_DAMAGE").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        XPotion.matchXPotion("DAMAGE_RESISTANCE").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        XPotion.matchXPotion("SPEED").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
        XPotion.matchXPotion("REGENERATION").ifPresent(xp -> player.removePotionEffect(xp.getPotionEffectType()));
    }
    
    public void applyPassiveEffects(Player player, BlessingType blessing) {
        switch (blessing) {
            case STRENGTH:
                applyPotionEffect(player, "INCREASE_DAMAGE", 0, 80);
                break;
            case ENDURANCE:
                applyPotionEffect(player, "DAMAGE_RESISTANCE", 0, 80);
                break;
            case SPEED:
                applyPotionEffect(player, "SPEED", 1, 80);
                break;
            case LIFE:
                applyPotionEffect(player, "REGENERATION", 1, 80);
                break;
            case THUNDER:
                applyPotionEffect(player, "SPEED", 0, 80);
                break;
        }
    }
    
    private void applyPotionEffect(Player player, String effectName, int amplifier, int duration) {
        XPotion.matchXPotion(effectName)
            .map(xp -> xp.buildPotionEffect(duration, amplifier))
            .ifPresent(player::addPotionEffect);
    }
    
    private String formatBlessingName(BlessingType type) {
        String name = type.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    private String getBlessingColor(BlessingType type) {
        switch (type) {
            case THUNDER:
                return "§e";
            case STRENGTH:
                return "§4";
            case SPEED:
                return "§9";
            case ENDURANCE:
                return "§7";
            case LIFE:
                return "§d";
            default:
                return "§f";
        }
    }
    
    private void playSoundForBlessing(Player player) {
        com.cryptomorin.xseries.XSound.matchXSound("BLOCK_BEACON_ACTIVATE")
            .ifPresent(sound -> sound.play(player));
    }
    
    public BlessingType getPlayerBlessing(Player player) {
        return plugin.getDataManager().getPlayerData(player.getUniqueId()).getBlessing();
    }
}