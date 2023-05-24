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

import java.util.Collections;
import java.util.List;

public enum NFCConfig {
    // Notes
    NOTE_NAME("notes.name", "&aNFCNote"),
    NOTE_LORE("notes.lore", Collections.singletonList("&7Value: &e{money}$")),
    NOTE_MATERIAL("notes.material", "PAPER"),

    NOTE_GLINT_ENABLED("notes.glint.enabled", false),
    NOTE_GLINT_HIDE_ENCHANTMENT_FLAG("notes.glint.hide-enchantment-flag", false),
    NOTE_GLINT_ENCHANTMENT("notes.glint.enchantment", "MENDING"),
    NOTE_GLINT_ENCHANTMENT_LEVEL("notes.glint.enchantment-level", 1),
    NOTE_DECIMAL_FORMAT("notes.decimal-format", "#,###.#"),
    NOTE_UUID("notes.identifier", "9a12cb32-1a7e-4e41-be79-9938528b4375"),
    NOTE_CUSTOM_MODEL_DATA_INTEGER("notes.custom-model-data-integer", 2440573),

    // Use decimals
    USE_DECIMALS("notes.use-decimals", false),

    // Use european format
    USE_EUROPEAN_FORMAT("notes.use-european-format", false),

    // Redeem sound
    REDEEM_SOUND_ENABLED("redeem-sound.enabled", true),
    REDEEM_SOUND("redeem-sound.sound", "BLOCK_CHAIN_BREAK"),
    REDEEM_SOUND_CATEGORY("redeem-sound.sound-category", "AMBIENT"),
    REDEEM_SOUND_VOLUME("redeem-sound.volume", 1d),
    REDEEM_SOUND_PITCH("redeem-sound.pitch", 2d),

    // Modules
    MODULES_WITHDRAW("modules.withdraw.base", true),
    MODULES_MULTIPLE_WITHDRAW("modules.withdraw.multiple-withdraw", true),
    MODULES_WITHDRAW_ALL("modules.withdraw.withdraw-all", true),
    MODULES_DEPOSIT_ACTION("modules.deposit.action.base", true),
    MODULES_MASSDEPOSIT("modules.deposit.action.massdeposit", true),
    MODULES_DEPOSIT_COMMAND("modules.deposit.command.deposit-one", true),
    MODULES_DEPOSIT_ONE("modules.deposit.command.deposit-all", true),
    MODULES_DEPOSIT_MULTIPLE("modules.deposit.command.deposit-multiple", true),
    MODULES_DEPOSIT_STACK("modules.deposit.command.deposit-stack", true),
    MODULES_WARN_STAFF("modules.warn-staff", true),
    MODULES_COUNT_SAME_PLAYER("modules.count.same-player", true),
    MODULES_COUNT_OTHER_PLAYERS("modules.count.other-players", true),

    // Other
    SHOW_PLUGIN_INFO("show-plugin-info", true),

    // Economy plugin
    ECONOMY_PLUGIN("economy-plugin", "Vault"),

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

    NFCConfig(String value, double def){
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

    public Double getDouble(){ return config.getDouble((String) this.value, (double) this.def); }

    public boolean getBoolean(){ return config.getBoolean((String) this.value, (boolean) this.def); }

    public List<String> getList(){ return config.getStringList((String) this.value); }

    public static void setConfig(Configuration config){
        NFCConfig.config = config;
    }
}
