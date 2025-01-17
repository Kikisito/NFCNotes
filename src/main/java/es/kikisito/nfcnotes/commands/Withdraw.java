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

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.NFCNote;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.events.WithdrawEvent;
import es.kikisito.nfcnotes.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class Withdraw implements CommandExecutor {
    private final Main plugin;

    public Withdraw(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // Only players can execute this command. Console, get away!
        if (!(sender instanceof Player player)) {
            plugin.getAdventure().sender(sender).sendMessage(NFCMessages.ONLY_PLAYERS.getString());
            return false;
        }

        // Cast to Adventure Audience
        Audience audience = plugin.getAdventure().sender(player);

        // Check if the player is allowed to withdraw money and its inventory is not full
        if (player.getInventory().firstEmpty() == -1) {
            audience.sendMessage(NFCMessages.FULL_INVENTORY.getString());
            return false;
        } else if(NFCConfig.DISABLED_WORLDS.getStrings().contains(player.getWorld().getName()) && !player.hasPermission("nfcnotes.staff.withdraw.bypass.disabled-world")){
            audience.sendMessage(NFCMessages.DISABLED_WORLD.getString());
            return false;
        }
        double money;
        int amount;
        try {
            switch (args.length) {
                case 0:
                    if(!NFCConfig.WITHDRAW_ONLY_ALLOWS_A_SPECIFIC_VALUE.getBoolean()) {
                        audience.sendMessage(NFCMessages.WITHDRAW_USAGE.getString());
                    } else {
                        if(player.hasPermission("nfcnotes.withdraw.one")) {
                            money = NFCConfig.WITHDRAW_VALUE.getDouble();
                            withdraw(player, money, 1);
                        } else {
                            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    }
                    break;
                case 1:
                    if(!NFCConfig.WITHDRAW_ONLY_ALLOWS_A_SPECIFIC_VALUE.getBoolean()) {
                        // Check if "withdraw all" submodule is enabled and te first argument is "all"
                        if (args[0].equalsIgnoreCase("all")) {
                            if(NFCConfig.MODULES_WITHDRAW_ALL.getBoolean()) {
                                if (player.hasPermission("nfcnotes.withdraw.all")) {
                                    money = Utils.getPlayerBalance(plugin, player);
                                    withdraw(player, money, 1);
                                    return true;
                                } else {
                                    audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                                }
                            } else {
                                audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                            }
                        } else {
                            if(NFCConfig.MODULES_WITHDRAW.getBoolean()) {
                                if(player.hasPermission("nfcnotes.withdraw.one")) {
                                    money = Double.parseDouble(args[0].replace(',', '.'));
                                    money = Math.round(money * 100.0) / 100.0;
                                    withdraw(player, money, 1);
                                } else {
                                    audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                                }
                            } else {
                                audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                            }
                        }
                    } else {
                        if(player.hasPermission("nfcnotes.withdraw.one")) {
                            money = NFCConfig.WITHDRAW_VALUE.getDouble();
                            amount = Integer.parseInt(args[0]);
                            withdraw(player, money, amount);
                        } else {
                            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    }
                    break;
                case 2:
                    if(NFCConfig.WITHDRAW_ONLY_ALLOWS_A_SPECIFIC_VALUE.getBoolean()) {
                        audience.sendMessage(NFCMessages.WITHDRAW_USAGE.getString());
                        break;
                    }

                    // Works only if the multiple withdraw submodule is enabled
                    if(player.hasPermission("nfcnotes.withdraw.multiple")) {
                        if (NFCConfig.MODULES_MULTIPLE_WITHDRAW.getBoolean()) {
                            money = Double.parseDouble(args[0].replace(',', '.'));
                            money = Math.round(money * 100.0) / 100.0;
                            amount = Integer.parseInt(args[1]);
                            withdraw(player, money, amount);
                        }
                    } else {
                        audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                    }
                    break;
                default:
                    audience.sendMessage(NFCMessages.WITHDRAW_USAGE.getString());
                    break;
            }
        } catch (NumberFormatException ex) {
            audience.sendMessage(NFCMessages.INCORRECT_FORMAT.getString());
        }
        return true;
    }

    private void withdraw(Player p, double m, int a){
        // Cast to Adventure Audience
        Audience audience = plugin.getAdventure().sender(p);

        // Check if given number is positive and is an integer.
        if (m <= 0) {
            audience.sendMessage(NFCMessages.USE_A_NUMBER_HIGHER_THAN_ZERO.getString());
            return;
        } else if(!(m % 1 == 0) && !NFCConfig.USE_DECIMALS.getBoolean()) {
            audience.sendMessage(NFCMessages.ONLY_INTEGERS.getString());
            return;
        }

        // Check if the player has enough space in the inventory when withdrawing more than one stack
        int freeSlots = this.countFreeSlots(p.getInventory());
        if (a > 64 && freeSlots < (int) Math.ceil(a / 64.0)) {
            audience.sendMessage(NFCMessages.NOT_ENOUGH_SPACE.getString());
            return;
        }

        // Call WithdrawEvent and check if it was cancelled
        WithdrawEvent withdrawEvent = new WithdrawEvent(p, m, a, ActionMethod.COMMAND);
        plugin.getServer().getPluginManager().callEvent(withdrawEvent);
        if(!withdrawEvent.isCancelled()) {
            // Get variables from event
            Player player = withdrawEvent.getPlayer();
            Audience playerAudience = plugin.getAdventure().sender(player);
            Double money = withdrawEvent.getMoney();
            Integer amount = withdrawEvent.getAmount();
            // Make the amount readable
            DecimalFormat decimalFormat = Utils.getDecimalFormat();
            String formattedMoney = decimalFormat.format(money * amount);
            // Execute if the event wasn't cancelled
            // Execute withdraw and get Vault's response
            // EconomyResponse response = plugin.getVaultEco().withdrawPlayer(player, money * amount);
            if (Utils.getPlayerBalance(plugin, player) >= money * amount) {
                if(Utils.withdrawSuccessful(plugin, player, money * amount)) {
                    // Create the note and give it to the player
                    ItemStack paper = NFCNote.createNFCNoteItem(this.plugin, NFCConfig.NOTE_NAME.getMessage(), NFCConfig.NOTE_LORE.getMessages(), NFCConfig.NOTE_MATERIAL.getString(), p.getName(), decimalFormat, money, amount);
                    player.getInventory().addItem(paper);
                    playerAudience.sendMessage(NFCMessages.WITHDRAW_SUCCESSFUL.getString("{money}", formattedMoney));
                    // Warn staff if the note's value is higher than the specified in the configuration file
                    if (money * amount >= NFCConfig.WARN_VALUE_LIMIT.getInt() && NFCConfig.MODULES_WARN_STAFF.getBoolean()) {
                        for (Player pl : plugin.getServer().getOnlinePlayers()) {
                            if (pl.hasPermission("nfcnotes.staff.warn") && player != pl) {
                                Audience staffAudience = plugin.getAdventure().sender(pl);
                                staffAudience.sendMessage(NFCMessages.STAFF_WARN_WITHDRAW.getString("{player}", player.getName(), "{money}", formattedMoney));
                                plugin.getLogger().info(NFCMessages.STAFF_WARN_WITHDRAW.getLegacyString("{player}", player.getName(), "{money}", formattedMoney));
                            }
                        }
                    }
                } else {
                    // Unexpected error
                    playerAudience.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                }
            } else {
                playerAudience.sendMessage(NFCMessages.INSUFFICIENT_FUNDS.getString());
            }
        }
    }

    private int countFreeSlots(PlayerInventory inventory) {
        int freeSlots = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) freeSlots++;
        }
        return freeSlots;
    }
}
