package es.kikisito.nfcnotes;

import es.kikisito.nfcnotes.enums.NFCConfig;
import org.bstats.bukkit.Metrics;

public class CustomMetrics {
    public CustomMetrics(Main plugin, Metrics metrics) {
        metrics.addCustomChart(new Metrics.SimplePie("used_language", NFCConfig.NOTE_MATERIAL::getString));
    }
}
