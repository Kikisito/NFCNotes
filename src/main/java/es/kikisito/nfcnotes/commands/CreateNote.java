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
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateNote implements CommandExecutor {
    private Main plugin;
    private Configuration config;

    public CreateNote(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        // Check if the player is allowed to withdraw money and its inventory is not full
        if (!sender.hasPermission("nfcnotes.staff.createnote")) {
            sender.sendMessage(Utils.parseMessage(messages.getString("no-permission")));
            return false;
        } else if (sender instanceof Player && ((Player) sender).getInventory().firstEmpty() == -1) {
            sender.sendMessage(Utils.parseMessage(messages.getString("full-inventory")));
            return false;
        }
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        money = Double.parseDouble(args[0]);
                        createNote(p, money, 1);
                    } else sender.sendMessage(Utils.parseMessage(messages.getString("only-players")));
                    break;
                case 2:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        createNote(player, money, 1);
                    } else {
                        if(sender instanceof Player) {
                            Player p = (Player) sender;
                            money = Double.parseDouble(args[0]);
                            amount = Integer.parseInt(args[1]);
                            createNote(p, money, amount);
                        } else sender.sendMessage(Utils.parseMessage(messages.getString("only-players")));
                    }
                    break;
                case 3:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        amount = Integer.parseInt(args[2]);
                        createNote(player, money, amount);
                        break;
                    }
                default:
                    sender.sendMessage(Utils.parseMessage(messages.getString("withdraw-usage")));
                    break;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(Utils.parseMessage(messages.getString("only-integers")));
        }
        return true;
    }

    private void createNote(Player p, Double m, Integer a){
        FileConfiguration messages = plugin.getMessages();
        // Check if given number is positive and is an integer.
        if (m <= 0) {
            p.sendMessage(Utils.parseMessage(messages.getString("use-a-number-higher-than-zero")));
            return;
        } else if(!(m % 1 == 0)) {
            p.sendMessage(Utils.parseMessage(messages.getString("only-integers")));
            return;
        }
        // Make the amount readable
        DecimalFormat decimalFormat = new DecimalFormat(config.getString("notes.decimal-format"));
        String formattedMoney = decimalFormat.format(m);
        // Create the note and give it to the player
        ItemStack paper = NFCNote.createNFCNoteItem(config.getString("notes.identifier"), config.getString("notes.name"), config.getStringList("notes.lore"), config.getString("notes.material"), decimalFormat, m, a);
        p.getInventory().addItem(paper);
        p.sendMessage(Utils.parseMessage(messages.getString("createnote-successful").replace("{money}", formattedMoney)));
    }
}
