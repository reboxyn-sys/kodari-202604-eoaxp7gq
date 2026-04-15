package com.blessings.smp.data;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class PlayerData {
    private UUID uuid;
    private BlessingType blessing;
    private int tokens;
    private Set<UUID> trustedPlayers;
    private long lastMaxHealthUpdate;
    private boolean hasAncientChestplate;
    
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.tokens = 2;
        this.trustedPlayers = new HashSet<>();
        this.lastMaxHealthUpdate = 0L;
        this.hasAncientChestplate = false;
    }
    
    public void addTrusted(UUID player) {
        trustedPlayers.add(player);
    }
    
    public void removeTrusted(UUID player) {
        trustedPlayers.remove(player);
    }
    
    public boolean isTrusted(UUID player) {
        return trustedPlayers.contains(player);
    }
}