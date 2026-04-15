package com.blessings.smp.managers;

import com.blessings.smp.BlessingsSMP;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MythicItemManager {
    private final BlessingsSMP plugin;
    
    public MythicItemManager(BlessingsSMP plugin) {
        this.plugin = plugin;
    }
    
    public ItemStack createZeusThunderbolt() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_SWORD").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§lZeus's Thunderbolt");
            meta.setLore(Arrays.asList(
                "§7Every 3 hits strikes lightning",
                "§6§lMYTHICAL"
            ));
            XEnchantment.matchXEnchantment("DAMAGE_ALL").ifPresent(e -> meta.addEnchant(e.getEnchant(), 5, true));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createAncientAxe() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_AXE").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§lAncient Axe");
            meta.setLore(Arrays.asList(
                "§730% chance to freeze enemies",
                "§7Freezes on 10th consecutive hit",
                "§6§lMYTHICAL"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createAncientHelmet() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_HELMET").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lAncient Helmet");
            meta.setLore(Arrays.asList(
                "§7Water Breathing",
                "§7Resistance I",
                "§6§lMYTHICAL"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createAncientChestplate() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_CHESTPLATE").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lAncient Chestplate");
            meta.setLore(Arrays.asList(
                "§7+10 Hearts",
                "§6§lMYTHICAL"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createAncientLeggings() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_LEGGINGS").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lAncient Leggings");
            meta.setLore(Arrays.asList(
                "§7Speed II",
                "§6§lMYTHICAL"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createAncientBoots() {
        ItemStack item = XMaterial.matchXMaterial("NETHERITE_BOOTS").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lAncient Boots");
            meta.setLore(Arrays.asList(
                "§7No Fall Damage",
                "§7Dolphin's Grace",
                "§6§lMYTHICAL"
            ));
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createRerollItem() {
        ItemStack item = XMaterial.matchXMaterial("NETHER_STAR").map(XMaterial::parseItem).orElse(null);
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lReroller");
            meta.setLore(Arrays.asList(
                "§7Right-click to reroll your blessing",
                "§cTokens are preserved"
            ));
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public boolean isZeusThunderbolt(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Zeus's Thunderbolt");
    }
    
    public boolean isAncientAxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Ancient Axe");
    }
    
    public boolean isAncientHelmet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Ancient Helmet");
    }
    
    public boolean isAncientChestplate(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Ancient Chestplate");
    }
    
    public boolean isAncientLeggings(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Ancient Leggings");
    }
    
    public boolean isAncientBoots(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Ancient Boots");
    }
    
    public boolean isRerollItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().contains("Reroller");
    }
}