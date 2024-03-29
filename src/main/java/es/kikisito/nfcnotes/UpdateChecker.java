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

package es.kikisito.nfcnotes;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final Main plugin;

    public UpdateChecker(Main plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        try (
                InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=80976").openStream();
                Scanner scanner = new Scanner(inputStream)
        ) {
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
        } catch (IOException exception) {
            plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
        }
    }
}
