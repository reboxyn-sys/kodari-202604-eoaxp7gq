package com.blessings.smp;

import com.blessings.smp.commands.*;
import com.blessings.smp.inventory.gui.GUIListener;
import com.blessings.smp.inventory.gui.GUIManager;
import com.blessings.smp.listeners.*;
import com.blessings.smp.managers.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BlessingsSMP extends JavaPlugin {
    
    private ConfigManager configManager;
    private DataManager dataManager;
    private BlessingManager blessingManager;
    private TokenManager tokenManager;
    private CooldownManager cooldownManager;
    private TrustManager trustManager;
    private MythicItemManager mythicItemManager;
    private GUIManager guiManager;
    private ArmorEffectsManager armorEffectsManager;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.blessingManager = new BlessingManager(this);
        this.tokenManager = new TokenManager(this);
        this.cooldownManager = new CooldownManager();
        this.trustManager = new TrustManager(this);
        this.mythicItemManager = new MythicItemManager(this);
        this.guiManager = new GUIManager();
        this.armorEffectsManager = new ArmorEffectsManager(this);
        
        dataManager.loadData();
        
        registerListeners();
        registerCommands();
        
        getLogger().info("BlessingsSMP has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveData();
        }
        
        if (armorEffectsManager != null) {
            Bukkit.getOnlinePlayers().forEach(player -> 
                armorEffectsManager.stopArmorEffectTask(player.getUniqueId())
            );
        }
        
        getLogger().info("BlessingsSMP has been disabled!");
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ArmorListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MythicItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(guiManager), this);
    }
    
    private void registerCommands() {
        getCommand("smp").setExecutor(new SMPCommand(this));
        getCommand("blessing").setExecutor(new BlessingCommand(this));
        getCommand("tokens").setExecutor(new TokensCommand(this));
        getCommand("trust").setExecutor(new TrustCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("givereroll").setExecutor(new GiveRerollCommand(this));
        getCommand("givetoken").setExecutor(new GiveTokenCommand(this));
        getCommand("givemythic").setExecutor(new GiveMythicCommand(this));
        getCommand("cooldown").setExecutor(new CooldownCommand(this));
        getCommand("blessingsreload").setExecutor(new ReloadCommand(this));
    }
    
    public void reload() {
        reloadConfig();
        configManager = new ConfigManager(this);
        dataManager.saveData();
        dataManager.loadData();
    }
}