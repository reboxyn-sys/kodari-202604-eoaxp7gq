package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SMPCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public SMPCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blessings.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0 || !args[0].equalsIgnoreCase("start")) {
            sender.sendMessage("§cUsage: /smp start");
            return true;
        }
        
        if (plugin.getConfigManager().isSMPStarted()) {
            sender.sendMessage("§cSMP has already started!");
            return true;
        }
        
        plugin.getConfigManager().setSMPStarted(true);
        
        Bukkit.broadcastMessage("§6§l✦ ʙʟᴇꜱꜱɪɴɢꜱ ꜱᴍᴘ ✦");
        Bukkit.broadcastMessage("§ePhase 1 - Start");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getBlessingManager().assignRandomBlessing(player);
        }
        
        return true;
    }
}