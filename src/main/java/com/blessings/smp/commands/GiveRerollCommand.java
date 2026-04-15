package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveRerollCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public GiveRerollCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blessings.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /givereroll <player> <amount>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return true;
        }
        
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive!");
            return true;
        }
        
        for (int i = 0; i < amount; i++) {
            ItemStack reroll = plugin.getMythicItemManager().createRerollItem();
            if (reroll != null) {
                target.getInventory().addItem(reroll);
            }
        }
        
        sender.sendMessage("§aGave " + target.getName() + " " + amount + " reroll item(s)!");
        target.sendMessage("§aYou received " + amount + " reroll item(s)!");
        
        return true;
    }
}