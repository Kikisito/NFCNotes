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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.NFCNote;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class CreateNote implements CommandExecutor, TabExecutor {
    private final Main plugin;

    public CreateNote(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // Cast to Adventure Audience
        Audience audience = plugin.getAdventure().sender(sender);

        // Check if the player is allowed to withdraw money and its inventory is not full
        if (!sender.hasPermission("nfcnotes.staff.createnote")) {
            audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
            return false;
        } else if (sender instanceof Player && ((Player) sender).getInventory().firstEmpty() == -1) {
            audience.sendMessage(NFCMessages.FULL_INVENTORY.getString());
            return false;
        }
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    if(sender instanceof Player p) {
                        money = Double.parseDouble(args[0]);
                        createNote(audience, p, money, 1);
                    } else audience.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
                    break;
                case 2:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        createNote(audience, player, money, 1);
                    } else {
                        if(sender instanceof Player p) {
                            money = Double.parseDouble(args[0]);
                            amount = Integer.parseInt(args[1]);
                            createNote(audience, p, money, amount);
                        } else audience.sendMessage(NFCMessages.ONLY_PLAYERS.getString());
                    }
                    break;
                case 3:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        amount = Integer.parseInt(args[2]);
                        createNote(audience, player, money, amount);
                        break;
                    }
                default:
                    audience.sendMessage(NFCMessages.CREATENOTE_USAGE.getString());
                    break;
            }
        } catch (NumberFormatException ex) {
            audience.sendMessage(NFCMessages.INCORRECT_FORMAT.getString());
        }
        return true;
    }

    private void createNote(Audience audience, Player p, Double m, Integer a){
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

        // Make the amount readable
        DecimalFormat decimalFormat = Utils.getDecimalFormat();
        String formattedMoney = decimalFormat.format(m);
        // Create the note and give it to the player
        ItemStack paper = NFCNote.createNFCNoteItem(this.plugin, NFCConfig.NOTE_NAME.getMessage(), NFCConfig.NOTE_LORE.getMessages(), NFCConfig.NOTE_MATERIAL.getString(), p.getName(), decimalFormat, m, a);
        p.getInventory().addItem(paper);
        audience.sendMessage(NFCMessages.CREATENOTE_SUCCESSFUL.getString("{money}", formattedMoney));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length != 1) return Collections.emptyList();
        List<String> tab = new ArrayList<>();
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(p.getName().startsWith(args[0])) tab.add(p.getName());
        }
        return tab;
    }

    private int countFreeSlots(PlayerInventory inventory) {
        int freeSlots = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) freeSlots++;
        }
        return freeSlots;
    }
}
