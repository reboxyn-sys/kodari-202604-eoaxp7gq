package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {
    private final BlessingsSMP plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public DataManager(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        setupDataFile();
    }
    
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }
    
    public void loadData() {
        playerDataMap.clear();
        ConfigurationSection section = dataConfig.getConfigurationSection("players");
        if (section == null) return;
        
        for (String key : section.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            PlayerData data = new PlayerData(uuid);
            
            String blessingStr = section.getString(key + ".blessing");
            if (blessingStr != null && !blessingStr.equals("NONE")) {
                data.setBlessing(BlessingType.valueOf(blessingStr));
            }
            
            data.setTokens(section.getInt(key + ".tokens", 2));
            
            List<String> trustedList = section.getStringList(key + ".trusted");
            for (String trustedUUID : trustedList) {
                data.addTrusted(UUID.fromString(trustedUUID));
            }
            
            playerDataMap.put(uuid, data);
        }
        
        plugin.getLogger().info("Loaded data for " + playerDataMap.size() + " players");
    }
    
    public void saveData() {
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerData data = entry.getValue();
            String path = "players." + uuid.toString();
            
            dataConfig.set(path + ".blessing", data.getBlessing() != null ? data.getBlessing().name() : "NONE");
            dataConfig.set(path + ".tokens", data.getTokens());
            
            List<String> trustedList = new ArrayList<>();
            for (UUID trusted : data.getTrustedPlayers()) {
                trustedList.add(trusted.toString());
            }
            dataConfig.set(path + ".trusted", trustedList);
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}