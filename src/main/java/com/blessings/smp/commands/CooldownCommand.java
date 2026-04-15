package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CooldownCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public CooldownCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blessings.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        plugin.getCooldownManager().clearCooldowns(player.getUniqueId());
        player.sendMessage("§aAll cooldowns cleared!");
        
        return true;
    }
}