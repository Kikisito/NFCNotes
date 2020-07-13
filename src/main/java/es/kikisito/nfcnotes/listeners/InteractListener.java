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
import es.kikisito.nfcnotes.Utils;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.events.DepositEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InteractListener implements Listener {
    private Main plugin;
    private Configuration config;
    private Economy eco;

    public InteractListener(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.eco = plugin.getEco();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void redeem(PlayerInteractEvent e) {
        FileConfiguration messages = plugin.getMessages();
        Player p = e.getPlayer();
        // Check if the item's material is Paper
        if (NFCNote.isNFCNote(e.getItem())) {
            // Check if player is allowed to deposit money
            if (!p.hasPermission("nfcnotes.deposit") || !config.getBoolean("modules.deposit.action")) return;
            DecimalFormat decimalFormat = new DecimalFormat(config.getString("notes.decimal-format"));
            double totalAmount = 0;
            // Mass Deposit
            // Checks if a player is sneaking, the submodule is enabled and if the player is allowed to mass-deposit.
            if (p.isSneaking() && p.hasPermission("nfcnotes.deposit.massdeposit") && config.getBoolean("modules.massdeposit")) {
                List<ItemStack> notes = new ArrayList<>();
                double value = 0;
                // Checks for notes in player's inventory
                for (ItemStack i : e.getPlayer().getInventory()) {
                    if (NFCNote.isNFCNote(i)) {
                        NFCNote nfcNote = new NFCNote(i);
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
                    if (eco.depositPlayer(player, money).transactionSuccess()) {
                        for (ItemStack i : notes) i.setAmount(0);
                        player.sendMessage(Utils.parseMessage(messages.getString("massdeposit-successful")).replace("{money}", formattedMoney));
                        totalAmount = money;
                    } else {
                        player.sendMessage(Utils.parseMessage(messages.getString("unexpected-error")));
                    }
                }
            } else {
                // Deposit
                NFCNote nfcNote = new NFCNote(e.getItem());
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
                    if (eco.depositPlayer(player, money).transactionSuccess()) {
                        player.sendMessage(Utils.parseMessage(messages.getString("deposit-successful")).replace("{money}", formattedMoney));
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                        totalAmount = money;
                    } else {
                        player.sendMessage(Utils.parseMessage(messages.getString("unexpected-error")));
                    }
                }
            }
            // Warn staff if the note's value is higher than the specified in the configuration file
            if (totalAmount >= config.getInt("warn-staff-if-value-is-higher-than")) {
                String formattedMoney = decimalFormat.format(totalAmount);
                for (Player pl : plugin.getServer().getOnlinePlayers()) {
                    if (pl.hasPermission("nfcnotes.staff.warn") && e.getPlayer() != pl) {
                        pl.sendMessage(Utils.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                        plugin.getLogger().info(Utils.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                    }
                }
            }
        }
    }
}