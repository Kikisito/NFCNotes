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
import es.kikisito.nfcnotes.enums.NFCMessages;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final Main plugin;

    public JoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void checkUpdates(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("nfcnotes.staff.check-updates")) {
            new UpdateChecker(plugin).getVersion((version) -> {
                if (!plugin.getDescription().getVersion().equals(version)) {
                    Audience audience = plugin.getAdventure().player(e.getPlayer());
                    audience.sendMessage(NFCMessages.getClickableComponent("open_url", "https://github.com/Kikisito/NFCNotes/releases/", NFCMessages.UPDATES_UPDATE_AVAILABLE.getString("{version}", version)));
                }
            });
        }

    }
}
