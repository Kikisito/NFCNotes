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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public enum NFCMessages {
    ONLY_PLAYERS("only-players", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Only players can execute this command.</gray>"),
    FULL_INVENTORY("full-inventory", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Your inventory is full. Store some items and try again.</gray>"),
    NOT_ENOUGH_SPACE("not-enough-space", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You don't have enough space in your inventory.</gray>"),
    NO_PERMISSION("no-permission", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You're not allowed to do that.</gray>"),
    WITHDRAW_USAGE("withdraw-usage", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Usage: /withdraw <money> [amount]</gray>"),
    CREATENOTE_USAGE("createnote-usage", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Usage: /createnote [player] <money> [amount]</gray>"),
    ONLY_INTEGERS("only-integers", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Use an integer number.</gray>"),
    USE_A_NUMBER_HIGHER_THAN_ZERO("use-a-number-higher-than-zero", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Use a number higher than <gold>0</gold></gray>"),
    WITHDRAW_SUCCESSFUL("withdraw-successful", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You withdrawn <gold>{money}</gold>$.</gray>"),
    CREATENOTE_SUCCESSFUL("createnote-successful", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You created a note with a value of <gold>{money}</gold>$.</gray>"),
    INSUFFICIENT_FUNDS("insufficient-funds", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You don't have enough money.</gray>"),
    DEPOSIT_SUCCESSFUL("deposit-successful", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You redeemed <gold>{money}</gold>$.</gray>"),
    MASSDEPOSIT_SUCCESSFUL("massdeposit-successful", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You redeemed <gold>{money}</gold>$.</gray>"),
    UNEXPECTED_ERROR("unexpected-error", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>An unexpected error has occurred. This action has been cancelled.</gray>"),
    DEPOSIT_USAGE("deposit-usage", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Usage: /deposit [all/stack/amount of notes]</gray>"),
    NO_NOTES_FOUND("no-notes-found", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>No notes have been found in your inventory.</gray>"),
    NOT_A_NOTE("not-a-note", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>This item is not a note. You can't redeem it.</gray>"),
    DISABLED_WORLD("disabled-world", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Notes are disabled in this world.</gray>"),
    STAFF_WARN_WITHDRAW("staff.warn-withdraw", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>{player} has withdrawn <gold>{money}</gold>$.</gray>"),
    STAFF_WARN_DEPOSIT("staff.warn-deposit", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>{player} has redeemed <gold>{money}</gold>$.</gray>"),
    STAFF_PLUGIN_RELOADED("staff.plugin-reloaded", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>NFCNotes has successfully restarted.</gray>"),
    UPDATES_UPDATE_AVAILABLE("updates.update-available", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>NFCNotes {version} is available. Click here to download it.</gray>"),
    UPDATES_NO_UPDATES("updates.no-updates", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You are using the latest version.</gray>"),
    MODULE_DISABLED("module-disabled", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>This action is currently disabled.</gray>"),
    INSUFFICIENT_NOTES("insufficient-notes", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You don't have enough notes.</gray>"),
    INCORRECT_FORMAT("incorrect-format", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Incorrect format. Use dots <white><italic>.</italic></white> to separate decimals.</gray>"),
    PLAYER_NOT_FOUND("player-not-found", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Player {player} could not be found.</gray>"),
    COUNT_USAGE("count-usage", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>Usage: /count [player]</gray>"),
    COUNT_SELF("count-self", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gray>You have <gold>{money}</gold>$ in your inventory.</gray>"),
    COUNT_OTHER("count-other", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gold>{player}</gold><gray> has <gold>{money}</gold>$ in their inventory.</gray>"),
    NOTE_CONVERTED("note-converted", "<dark_gray>[<gold>NFCNotes</gold>]</dark_gray> <gold>You tried to use an outdated note, but we have automatically updated it for you! If you want to redeem it, click it again.</gold>"),

    VERSION("messages-version", 0);

    private static FileConfiguration messages;
    private final Object message;
    private final Object def;

    private final MiniMessage mm = MiniMessage.miniMessage();

    NFCMessages(String message, String def) {
        this.message = message;
        this.def = def;
    }

    NFCMessages(String message, int def){
        this.message = message;
        this.def = def;
    }

    // No placeholders
    public Component getString(){
        // Messages files from version 10 use components. Below that, they use legacy strings. COMPATIBILITY MODE, this will be removed.
        if(NFCMessages.VERSION.getInt() < 10) return LegacyComponentSerializer.legacyAmpersand().deserialize(messages.getString((String) this.message, (String) this.def));
        else return mm.deserialize(messages.getString((String) this.message, (String) this.def));
    }

    // With placeholders
    public Component getString(Map<String, String> placeholders) {
        String text = messages.getString((String) this.message, (String) this.def);

        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        // Messages files from version 10 use components. Below that, they use legacy strings. COMPATIBILITY MODE, this will be removed.
        if(NFCMessages.VERSION.getInt() < 10) return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        else return mm.deserialize(text);
    }

    public Component getString(String... keyValuePairs){
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide pairs of placeholder keys and values");
        }

        Map<String, String> placeholders = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            placeholders.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }

        return getString(placeholders);
    }

    public String getLegacyString(){
        return LegacyComponentSerializer.legacySection().serialize(getString());
    }

    public String getLegacyString(Map<String, String> placeholders){
        return LegacyComponentSerializer.legacySection().serialize(getString(placeholders));
    }

    public String getLegacyString(String... keyValuePairs) {
        return LegacyComponentSerializer.legacySection().serialize(getString(keyValuePairs));
    }

    public static Component getClickableComponent(String action, String url, Component component) {
        return switch (action) {
            case "open_url" -> component.clickEvent(ClickEvent.openUrl(url));
            case "run_command" -> component.clickEvent(ClickEvent.runCommand(url));
            case "suggest_command" -> component.clickEvent(ClickEvent.suggestCommand(url));
            case "copy_to_clipboard" -> component.clickEvent(ClickEvent.copyToClipboard(url));
            default -> component;
        };
    }

    public int getInt(){ return messages.getInt((String) this.message, (int) this.def);}

    public static void setMessagesFile(FileConfiguration messages){
        NFCMessages.messages = messages;
    }
}
