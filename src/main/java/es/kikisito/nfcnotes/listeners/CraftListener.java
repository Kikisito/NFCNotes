/*
 * Copyright (C) 2020  Kikisito (Kyllian)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.kikisito.nfcnotes.listeners;

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.NFCNote;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {
    private Main plugin;
    private Configuration config;

    public CraftListener(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /*@EventHandler
    public void onCraft(InventoryClickEvent e) {
        e.getWhoClicked().sendMessage(e.getAction().toString());
        List<Object> forbiddeninventories = new ArrayList<>();
        for(String invtype : config.getStringList("disable-tables")){
            forbiddeninventories.add(InventoryType.valueOf(invtype));
        }
        if(e.getClickedInventory() != null){
            if(forbiddeninventories.contains(e.getClickedInventory().getType())) {
                if (NFCNote.isNFCNote(e.getCursor())) {
                    e.setCancelled(true);
                }
            }
        }
    }*/

    // Crafting table
    @EventHandler
    public void craftEvent(CraftItemEvent e){
        for(ItemStack i : e.getInventory()) {
            if(NFCNote.isNFCNote(i)){
                e.setCancelled(true);
                return;
            }
        }
    }

    // Furnace
    @EventHandler
    public void smeltEvent(FurnaceSmeltEvent e){
        ItemStack i = new ItemStack(e.getSource());
        if(NFCNote.isNFCNote(i)) {
            i.setAmount(1);
            e.setResult(i);
        }
    }

    @EventHandler
    public void burnEvent(FurnaceBurnEvent e){
        if(NFCNote.isNFCNote(e.getFuel())) {
            e.setCancelled(true);
        }
    }

    // Brewing Stand
    @EventHandler
    public void brewEvent(BrewEvent e){
        for(ItemStack i : e.getContents()) {
            if(NFCNote.isNFCNote(i)){
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void brewFuelEvent(BrewingStandFuelEvent e){
        if(NFCNote.isNFCNote(e.getFuel())) {
            e.setCancelled(true);
        }
    }

    // Anvil
    @EventHandler
    public void anvilEvent(PrepareAnvilEvent e){
        for(ItemStack i : e.getInventory()) {
            if(NFCNote.isNFCNote(i)){
                e.setResult(null);
                return;
            }
        }
    }

    // Enchanting Table
    @EventHandler
    public void enchantEvent(EnchantItemEvent e){
        for(ItemStack i : e.getInventory()) {
            if(NFCNote.isNFCNote(i)){
                e.setCancelled(true);
                return;
            }
        }
    }
}
