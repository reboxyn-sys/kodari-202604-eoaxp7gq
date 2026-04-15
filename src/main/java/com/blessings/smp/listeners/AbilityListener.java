package com.blessings.smp.listeners;

import com.blessings.smp.BlessingsSMP;
import com.blessings.smp.abilities.*;
import com.blessings.smp.data.BlessingType;
import com.blessings.smp.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class AbilityListener implements Listener {
    private final BlessingsSMP plugin;
    
    public AbilityListener(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        if (data.getBlessing() == null) return;
        
        event.setCancelled(true);
        
        boolean isShifting = player.isSneaking();
        
        switch (data.getBlessing()) {
            case STRENGTH:
                if (isShifting) {
                    new KratosIntervention(plugin).execute(player);
                } else {
                    new PridefulSlam(plugin).execute(player);
                }
                break;
            case ENDURANCE:
                if (isShifting) {
                    new AtlasDominion(plugin).execute(player);
                } else {
                    new UnwaveringEndurance(plugin).execute(player);
                }
                break;
            case SPEED:
                if (isShifting) {
                    new HermesAdrenaline(plugin).execute(player);
                } else {
                    new SonicBoom(plugin).execute(player);
                }
                break;
            case LIFE:
                if (isShifting) {
                    new GaiaProduce(plugin).execute(player);
                } else {
                    new LifeDrain(plugin).execute(player);
                }
                break;
            case THUNDER:
                if (isShifting) {
                    new ThunderAssertion(plugin).execute(player);
                } else {
                    new ZeusWrath(plugin).execute(player);
                }
                break;
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getMythicItemManager().isRerollItem(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
            
            if (data.getBlessing() == null) {
                player.sendMessage("§cYou don't have a blessing to reroll!");
                return;
            }
            
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            plugin.getBlessingManager().assignRandomBlessing(player);
        }
        
        if (plugin.getTokenManager().isTokenItem(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            plugin.getTokenManager().addTokens(player, 1);
        }
    }
}