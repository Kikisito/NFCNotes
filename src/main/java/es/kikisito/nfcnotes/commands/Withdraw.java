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

package es.kikisito.nfcnotes.commands;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.NFCNote;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.events.WithdrawEvent;
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Withdraw implements CommandExecutor {
    private final Main plugin;

    public Withdraw(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Only players can execute this command. Console, get away!
        if (!(sender instanceof Player)) {
            sender.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
            return false;
        }
        Player p = (Player) sender;
        // Check if the player is allowed to withdraw money and its inventory is not full
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(NFCMessages.FULL_INVENTORY.getString());
            return false;
        } else if(NFCConfig.DISABLED_WORLDS.getList().contains(p.getWorld().getName()) && !p.hasPermission("nfcnotes.staff.withdraw.bypass.disabled-world")){
            p.sendMessage(NFCMessages.DISABLED_WORLD.getString());
            return false;
        }
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    // Check if "withdraw all" submodule is enabled and te first argument is "all"
                    if (args[0].equalsIgnoreCase("all")) {
                        if(NFCConfig.MODULES_WITHDRAW_ALL.getBoolean()) {
                            if (p.hasPermission("nfcnotes.withdraw.all")) {
                                money = Utils.getPlayerBalance(plugin, p);
                                withdraw(p, money, 1);
                                return true;
                            } else {
                                p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                            }
                        } else {
                            p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                        }
                    } else {
                        if(NFCConfig.MODULES_WITHDRAW.getBoolean()) {
                            if(p.hasPermission("nfcnotes.withdraw.one")) {
                                money = Double.parseDouble(args[0].replace(',', '.'));
                                money = Math.round(money * 100.0) / 100.0;
                                withdraw(p, money, 1);
                            } else {
                                p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                            }
                        } else {
                            p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                        }
                    }
                    break;
                case 2:
                    // Works only if the multiple withdraw submodule is enabled
                    if(p.hasPermission("nfcnotes.withdraw.multiple")) {
                        if (NFCConfig.MODULES_MULTIPLE_WITHDRAW.getBoolean()) {
                            money = Double.parseDouble(args[0].replace(',', '.'));
                            money = Math.round(money * 100.0) / 100.0;
                            amount = Integer.parseInt(args[1]);
                            withdraw(p, money, amount);
                            break;
                        }
                    } else {
                        p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                    }
                default:
                    p.sendMessage(NFCMessages.WITHDRAW_USAGE.getString());
                    break;
            }
        } catch (NumberFormatException ex) {
            p.sendMessage(NFCMessages.INCORRECT_FORMAT.getString());
        }
        return true;
    }

    private void withdraw(Player p, double m, int a){
        // Check if given number is positive and is an integer.
        if (m <= 0) {
            p.sendMessage(NFCMessages.USE_A_NUMBER_HIGHER_THAN_ZERO.getString());
            return;
        } else if(!(m % 1 == 0) && !NFCConfig.USE_DECIMALS.getBoolean()) {
            p.sendMessage(NFCMessages.ONLY_INTEGERS.getString());
            return;
        }
        // Call WithdrawEvent and check if it was cancelled
        WithdrawEvent withdrawEvent = new WithdrawEvent(p, m, a, ActionMethod.COMMAND);
        plugin.getServer().getPluginManager().callEvent(withdrawEvent);
        if(!withdrawEvent.isCancelled()) {
            // Get variables from event
            Player player = withdrawEvent.getPlayer();
            Double money = withdrawEvent.getMoney();
            Integer amount = withdrawEvent.getAmount();
            // Make the amount readable
            DecimalFormat decimalFormat;
            if(NFCConfig.USE_EUROPEAN_FORMAT.getBoolean()) {
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);
                decimalFormatSymbols.setDecimalSeparator(',');
                decimalFormatSymbols.setGroupingSeparator('.');
                decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString(), decimalFormatSymbols);
            } else decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString());
            decimalFormat.setMaximumFractionDigits(2);
            String formattedMoney = decimalFormat.format(money * amount);
            // Execute if the event wasn't cancelled
            // Execute withdraw and get Vault's response
            // EconomyResponse response = plugin.getVaultEco().withdrawPlayer(player, money * amount);
            if (Utils.getPlayerBalance(plugin, player) >= money * amount) {
                if(Utils.withdrawSuccessful(plugin, player, money * amount)) {
                    System.out.println(money * amount);
                    // Create the note and give it to the player
                    ItemStack paper = NFCNote.createNFCNoteItem(NFCConfig.NOTE_UUID.getString(), NFCConfig.NOTE_NAME.getString(), NFCConfig.NOTE_LORE.getList(), NFCConfig.NOTE_MATERIAL.getString(), p.getName(), decimalFormat, money, amount);
                    player.getInventory().addItem(paper);
                    player.sendMessage(NFCMessages.WITHDRAW_SUCCESSFUL.getString().replace("{money}", formattedMoney));
                    // Warn staff if the note's value is higher than the specified in the configuration file
                    if (money * amount >= NFCConfig.WARN_VALUE_LIMIT.getInt() && NFCConfig.MODULES_WARN_STAFF.getBoolean()) {
                        for (Player pl : plugin.getServer().getOnlinePlayers()) {
                            if (pl.hasPermission("nfcnotes.staff.warn") && player != pl) {
                                pl.sendMessage(NFCMessages.STAFF_WARN_WITHDRAW.getString().replace("{player}", player.getName()).replace("{money}", formattedMoney));
                                plugin.getLogger().info(NFCMessages.STAFF_WARN_WITHDRAW.getString().replace("{player}", player.getName()).replace("{money}", formattedMoney));
                            }
                        }
                    }
                } else {
                    // Unexpected error
                    player.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                }
            } else {
                player.sendMessage(NFCMessages.INSUFFICIENT_FUNDS.getString());
            }
        }
    }
}
