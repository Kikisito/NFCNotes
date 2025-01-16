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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Count implements CommandExecutor, TabExecutor {
    private final Main plugin;

    public Count(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {                List<ItemStack> notes = new ArrayList<>();
        if(args.length == 0 && !(sender instanceof Player)){
            // Only players are allowed to use /count
            sender.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
        } else if (args.length == 0){
            // Check if this module is enabled
            if(NFCConfig.MODULES_COUNT_SAME_PLAYER.getBoolean()){
                // If sender instanceof Player
                Player player = (Player) sender;
                // Check if player is allowed to do this
                if(player.hasPermission("nfcnotes.count.self")){
                    double value = this.checkNotes(Objects.requireNonNull(player, "Player must not be null"));
                    // Send value to sender
                    player.sendMessage(NFCMessages.COUNT_SELF.getString().replace("{money}", this.getFormattedValue(value)));
                } else {
                    player.sendMessage(NFCMessages.NO_PERMISSION.getString());
                }
            } else {
                sender.sendMessage(NFCMessages.MODULE_DISABLED.getString());
            }
        } else if (args.length == 1){
            // Check if this module is enabled
            if(NFCConfig.MODULES_COUNT_OTHER_PLAYERS.getBoolean()){
                // Check if sender has permission
                if(sender.hasPermission("nfcnotes.staff.count.others")){
                    // Check if player is online
                    if(plugin.getServer().getPlayer(args[0]) != null){
                        // Get player and all his notes
                        Player player = plugin.getServer().getPlayer(args[0]);
                        double value = this.checkNotes(Objects.requireNonNull(player, "Player must not be null"));
                        // Send value to sender
                        sender.sendMessage(NFCMessages.COUNT_OTHER.getString().replace("{money}", this.getFormattedValue(value)).replace("{player}", player.getName()));
                    } else {
                        // No online player could be found
                        sender.sendMessage(NFCMessages.PLAYER_NOT_FOUND.getString().replace("{player}", args[0]));
                    }
                } else {
                    sender.sendMessage(NFCMessages.NO_PERMISSION.getString());
                }
            } else {
                sender.sendMessage(NFCMessages.MODULE_DISABLED.getString());
            }
        } else {
            sender.sendMessage(NFCMessages.COUNT_USAGE.getString());
        }
        return true;
    }

    /**
     * Returns the value of all notes found in a player's inventory
     * @param player Player to check
     * @return Value (in double) of all the notes that have been found
     */
    private double checkNotes(Player player){
        double value = 0;
        // Checks for notes in player's inventory
        for (ItemStack i : player.getInventory()) {
            if (NFCNote.isNFCNote(this.plugin, i)) {
                NFCNote nfcNote = new NFCNote(this.plugin, i);
                double amount = nfcNote.getValue() * i.getAmount();
                value += amount;
            }
        }
        return value;
    }

    /**
     * Returns a double with the DecimalFormat set in the configuration file
     * @param value Amount to be formatted
     * @return Formatted double
     */
    private String getFormattedValue(double value){
        // Make the value use DecimalFormat
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString(), decimalFormatSymbols);
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat.format(value);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> tab = new ArrayList<>();
        if(args.length == 1 && NFCConfig.MODULES_COUNT_OTHER_PLAYERS.getBoolean() && sender.hasPermission("nfcnotes.staff.count.others")){
            for(Player p : plugin.getServer().getOnlinePlayers()){
                if(p.getName().toLowerCase().startsWith(args[0].toLowerCase())){
                    tab.add(p.getName());
                }
            }
        }
        return tab;
    }
}
