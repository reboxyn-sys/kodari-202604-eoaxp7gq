package com.blessings.smp.commands;

import com.blessings.smp.BlessingsSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveMythicCommand implements CommandExecutor {
    private final BlessingsSMP plugin;
    
    public GiveMythicCommand(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blessings.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /givemythic <player> <item>");
            sender.sendMessage("§eItems: thunderbolt, axe, helmet, chestplate, leggings, boots");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        ItemStack item = null;
        String itemName = args[1].toLowerCase();
        
        switch (itemName) {
            case "thunderbolt":
                item = plugin.getMythicItemManager().createZeusThunderbolt();
                break;
            case "axe":
                item = plugin.getMythicItemManager().createAncientAxe();
                break;
            case "helmet":
                item = plugin.getMythicItemManager().createAncientHelmet();
                break;
            case "chestplate":
                item = plugin.getMythicItemManager().createAncientChestplate();
                break;
            case "leggings":
                item = plugin.getMythicItemManager().createAncientLeggings();
                break;
            case "boots":
                item = plugin.getMythicItemManager().createAncientBoots();
                break;
            default:
                sender.sendMessage("§cInvalid item! Available: thunderbolt, axe, helmet, chestplate, leggings, boots");
                return true;
        }
        
        if (item != null) {
            target.getInventory().addItem(item);
            sender.sendMessage("§aGave " + target.getName() + " " + itemName + "!");
            target.sendMessage("§aYou received a mythical item!");
        }
        
        return true;
    }
}