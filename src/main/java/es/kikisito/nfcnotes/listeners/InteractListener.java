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
import es.kikisito.nfcnotes.commands.Deposit;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.events.DepositEvent;
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class InteractListener implements Listener {
    private final Main plugin;

    public InteractListener(Main plugin) {
        this.plugin = plugin;
    }

    // For some reason this event is being called twice... and the last one is always getItem() = null 🤨
    @EventHandler(priority = EventPriority.LOWEST)
    private void redeem(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // Check if the item is a LEGACY NFCNote (uses attributes instead of the persistent data container)
        if(NFCNote.isLegacyNFCNote(e.getItem()) && e.getAction().toString().startsWith("RIGHT_CLICK")) {
            this.convertLegacyNote(p, e.getItem());
            return;
        }

        // Check if the item is an NFCNote
        if(NFCNote.isNFCNote(this.plugin, e.getItem()) && e.getAction().toString().startsWith("RIGHT_CLICK")) {
            // Check if player is allowed to deposit money
            if (!p.hasPermission("nfcnotes.deposit.action.deposit") || !NFCConfig.MODULES_DEPOSIT_ACTION.getBoolean()) return;
            else if(NFCConfig.DISABLED_WORLDS.getList().contains(p.getWorld().getName()) && !p.hasPermission("nfcnotes.staff.deposit.bypass.disabled-world")){
                p.sendMessage(NFCMessages.DISABLED_WORLD.getString());
                return;
            }

            // Parse note value to a formatted string
            DecimalFormat decimalFormat;
            if(NFCConfig.USE_EUROPEAN_FORMAT.getBoolean()) {
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);
                decimalFormatSymbols.setDecimalSeparator(',');
                decimalFormatSymbols.setGroupingSeparator('.');
                decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString(), decimalFormatSymbols);
            } else decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString());
            decimalFormat.setMaximumFractionDigits(2);

            // Redeem note
            double totalAmount = 0;
            // Mass Deposit
            // Checks if a player is sneaking, the submodule is enabled and if the player is allowed to mass-deposit.
            if (p.isSneaking() && p.hasPermission("nfcnotes.deposit.massdeposit") && NFCConfig.MODULES_MASSDEPOSIT.getBoolean()) {
                List<ItemStack> notes = new ArrayList<>();
                double value = 0;
                // Checks for notes in player's inventory
                for (ItemStack i : e.getPlayer().getInventory()) {
                    if (NFCNote.isNFCNote(this.plugin, i)) {
                        NFCNote nfcNote = new NFCNote(this.plugin, i);
                        double amount = nfcNote.getValue() * i.getAmount();
                        value = value + amount;
                        notes.add(i);
                    }
                }

                // Calls DepositEvent
                DepositEvent depositEvent = new DepositEvent(p, value, ActionMethod.SHIFT_RIGHT_CLICK);
                plugin.getServer().getPluginManager().callEvent(depositEvent);

                // Get variables from called event
                Player player = depositEvent.getPlayer();
                double money = depositEvent.getMoney();
                String formattedMoney = decimalFormat.format(money);

                // Deposit money if the event wasn't cancelled
                if (!depositEvent.isCancelled()) {
                    if (Utils.depositSuccessful(plugin, player, money)) {
                        for (ItemStack i : notes) i.setAmount(0);
                        player.sendMessage(NFCMessages.MASSDEPOSIT_SUCCESSFUL.getString().replace("{money}", formattedMoney));

                        // If enabled, play sound
                        playRedeemSound(player);
                        totalAmount = money;
                    } else {
                        player.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                    }
                }
            } else {
                // Deposit
                NFCNote nfcNote = new NFCNote(this.plugin, e.getItem());
                double m = nfcNote.getValue();
                // Calls DepositEvent
                DepositEvent depositEvent = new DepositEvent(p, m, ActionMethod.RIGHT_CLICK);
                plugin.getServer().getPluginManager().callEvent(depositEvent);
                // Deposit money if the event wasn't cancelled
                if (!depositEvent.isCancelled()) {
                    // Get variables from called event
                    Player player = depositEvent.getPlayer();
                    double money = depositEvent.getMoney();
                    String formattedMoney = decimalFormat.format(money);
                    if (Utils.depositSuccessful(plugin, player, money)) {
                        player.sendMessage(NFCMessages.DEPOSIT_SUCCESSFUL.getString().replace("{money}", formattedMoney));

                        // If enabled, play sound
                        playRedeemSound(player);

                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                        totalAmount = money;
                    } else {
                        player.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                    }
                }
            }
            // Warn staff if the note's value is higher than the specified in the configuration file
            if (totalAmount >= NFCConfig.WARN_VALUE_LIMIT.getInt() && NFCConfig.MODULES_WARN_STAFF.getBoolean()) {
                String formattedMoney = decimalFormat.format(totalAmount);
                for (Player pl : plugin.getServer().getOnlinePlayers()) {
                    if (pl.hasPermission("nfcnotes.staff.warn") && e.getPlayer() != pl) {
                        pl.sendMessage(NFCMessages.STAFF_WARN_DEPOSIT.getString().replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                        plugin.getLogger().info(NFCMessages.STAFF_WARN_DEPOSIT.getString().replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                    }
                }
            }
        }
    }

    private void playRedeemSound(Player player) {
        Deposit.playRedeemSound(player);
    }

    @SuppressWarnings("all")
    private void convertLegacyNote(Player p, ItemStack note) {
        // Legacy note, let's convert it to a modern note
        double money = note.getItemMeta().getAttributeModifiers(Attribute.LUCK).iterator().next().getAmount();

        ItemMeta itemMeta = new ItemStack(note.getType()).getItemMeta();
        // Store note values into the PDC
        NamespacedKey noteIdentifier = new NamespacedKey(plugin, "noteIdentifier");
        NamespacedKey noteValue = new NamespacedKey(plugin, "noteValue");
        itemMeta.getPersistentDataContainer().set(noteIdentifier, PersistentDataType.STRING, NFCConfig.NOTE_UUID.getString());
        itemMeta.getPersistentDataContainer().set(noteValue, PersistentDataType.DOUBLE, money);

        // Add display name, lore and Custom Model Data identifier
        itemMeta.setDisplayName(note.getItemMeta().getDisplayName());
        itemMeta.setLore(note.getItemMeta().getLore());
        itemMeta.setCustomModelData(note.getItemMeta().getCustomModelData());

        // Add previous enchants
        for(Map.Entry<Enchantment, Integer> enchant : note.getEnchantments().entrySet()) {
            itemMeta.addEnchant(enchant.getKey(), enchant.getValue(), true);
        }

        // Add previous flags
        for(ItemFlag flag : note.getItemMeta().getItemFlags()) {
            itemMeta.addItemFlags(flag);
        }

        // Set the new data to the itemstack
        note.setItemMeta(itemMeta);
        p.sendMessage(NFCMessages.NOTE_CONVERTED.getString());
    }
}