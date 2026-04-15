package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.PlayerData;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TokenManager {
    private final BlessingsSMP plugin;
    
    public TokenManager(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void addTokens(Player player, int amount) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        int newAmount = Math.min(data.getTokens() + amount, plugin.getConfigManager().getMaxTokens());
        data.setTokens(newAmount);
        
        player.sendMessage("§6§l✦ ᴛᴏᴋᴇɴ ✦ §e+" + amount);
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_PLAYER_LEVELUP").ifPresent(sound -> sound.play(player));
        }
        
        plugin.getConfigManager().log("Player " + player.getName() + " gained " + amount + " token(s). New total: " + newAmount);
    }
    
    public void removeTokens(Player player, int amount) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        int newAmount = Math.max(data.getTokens() - amount, plugin.getConfigManager().getMinTokens());
        data.setTokens(newAmount);
        
        if (plugin.getConfigManager().soundsEnabled()) {
            XSound.matchXSound("ENTITY_VILLAGER_NO").ifPresent(sound -> sound.play(player));
        }
        
        plugin.getConfigManager().log("Player " + player.getName() + " lost " + amount + " token(s). New total: " + newAmount);
    }
    
    public int getTokens(Player player) {
        return plugin.getDataManager().getPlayerData(player.getUniqueId()).getTokens();
    }
    
    public void setTokens(Player player, int amount) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        int clamped = Math.max(plugin.getConfigManager().getMinTokens(), 
                        Math.min(amount, plugin.getConfigManager().getMaxTokens()));
        data.setTokens(clamped);
    }
    
    public ItemStack createTokenItem(int amount) {
        ItemStack item = XMaterial.matchXMaterial("NETHER_STAR").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        item.setAmount(amount);
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l✦ Token ✦");
            meta.setLore(Arrays.asList(
                "§7Right-click to redeem",
                "§eValue: §f+1 token"
            ));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public boolean isTokenItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Token");
    }
}