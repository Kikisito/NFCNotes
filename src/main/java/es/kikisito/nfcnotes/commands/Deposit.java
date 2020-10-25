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

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.NFCNote;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.events.DepositEvent;
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deposit implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private DecimalFormat decimalFormat;

    public Deposit(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        double value = 0;
        // Only players can execute this command.
        if (!(sender instanceof Player)) {
            sender.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
            return false;
        }
        Player p = (Player) sender;
        if(NFCConfig.DISABLED_WORLDS.getList().contains(p.getWorld().getName()) && !p.hasPermission("nfcnotes.staff.deposit.bypass.disabled-world")){
            sender.sendMessage(NFCMessages.DISABLED_WORLD.getString());
            return false;
        }
        decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString());
        decimalFormat.setMaximumFractionDigits(2);
        switch(args.length){
            case 0:
                if(p.hasPermission("nfcnotes.deposit.command.one")) {
                    if (NFCConfig.MODULES_DEPOSIT_COMMAND.getBoolean()) {
                        if (NFCNote.isNFCNote(p.getInventory().getItemInMainHand())) {
                            NFCNote nfcNote = new NFCNote(p.getInventory().getItemInMainHand());
                            value = nfcNote.getValue();
                            this.depositMoney(nfcNote, p, 1, value);
                        } else {
                            p.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                        }
                    } else {
                        p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                }
                break;
            case 1:
                if(args[0].equals("all")){
                    if(NFCConfig.MODULES_DEPOSIT_ONE.getBoolean()) {
                        if (p.hasPermission("nfcnotes.deposit.command.all")) {
                            List<ItemStack> notes = new ArrayList<>();
                            // Checks for notes in player's inventory
                            for (ItemStack i : p.getInventory()) {
                                if (NFCNote.isNFCNote(i)) {
                                    NFCNote nfcNote = new NFCNote(i);
                                    double amount = nfcNote.getValue() * i.getAmount();
                                    value = value + amount;
                                    notes.add(i);
                                }
                            }
                            // Check if any note has been found
                            if (value == 0) {
                                p.sendMessage(NFCMessages.NO_NOTES_FOUND.getString());
                                return false;
                            }
                            // Calls DepositEvent
                            DepositEvent depositEvent = new DepositEvent(p, value, ActionMethod.COMMAND_ALL);
                            plugin.getServer().getPluginManager().callEvent(depositEvent);
                            // Deposit money if the event wasn't cancelled
                            if (!depositEvent.isCancelled()) {
                                // Get variables from called event
                                Player player = depositEvent.getPlayer();
                                double money = depositEvent.getMoney();
                                String formattedMoney = decimalFormat.format(money);
                                if (Utils.depositSuccessful(plugin, player, money)) {
                                    for (ItemStack i : notes) i.setAmount(0);
                                    player.sendMessage(NFCMessages.MASSDEPOSIT_SUCCESSFUL.getString().replace("{money}", formattedMoney));
                                } else {
                                    player.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                                }
                            }
                        } else {
                            p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                } else if(args[0].equals("stack")) {
                    if(NFCConfig.MODULES_DEPOSIT_STACK.getBoolean()) {
                        if (p.hasPermission("nfcnotes.deposit.command.stack")) {
                            if (NFCNote.isNFCNote(p.getInventory().getItemInMainHand())) {
                                NFCNote nfcNote = new NFCNote(p.getInventory().getItemInMainHand());
                                value = nfcNote.getValue();
                                this.depositMoney(nfcNote, p, nfcNote.getItemStack().getAmount(), value);
                            } else {
                                p.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                            }
                        } else {
                            p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                } else if(Utils.isInteger(args[0])) {
                    if(NFCConfig.MODULES_DEPOSIT_MULTIPLE.getBoolean()){
                        if (p.hasPermission("nfcnotes.deposit.command.multiple")) {
                            if (NFCNote.isNFCNote(p.getInventory().getItemInMainHand())) {
                                Integer amount = Integer.parseInt(args[0]);
                                NFCNote nfcNote = new NFCNote(p.getInventory().getItemInMainHand());
                                if(nfcNote.getItemStack().getAmount() >= amount){
                                    value = nfcNote.getValue();
                                    this.depositMoney(nfcNote, p, amount, value);
                                } else {
                                    p.sendMessage(NFCMessages.INSUFFICIENT_NOTES.getString());
                                }
                            } else {
                                p.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                            }
                        } else {
                            p.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        p.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                }
            default:
                sender.sendMessage(NFCMessages.DEPOSIT_USAGE.getString());
                break;
        }
        // Warn staff if the note's value is higher than the specified in the configuration file
        if (value >= NFCConfig.WARN_VALUE_LIMIT.getInt() && NFCConfig.MODULES_WARN_STAFF.getBoolean()) {
            String formattedMoney = decimalFormat.format(value);
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                if (pl.hasPermission("nfcnotes.staff.warn") && p != pl) {
                    pl.sendMessage(NFCMessages.STAFF_WARN_DEPOSIT.getString().replace("{player}", p.getName()).replace("{money}", formattedMoney));
                    plugin.getLogger().info(NFCMessages.STAFF_WARN_DEPOSIT.getString().replace("{player}", p.getName()).replace("{money}", formattedMoney));
                }
            }
        }
        return false;
    }

    public void depositMoney(NFCNote nfcNote, Player p, int amount, double value){
        DepositEvent depositEvent = new DepositEvent(p, value * amount, ActionMethod.COMMAND);
        if(!depositEvent.isCancelled()) {
            plugin.getServer().getPluginManager().callEvent(depositEvent);
            Player player = depositEvent.getPlayer();
            value = depositEvent.getMoney();
            String formattedMoney = decimalFormat.format(value);
            if (Utils.depositSuccessful(plugin, player, value)) {
                player.sendMessage(NFCMessages.DEPOSIT_SUCCESSFUL.getString().replace("{money}", formattedMoney));
                nfcNote.getItemStack().setAmount(nfcNote.getItemStack().getAmount() - amount);
            } else {
                p.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length != 1) return Collections.emptyList();
        List<String> options = Arrays.asList("all", "stack");
        List<String> tab = new ArrayList<>();
        for(String s : options){
            if(s.startsWith(args[0])) tab.add(s);
        }
        return tab;
    }
}
