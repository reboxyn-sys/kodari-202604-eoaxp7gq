package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class TrustCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public TrustCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUsage: /trust <add/remove/list> [player]");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("list")) {
            Set<UUID> trusted = plugin.getTrustManager().getTrustedPlayers(player.getUniqueId());
            
            if (trusted.isEmpty()) {
                player.sendMessage("§eYou haven't trusted anyone yet.");
                return true;
            }
            
            player.sendMessage("§6§l✦ TRUSTED PLAYERS ✦");
            for (UUID uuid : trusted) {
                Player trustedPlayer = Bukkit.getPlayer(uuid);
                if (trustedPlayer != null) {
                    player.sendMessage("§e- §f" + trustedPlayer.getName());
                }
            }
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage("§cUsage: /trust <add/remove> <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cYou can't trust yourself!");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("add")) {
            plugin.getTrustManager().addTrust(player.getUniqueId(), target.getUniqueId());
            player.sendMessage("§aYou now trust " + target.getName());
        } else if (args[0].equalsIgnoreCase("remove")) {
            plugin.getTrustManager().removeTrust(player.getUniqueId(), target.getUniqueId());
            player.sendMessage("§cYou no longer trust " + target.getName());
        } else {
            player.sendMessage("§cUsage: /trust <add/remove/list> [player]");
        }
        
        return true;
    }
}