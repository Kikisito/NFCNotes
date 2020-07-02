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
import es.kikisito.nfcnotes.events.DepositEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InteractListener implements Listener {
    private Main plugin;
    private Configuration config;
    private FileConfiguration messages;
    private Economy eco = Main.getEco();

    public InteractListener(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
    }

    @EventHandler
    private void redeem(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        // Check if the item's material is Paper
        if(e.getItem() != null && e.getItem().getType() == Material.PAPER){
            ItemStack item = e.getItem();
            if(plugin.isNote(item)) {
                // Check if player is allowed to deposit money
                if (!p.hasPermission("nfcnotes.deposit") || !config.getBoolean("modules.deposit")) return;
                DecimalFormat decimalFormat = new DecimalFormat(config.getString("notes.decimal-format"));
                double totalAmount = 0;
                // Mass Deposit
                // Checks if a player is sneaking, the submodule is enabled and if the player is allowed to mass-deposit.
                if (p.isSneaking() && p.hasPermission("nfcnotes.deposit.massdeposit") && config.getBoolean("modules.massdeposit")) {
                    List<ItemStack> notes = new ArrayList<>();
                    double value = 0;
                    // Checks for notes in player's inventory
                    for (ItemStack i : e.getPlayer().getInventory()) {
                        if (i != null && i.getType().equals(Material.PAPER)) {
                            if (plugin.isNote(i)) {
                                double amount = i.getItemMeta().getAttributeModifiers(Attribute.GENERIC_LUCK).iterator().next().getAmount() * i.getAmount();
                                value = value + amount;
                                notes.add(i);
                            }
                        }
                    }
                    // Calls DepositEvent
                    DepositEvent depositEvent = new DepositEvent(p, value);
                    plugin.getServer().getPluginManager().callEvent(depositEvent);
                    // Get variables from called event
                    Player player = depositEvent.getPlayer();
                    double money = depositEvent.getMoney();
                    String formattedMoney = decimalFormat.format(money);
                    // Deposit money if the event wasn't cancelled
                    if (!depositEvent.isCancelled()) {
                        if (eco.depositPlayer(player, money).transactionSuccess()) {
                            for (ItemStack i : notes) i.setAmount(0);
                            plugin.getLogger().info(player.getName() + " ha canjeado un total de " + formattedMoney + " Θ.");
                            player.sendMessage(plugin.parseMessage(messages.getString("massdeposit-successful")).replace("{money}", formattedMoney));
                            totalAmount = money;
                        } else {
                            player.sendMessage(plugin.parseMessage(messages.getString("unexpected-error")));
                        }
                    }
                } else {
                    // Deposit
                    ItemMeta im = item.getItemMeta();
                    double m = im.getAttributeModifiers(Attribute.GENERIC_LUCK).iterator().next().getAmount();
                    // Calls DepositEvent
                    DepositEvent depositEvent = new DepositEvent(p, m);
                    plugin.getServer().getPluginManager().callEvent(depositEvent);
                    // Deposit money if the event wasn't cancelled
                    if (!depositEvent.isCancelled()) {
                        // Get variables from called event
                        Player player = depositEvent.getPlayer();
                        double money = depositEvent.getMoney();
                        String formattedMoney = decimalFormat.format(money);
                        if (eco.depositPlayer(player, money).transactionSuccess()) {
                            player.sendMessage(plugin.parseMessage(messages.getString("deposit-successful")).replace("{money}", formattedMoney));
                            item.setAmount(item.getAmount() - 1);
                            totalAmount = money;
                        } else {
                            player.sendMessage(plugin.parseMessage(messages.getString("unexpected-error")));
                        }
                    }
                }
                // Warn staff if the note's value is higher than the specified in the configuration file
                if (totalAmount >= config.getInt("warn-staff-if-value-is-higher-than")) {
                    String formattedMoney = decimalFormat.format(totalAmount);
                    for (Player pl : plugin.getServer().getOnlinePlayers()) {
                        if (pl.hasPermission("nfcnotes.staff.warn") && e.getPlayer() != pl) {
                            pl.sendMessage(plugin.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                            plugin.getLogger().info(plugin.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", e.getPlayer().getName()).replace("{money}", formattedMoney));
                        }
                    }
                }
            }
        }
    }
}