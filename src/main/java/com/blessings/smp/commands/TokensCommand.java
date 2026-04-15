package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public TokensCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        int tokens = plugin.getTokenManager().getTokens(player);
        
        player.sendMessage("§6§l✦ ᴛᴏᴋᴇɴꜱ ✦");
        player.sendMessage("§eYou have §f" + tokens + " §etokens");
        
        return true;
    }
}