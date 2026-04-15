package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public ReloadCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blessings.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        plugin.reload();
        sender.sendMessage("§aBlessingsSMP configuration reloaded!");
        
        return true;
    }
}