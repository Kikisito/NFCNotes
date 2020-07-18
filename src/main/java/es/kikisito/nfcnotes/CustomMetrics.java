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
