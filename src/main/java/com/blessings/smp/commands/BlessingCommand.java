package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlessingCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public BlessingCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("blessings.admin")) {
                sender.sendMessage("§cYou don't have permission!");
                return true;
            }
            
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /blessing give <player> <blessing>");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
            
            BlessingType blessing;
            try {
                blessing = BlessingType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid blessing! Available: STRENGTH, ENDURANCE, SPEED, LIFE, THUNDER, TIME");
                return true;
            }
            
            plugin.getBlessingManager().setBlessing(target, blessing, true);
            sender.sendMessage("§aGave " + target.getName() + " the " + blessing.name().toLowerCase() + " blessing!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        if (data.getBlessing() == null) {
            player.sendMessage("§cYou don't have a blessing yet! Wait for the SMP to start.");
            return true;
        }
        
        player.sendMessage("§6§l✦ YOUR BLESSING ✦");
        player.sendMessage("§eBlessing: §f" + formatBlessing(data.getBlessing()));
        player.sendMessage("§eTokens: §f" + data.getTokens());
        
        return true;
    }
    
    private String formatBlessing(BlessingType type) {
        String name = type.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}