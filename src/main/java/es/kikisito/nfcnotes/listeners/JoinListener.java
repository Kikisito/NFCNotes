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

package es.kikisito.nfcnotes.listeners;

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.UpdateChecker;
import es.kikisito.nfcnotes.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private Main plugin;
    private Configuration config;

    public JoinListener(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void checkUpdates(PlayerJoinEvent e) {
        FileConfiguration messages = plugin.getMessages();
        if (e.getPlayer().hasPermission("nfcnotes.staff.check-updates")) {
            new UpdateChecker(plugin).getVersion((version) -> {
                if (!plugin.getDescription().getVersion().equals(version)) {
                    TextComponent msg = new TextComponent(Utils.parseMessage(messages.getString("updates.update-available").replace("{version}", version)));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/1-13-1-16-nfcnotes.80976/"));
                    e.getPlayer().spigot().sendMessage(msg);
                }
            });
        }

    }
}
