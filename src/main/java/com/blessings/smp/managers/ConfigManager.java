package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final BlessingsSMP plugin;
    private final FileConfiguration config;
    
    public ConfigManager(BlessingsSMP plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }
    
    public boolean isSMPStarted() {
        return config.getBoolean("smp-started", false);
    }
    
    public void setSMPStarted(boolean started) {
        config.set("smp-started", started);
        plugin.saveConfig();
    }
    
    public int getStartingTokens() {
        return config.getInt("starting-tokens", 2);
    }
    
    public int getMaxTokens() {
        return config.getInt("max-tokens", 5);
    }
    
    public int getMinTokens() {
        return config.getInt("min-tokens", -3);
    }
    
    public int getTokenKillReward() {
        return config.getInt("token-kill-reward", 1);
    }
    
    public int getTokenDeathPenalty() {
        return config.getInt("token-death-penalty", 1);
    }
    
    public int getCooldown(String blessing, String ability) {
        return config.getInt("abilities." + blessing + "." + ability + ".cooldown", 60);
    }
    
    public int getDuration(String blessing, String ability) {
        return config.getInt("abilities." + blessing + "." + ability + ".duration", 100);
    }
    
    public double getDamage(String blessing, String ability) {
        return config.getDouble("abilities." + blessing + "." + ability + ".damage", 5.0);
    }
    
    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }
    
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }
    
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
    
    public String getString(String path, String def) {
        return config.getString(path, def);
    }
    
    public boolean particlesEnabled() {
        return config.getBoolean("particles.enabled", true);
    }
    
    public boolean soundsEnabled() {
        return config.getBoolean("sounds.enabled", true);
    }
    
    public void log(String message) {
        if (isDebug()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
}