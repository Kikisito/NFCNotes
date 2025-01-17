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
import net.kyori.adventure.audience.Audience;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public class Deposit implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final DecimalFormat decimalFormat = Utils.getDecimalFormat();

    public Deposit(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // Cast to Adventure Audience
        Audience audience = plugin.getAdventure().sender(sender);

        double value = 0;
        // Only players can execute this command.
        if (!(sender instanceof Player p)) {
            audience.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
            return false;
        }
        if(NFCConfig.DISABLED_WORLDS.getStrings().contains(p.getWorld().getName()) && !p.hasPermission("nfcnotes.staff.deposit.bypass.disabled-world")){
            sender.sendMessage(NFCMessages.DISABLED_WORLD.getString());
            return false;
        }
        switch(args.length){
            case 0:
                if(p.hasPermission("nfcnotes.deposit.command.one")) {
                    if (NFCConfig.MODULES_DEPOSIT_COMMAND.getBoolean()) {
                        if (NFCNote.isNFCNote(this.plugin, p.getInventory().getItemInMainHand())) {
                            NFCNote nfcNote = new NFCNote(this.plugin, p.getInventory().getItemInMainHand());
                            value = nfcNote.getValue();
                            this.depositMoney(nfcNote, p, 1, value);
                        } else {
                            audience.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                        }
                    } else {
                        audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
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
                                if (NFCNote.isNFCNote(this.plugin, i)) {
                                    NFCNote nfcNote = new NFCNote(this.plugin, i);
                                    double amount = nfcNote.getValue() * i.getAmount();
                                    value = value + amount;
                                    notes.add(i);
                                }
                            }
                            // Check if any note has been found
                            if (value == 0) {
                                audience.sendMessage(NFCMessages.NO_NOTES_FOUND.getString());
                                return false;
                            }
                            // Calls DepositEvent
                            DepositEvent depositEvent = new DepositEvent(p, value, ActionMethod.COMMAND_ALL);
                            plugin.getServer().getPluginManager().callEvent(depositEvent);
                            // Deposit money if the event wasn't cancelled
                            if (!depositEvent.isCancelled()) {
                                // Get variables from called event
                                Player player = depositEvent.getPlayer();
                                Audience playerAudience = plugin.getAdventure().player(player);
                                double money = depositEvent.getMoney();
                                String formattedMoney = decimalFormat.format(money);
                                if (Utils.depositSuccessful(plugin, player, money)) {
                                    for (ItemStack i : notes) i.setAmount(0);
                                    playerAudience.sendMessage(NFCMessages.MASSDEPOSIT_SUCCESSFUL.getString("{money}", formattedMoney));
                                    Deposit.playRedeemSound(player);
                                } else {
                                    playerAudience.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
                                }
                            }
                        } else {
                            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                } else if(args[0].equals("stack")) {
                    if(NFCConfig.MODULES_DEPOSIT_STACK.getBoolean()) {
                        if (p.hasPermission("nfcnotes.deposit.command.stack")) {
                            if (NFCNote.isNFCNote(this.plugin, p.getInventory().getItemInMainHand())) {
                                NFCNote nfcNote = new NFCNote(this.plugin, p.getInventory().getItemInMainHand());
                                value = nfcNote.getValue();
                                this.depositMoney(nfcNote, p, nfcNote.getItemStack().getAmount(), value);
                            } else {
                                audience.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                            }
                        } else {
                            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                } else if(Utils.isInteger(args[0])) {
                    if(NFCConfig.MODULES_DEPOSIT_MULTIPLE.getBoolean()){
                        if (p.hasPermission("nfcnotes.deposit.command.multiple")) {
                            if (NFCNote.isNFCNote(this.plugin, p.getInventory().getItemInMainHand())) {
                                int amount = Integer.parseInt(args[0]);
                                NFCNote nfcNote = new NFCNote(this.plugin, p.getInventory().getItemInMainHand());
                                if(nfcNote.getItemStack().getAmount() >= amount){
                                    value = nfcNote.getValue();
                                    this.depositMoney(nfcNote, p, amount, value);
                                } else {
                                    audience.sendMessage(NFCMessages.INSUFFICIENT_NOTES.getString());
                                }
                            } else {
                                audience.sendMessage(NFCMessages.NOT_A_NOTE.getString());
                            }
                        } else {
                            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
                        }
                    } else {
                        audience.sendMessage(NFCMessages.MODULE_DISABLED.getString());
                    }
                    break;
                }
            default:
                audience.sendMessage(NFCMessages.DEPOSIT_USAGE.getString());
                break;
        }
        // Warn staff if the note's value is higher than the specified in the configuration file
        if (value >= NFCConfig.WARN_VALUE_LIMIT.getInt() && NFCConfig.MODULES_WARN_STAFF.getBoolean()) {
            String formattedMoney = decimalFormat.format(value);
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                Audience playerAudience = plugin.getAdventure().player(pl);
                if (pl.hasPermission("nfcnotes.staff.warn") && p != pl) {
                    playerAudience.sendMessage(NFCMessages.STAFF_WARN_DEPOSIT.getString("{player}", p.getName(), "{money}", formattedMoney));
                    plugin.getLogger().info(NFCMessages.STAFF_WARN_DEPOSIT.getLegacyString("{player}", p.getName(), "{money}", formattedMoney));
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
            Audience playerAudience = plugin.getAdventure().player(player);
            value = depositEvent.getMoney();
            String formattedMoney = decimalFormat.format(value);
            if (Utils.depositSuccessful(plugin, player, value)) {
                playerAudience.sendMessage(NFCMessages.DEPOSIT_SUCCESSFUL.getString("{money}", formattedMoney));
                Deposit.playRedeemSound(player);
                nfcNote.getItemStack().setAmount(nfcNote.getItemStack().getAmount() - amount);
            } else {
                Audience senderAudience = plugin.getAdventure().player(p);
                senderAudience.sendMessage(NFCMessages.UNEXPECTED_ERROR.getString());
            }
        }
    }

    public static void playRedeemSound(Player player) {
        if(NFCConfig.REDEEM_SOUND_ENABLED.getBoolean()){
            Sound sound = Sound.valueOf(NFCConfig.REDEEM_SOUND.getString());
            SoundCategory soundCategory = SoundCategory.valueOf(NFCConfig.REDEEM_SOUND_CATEGORY.getString());
            float volume = NFCConfig.REDEEM_SOUND_VOLUME.getDouble().floatValue();
            float pitch = NFCConfig.REDEEM_SOUND_PITCH.getDouble().floatValue();
            player.playSound(player.getLocation(), sound, soundCategory, volume, pitch);
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length != 1) return Collections.emptyList();
        List<String> options = Arrays.asList("all", "stack");
        List<String> tab = new ArrayList<>();
        for(String s : options){
            if(s.startsWith(args[0])) tab.add(s);
        }
        return tab;
    }
}
