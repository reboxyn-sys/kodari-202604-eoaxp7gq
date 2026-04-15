package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.PlayerData;

import java.util.Set;
import java.util.UUID;

public class TrustManager {
    private final BlessingsSMP plugin;
    
    public TrustManager(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public void addTrust(UUID player, UUID trusted) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.addTrusted(trusted);
    }
    
    public void removeTrust(UUID player, UUID trusted) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.removeTrusted(trusted);
    }
    
    public boolean isTrusted(UUID player, UUID target) {
        if (player.equals(target)) return true;
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        return data.isTrusted(target);
    }
    
    public Set<UUID> getTrustedPlayers(UUID player) {
        return plugin.getDataManager().getPlayerData(player).getTrustedPlayers();
    }
}