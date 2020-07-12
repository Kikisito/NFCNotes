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
import es.kikisito.nfcnotes.Utils;
import es.kikisito.nfcnotes.enums.WithdrawMethod;
import es.kikisito.nfcnotes.events.WithdrawEvent;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Withdraw implements CommandExecutor {
    private Main plugin;
    private Configuration config;

    public Withdraw(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        // Only players can execute this command. Console, get away!
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.parseMessage(messages.getString("only-players")));
            return false;
        }
        Player p = (Player) sender;
        // Check if the player is allowed to withdraw money and its inventory is not full
        if (!p.hasPermission("nfcnotes.withdraw") || !config.getBoolean("modules.withdraw")) {
            p.sendMessage(Utils.parseMessage(messages.getString("no-permission")));
            return false;
        } else if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(Utils.parseMessage(messages.getString("full-inventory")));
            return false;
        }
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    // Check if "withdraw all" submodule is enabled and te first argument is "all"
                    if (args[0].equalsIgnoreCase("all") && config.getBoolean("modules.withdraw-all")) {
                        money = Main.getEco().getBalance(p);
                        withdraw(p, money, 1);
                        return true;
                    } else {
                        money = Double.parseDouble(args[0]);
                        withdraw(p, money, 1);
                    }
                    break;
                case 2:
                    // Works only if the multiple withdraw submodule is enabled
                    if(config.getBoolean("modules.multiple-withdraw")) {
                        money = Double.parseDouble(args[0]);
                        amount = Integer.parseInt(args[1]);
                        withdraw(p, money, amount);
                        break;
                    }
                default:
                    p.sendMessage(Utils.parseMessage(messages.getString("withdraw-usage")));
                    break;
            }
        } catch (NumberFormatException ex) {
            p.sendMessage(Utils.parseMessage(messages.getString("only-integers")));
        }
        return true;
    }

    private void withdraw(Player p, Double m, Integer a){
        FileConfiguration messages = plugin.getMessages();
        // Check if given number is positive and is an integer.
        if (m <= 0) {
            p.sendMessage(Utils.parseMessage(messages.getString("use-a-number-higher-than-zero")));
            return;
        } else if(!(m % 1 == 0)) {
            p.sendMessage(Utils.parseMessage(messages.getString("only-integers")));
            return;
        }
        // Call WithdrawEvent and check if it was cancelled
        WithdrawEvent withdrawEvent = new WithdrawEvent(p, m, a, WithdrawMethod.COMMAND);
        plugin.getServer().getPluginManager().callEvent(withdrawEvent);
        if(!withdrawEvent.isCancelled()) {
            // Get variables from event
            Player player = withdrawEvent.getPlayer();
            Double money = withdrawEvent.getMoney();
            Integer amount = withdrawEvent.getAmount();
            // Make the amount readable
            DecimalFormat decimalFormat = new DecimalFormat(config.getString("notes.decimal-format"));
            String formattedMoney = decimalFormat.format(money);
            // Execute if the event wasn't cancelled
            // Execute withdraw and get Vault's response
            EconomyResponse response = Main.getEco().withdrawPlayer(player, money * amount);
            if (response.type.equals(ResponseType.SUCCESS)) {
                // Create the note and give it to the player
                ItemStack paper = NFCNote.createNFCNote(config.getString("notes.name"), config.getStringList("notes.lore"), config.getString("notes.identifier"), decimalFormat, money, amount);
                player.getInventory().addItem(paper);
                player.sendMessage(Utils.parseMessage(messages.getString("withdraw-successful").replace("{money}", formattedMoney)));
            } else if(response.amount == 0){
                // Insufficient funds
                player.sendMessage(Utils.parseMessage(messages.getString("insufficient-funds")));
            } else {
                // Unexpected error
                player.sendMessage(ChatColor.RED + response.errorMessage);
            }
            // Warn staff if the note's value is higher than the specified in the configuration file
            if (money * amount >= config.getInt("warn-staff-if-value-is-higher-than")) {
                for (Player pl : plugin.getServer().getOnlinePlayers()) {
                    if (pl.hasPermission("nfcnotes.staff.warn") && player != pl) {
                        pl.sendMessage(Utils.parseMessage(messages.getString("staff.warn-withdraw")).replace("{player}", player.getName()).replace("{money}", formattedMoney));
                        plugin.getLogger().info(Utils.parseMessage(messages.getString("staff.warn-withdraw")).replace("{player}", player.getName()).replace("{money}", formattedMoney));
                    }
                }
            }
        }
    }
}
