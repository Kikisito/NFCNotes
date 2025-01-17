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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NFCNotes implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public NFCNotes(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // Cast to Adventure Audience
        Audience audience = plugin.getAdventure().sender(sender);

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("nfcnotes.staff.reload")) {
                plugin.reloadPlugin();
                audience.sendMessage(NFCMessages.STAFF_PLUGIN_RELOADED.getString());
            } else if ((args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update")) && sender.hasPermission("nfcnotes.staff.check-updates")) {
                new UpdateChecker(plugin).getVersion((version) -> {
                    if (!plugin.getDescription().getVersion().equals(version)) {
                        audience.sendMessage(NFCMessages.getClickableComponent("open_url", "https://github.com/Kikisito/NFCNotes/releases/", NFCMessages.UPDATES_UPDATE_AVAILABLE.getString("{version}", version)));
                    } else {
                        audience.sendMessage(NFCMessages.UPDATES_NO_UPDATES.getString());
                    }

                });
            } else {
                audience.sendMessage(NFCMessages.NO_PERMISSION.getString());
            }
        } else if (NFCConfig.SHOW_PLUGIN_INFO.getBoolean()) {
            audience.sendMessage(mm.deserialize("<dark_gray>[</dark_gray><gold>NFCNotes</gold><dark_gray>]</dark_gray> <gray>Developed by</gray> <gold>Kikisito</gold>"));
            audience.sendMessage(mm.deserialize("<dark_gray>[</dark_gray><gold>NFCNotes</gold><dark_gray>]</dark_gray> <gray>Version</gray> <gold>" + plugin.getDescription().getVersion() + "</gold>"));
            audience.sendMessage(mm.deserialize("<dark_gray>[</dark_gray><gold>NFCNotes</gold><dark_gray>]</dark_gray> <gray>Get more information at</gray>"));
            audience.sendMessage(mm.deserialize("<dark_gray>[</dark_gray><gold>NFCNotes</gold><dark_gray>]</dark_gray> <gold>https://github.com/Kikisito/NFCNotes</gold>"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(args.length != 1) return Collections.emptyList();
        List<String> options = Arrays.asList("reload", "check", "update");
        List<String> tab = new ArrayList<>();
        for(String s : options){
            if(s.startsWith(args[0])) tab.add(s);
        }
        return tab;
    }
}
