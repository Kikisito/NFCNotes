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
import es.kikisito.nfcnotes.UpdateChecker;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.enums.NFCMessages;
import es.kikisito.nfcnotes.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NFCNotes implements CommandExecutor, TabCompleter {
    private final Main plugin;

    public NFCNotes(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("nfcnotes.staff.reload")) {
                    plugin.reloadPlugin();
                    sender.sendMessage(Utils.parseMessage(NFCMessages.STAFF_PLUGIN_RELOADED.getString()));
                } else {
                    sender.sendMessage(Utils.parseMessage(messages.getString("no-permission")));
                }
            } else if ((args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update")) && sender.hasPermission("nfcnotes.staff.check-updates")) {
                new UpdateChecker(plugin).getVersion((version) -> {
                    if (!plugin.getDescription().getVersion().equals(version)) {
                        TextComponent msg = new TextComponent(Utils.parseMessage(NFCMessages.UPDATES_UPDATE_AVAILABLE.getString().replace("{version}", version)));
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/1-13-1-16-nfcnotes.80976/"));
                        sender.spigot().sendMessage(msg);
                    } else {
                        sender.sendMessage(NFCMessages.UPDATES_NO_UPDATES.getString());
                    }

                });
            } else {
                sender.sendMessage(NFCMessages.NO_PERMISSION.getString());
            }
        } else {
            if (NFCConfig.SHOW_PLUGIN_INFO.getBoolean()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Developed by &6Kikisito"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Version &6" + plugin.getDescription().getVersion()));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Get more information at"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &6https://github.com/Kikisito/NFCNotes"));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length != 1) return Collections.emptyList();
        List<String> options = Arrays.asList("reload", "check", "update");
        List<String> tab = new ArrayList<>();
        for(String s : options){
            if(s.startsWith(args[0])) tab.add(s);
        }
        return tab;
    }
}
