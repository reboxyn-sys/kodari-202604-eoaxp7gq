package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WithdrawCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public WithdrawCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 1) {
            player.sendMessage("§cUsage: /withdraw <amount>");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount!");
            return true;
        }
        
        if (amount != 1) {
            player.sendMessage("§cYou can only withdraw 1 token at a time!");
            return true;
        }
        
        int currentTokens = plugin.getTokenManager().getTokens(player);
        int minTokens = plugin.getConfigManager().getMinTokens();
        
        if (currentTokens - amount < minTokens) {
            player.sendMessage("§cYou cannot withdraw that many tokens! (Minimum: " + minTokens + ")");
            return true;
        }
        
        plugin.getTokenManager().removeTokens(player, amount);
        
        ItemStack tokenItem = plugin.getTokenManager().createTokenItem(amount);
        if (tokenItem != null) {
            player.getInventory().addItem(tokenItem);
        }
        
        return true;
    }
}