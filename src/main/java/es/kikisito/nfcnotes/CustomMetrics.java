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

import es.kikisito.nfcnotes.enums.NFCConfig;
import org.bstats.bukkit.Metrics;

public class CustomMetrics {
    public CustomMetrics(Main plugin, Metrics metrics) {
        // Material used
        metrics.addCustomChart(new Metrics.SimplePie("material_used", NFCConfig.NOTE_MATERIAL::getString));
        // Decimal Format
        metrics.addCustomChart(new Metrics.SimplePie("decimal_format", NFCConfig.NOTE_DECIMAL_FORMAT::getString));
        // Does the plugin warn staff?
        metrics.addCustomChart(new Metrics.SimplePie("warn_staff_enabled", () -> {
            String warn_staff = "Disabled";
            if(NFCConfig.MODULES_WARN_STAFF.getBoolean()) warn_staff = "Enabled";
            return warn_staff;
        }));
        // Is Update Checker enabled?
        metrics.addCustomChart(new Metrics.SimplePie("update_checker_enabled", () -> {
            String update_checker = "Disabled";
            if(NFCConfig.MODULES_WARN_STAFF.getBoolean()) update_checker = "Enabled";
            return update_checker;
        }));
    }
}
