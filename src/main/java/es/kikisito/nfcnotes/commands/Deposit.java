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
import es.kikisito.nfcnotes.Utils;
import es.kikisito.nfcnotes.enums.ActionMethod;
import es.kikisito.nfcnotes.events.DepositEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Deposit implements CommandExecutor {
    private Main plugin;
    private Configuration config;
    private Economy eco;

    public Deposit(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.eco = plugin.getEco();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        // Only players can execute this command.
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.parseMessage(messages.getString("only-players")));
            return false;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("nfcnotes.deposit.command") || !config.getBoolean("modules.deposit.command")){
            sender.sendMessage(Utils.parseMessage(messages.getString("no-permission")));
            return false;
        } else if(config.getStringList("disabled-worlds").contains(p.getWorld().getName()) && !p.hasPermission("nfcnotes.staff.deposit.bypass.disabled-world")){
            sender.sendMessage(Utils.parseMessage(messages.getString("disabled-world")));
            return false;
        }
        DecimalFormat decimalFormat = new DecimalFormat(config.getString("notes.decimal-format"));
        double value = 0;
        switch(args.length){
            case 0:
                if(NFCNote.isNFCNote(p.getInventory().getItemInMainHand())){
                    NFCNote nfcNote = new NFCNote(p.getInventory().getItemInMainHand());
                    DepositEvent depositEvent = new DepositEvent(p, nfcNote.getValue(), ActionMethod.COMMAND);
                    plugin.getServer().getPluginManager().callEvent(depositEvent);
                    Player player = depositEvent.getPlayer();
                    Double money = depositEvent.getMoney();
                    String formattedMoney = decimalFormat.format(money);
                    if(eco.depositPlayer(player, money).transactionSuccess()){
                        player.sendMessage(Utils.parseMessage(messages.getString("deposit-successful")).replace("{money}", formattedMoney));
                        nfcNote.getItemStack().setAmount(nfcNote.getItemStack().getAmount() - 1);
                    } else {
                        p.sendMessage(Utils.parseMessage(messages.getString("unexpected-error")));
                    }
                } else {
                    p.sendMessage(Utils.parseMessage(messages.getString("not-a-note")));
                }
                break;
            case 1:
                if(args[0].equals("all")){
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
                    if(value == 0){
                        p.sendMessage(Utils.parseMessage(messages.getString("no-notes-found")));
                        return false;
                    }
                    // Calls DepositEvent
                    DepositEvent depositEvent = new DepositEvent(p, value, ActionMethod.COMMAND_ALL);
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
                        } else {
                            player.sendMessage(Utils.parseMessage(messages.getString("unexpected-error")));
                        }
                    }
                    break;
                } else {
                    // Coming soon!
                }
            default:
                sender.sendMessage(Utils.parseMessage(messages.getString("deposit-usage")));
                break;
        }
        // Warn staff if the note's value is higher than the specified in the configuration file
        if (value >= config.getInt("warn-staff-if-value-is-higher-than")) {
            String formattedMoney = decimalFormat.format(value);
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                if (pl.hasPermission("nfcnotes.staff.warn") && p != pl) {
                    pl.sendMessage(Utils.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", p.getName()).replace("{money}", formattedMoney));
                    plugin.getLogger().info(Utils.parseMessage(messages.getString("staff.warn-deposit")).replace("{player}", p.getName()).replace("{money}", formattedMoney));
                }
            }
        }
        return false;
    }
}
