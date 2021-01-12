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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {
    private final Main plugin;

    public CraftListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() != null){
            if(plugin.forbiddeninventories.contains(e.getClickedInventory().getType())) {
                if (NFCNote.isNFCNote(e.getCursor())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onShiftClick(InventoryClickEvent e) {
        if(plugin.forbiddeninventories.contains(e.getInventory().getType()) && e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && NFCNote.isNFCNote(e.getCurrentItem())) e.setCancelled(true);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        for(int i : e.getRawSlots()){
            if(i < e.getInventory().getSize() && NFCNote.isNFCNote(e.getOldCursor()) && plugin.forbiddeninventories.contains(e.getInventory().getType())){
                e.setCancelled(true);
                return;
            }
        }
    }

    // This prevents hoppers from putting NFCNotes inside disabled tables
    @EventHandler
    public void moveEvent(InventoryMoveItemEvent e){
        if(plugin.forbiddeninventories.contains(e.getDestination().getType()) && NFCNote.isNFCNote(e.getItem())) e.setCancelled(true);
    }

    // This event exists because players would still use the crafting table from their inventories.
    @EventHandler
    public void craftEvent(CraftItemEvent e){
        for(ItemStack i : e.getInventory()) {
            if(NFCNote.isNFCNote(i) && plugin.forbiddeninventories.contains(InventoryType.WORKBENCH)){
                e.setCancelled(true);
                return;
            }
        }
    }
}
