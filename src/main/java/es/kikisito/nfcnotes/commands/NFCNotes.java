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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

public class NFCNotes implements CommandExecutor {
    private Main plugin;
    private Configuration config;
    private FileConfiguration messages;

    public NFCNotes(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        switch(args.length){
            case 1:
                if(args[0].equalsIgnoreCase("reload")){
                    if(sender.hasPermission("nfcnotes.staff.reload")) {
                        plugin.reloadPlugin();
                        sender.sendMessage(plugin.parseMessage(messages.getString("staff.plugin-reloaded")));
                    } else {
                        sender.sendMessage(plugin.parseMessage(messages.getString("no-permission")));
                    }
                }
                break;
            default:
                if(config.getBoolean("modules.show-plugin-info")){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Developed by &6Kikisito"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Version &6" + plugin.getDescription().getVersion()));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &7Get more information at"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6NFCNotes&8] &6https://github.com/Kikisito/NFCNotes"));
                }
                break;
        }
        return false;
    }
}
