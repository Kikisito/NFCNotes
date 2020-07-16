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

package es.kikisito.nfcnotes.enums;

import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.configuration.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum NFCConfig {
    // Notes
    NOTE_NAME("notes.name", "&aNFCNote"),
    NOTE_LORE("notes.lore", Arrays.asList("&7Value: &e{money}$")),
    NOTE_MATERIAL("notes.material", "PAPER"),
    NOTE_DECIMAL_FORMAT("notes.decimal-format", "#,###.#"),
    NOTE_UUID("notes.identifier", "9a12cb32-1a7e-4e41-be79-9938528b4375"),

    // Modules
    MODULES_WITHDRAW("modules.withdraw", true),
    MODULES_DEPOSIT_ACTION("modules.deposit.action", true),
    MODULES_DEPOSIT_COMMAND("modules.deposit.command", true),
    MODULES_MASSDEPOSIT("modules.massdeposit", true),
    MODULES_MULTIPLE_WITHDRAW("modules.multiple-withdraw", true),
    MODULES_WITHDRAW_ALL("modules.withdraw-all", true),
    MODULES_WARN_STAFF("modules.warn-staff", true),
    MODULES_SHOW_PLUGIN_INFO("modules.show-plugin-info", true),

    // Disabled worlds
    DISABLED_WORLDS("disabled-worlds", Collections.EMPTY_LIST),

    // Disabled tables
    DISABLED_TABLES("disabled-tables", Collections.EMPTY_LIST),

    // Warn staff if the value of any action is more than the specified
    WARN_VALUE_LIMIT("warn-staff-if-value-is-higher-than", 100000),

    // Update Checker
    UPDATE_CHECKER_IS_ENABLED("update-checker.enable", true),
    UPDATE_CHECKER_NOTIFY_ON_JOIN("update-checker.notify-on-join", true),

    // Config version
    VERSION("config-version", 0);


    private static Configuration config;
    private Object value;
    private Object def;

    NFCConfig(String value, String def) {
        this.value = value;
        this.def = def;
    }

    NFCConfig(String value, int def){
        this.value = value;
        this.def = def;
    }

    NFCConfig(String value, boolean def){
        this.value = value;
        this.def = def;
    }

    NFCConfig(String value, List<String> def){
        this.value = value;
        this.def = def;
    }

    public String getString(){
        return Utils.parseMessage(config.getString((String) this.value, (String) this.def));
    }

    public int getInt(){ return config.getInt((String) this.value, (int) this.def); }

    public boolean getBoolean(){ return config.getBoolean((String) this.value, (boolean) this.def); }

    public List<String> getList(){ return config.getStringList((String) this.value); }

    public static void setConfig(Configuration config){
        NFCConfig.config = config;
    }
}
