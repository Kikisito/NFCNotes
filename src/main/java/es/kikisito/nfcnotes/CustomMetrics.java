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
import org.bstats.charts.SimplePie;

public class CustomMetrics {
    public CustomMetrics(Main plugin, Metrics metrics) {
        // Material used
        metrics.addCustomChart(new SimplePie("material_used", NFCConfig.NOTE_MATERIAL::getString));
        // Decimal Format
        metrics.addCustomChart(new SimplePie("decimal_format", NFCConfig.NOTE_DECIMAL_FORMAT::getString));
        // Does the plugin warn staff?
        metrics.addCustomChart(new SimplePie("warn_staff_enabled", () -> {
            String warn_staff = "Disabled";
            if (NFCConfig.MODULES_WARN_STAFF.getBoolean()) warn_staff = "Enabled";
            return warn_staff;
        }));
        // Is Update Checker enabled?
        metrics.addCustomChart(new SimplePie("update_checker_enabled", () -> {
            String update_checker = "Disabled";
            if (NFCConfig.MODULES_WARN_STAFF.getBoolean()) update_checker = "Enabled";
            return update_checker;
        }));
        // Economy Plugin
        metrics.addCustomChart(new SimplePie("economy_plugin_used", NFCConfig.ECONOMY_PLUGIN::getString));
        // Are decimals used?
        metrics.addCustomChart(new SimplePie("use_of_decimals", () -> {
            if (NFCConfig.USE_DECIMALS.getBoolean()) return "Yes";
            else return "No";
        }));
        // Decimal format used (if decimals are used)
        metrics.addCustomChart(new SimplePie("number_format", () -> {
            if (NFCConfig.USE_EUROPEAN_FORMAT.getBoolean()) return "European/IS format";
            else return "American format";
        }));
    }
}
