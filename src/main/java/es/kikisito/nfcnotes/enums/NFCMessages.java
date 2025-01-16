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
import org.bukkit.configuration.file.FileConfiguration;

public enum NFCMessages {
    ONLY_PLAYERS("only-players", "&8[&6NFCNotes&8] &7Only players can execute this command."),
    FULL_INVENTORY("full-inventory", "&8[&6NFCNotes&8] &7Your inventory is full. Store some items and try again."),
    NOT_ENOUGH_SPACE("not-enough-space", "&8[&6NFCNotes&8] &7You don't have enough space in your inventory."),
    NO_PERMISSION("no-permission", "&8[&6NFCNotes&8] &7You're not allowed to do that."),
    WITHDRAW_USAGE("withdraw-usage", "&8[&6NFCNotes&8] &7Usage: /withdraw <money> [amount]"),
    CREATENOTE_USAGE("createnote-usage", "&8[&6NFCNotes&8] &7Usage: /createnote [player] <money> [amount]"),
    ONLY_INTEGERS("only-integers", "&8[&6NFCNotes&8] &7Use an integer number."),
    USE_A_NUMBER_HIGHER_THAN_ZERO("use-a-number-higher-than-zero", "&8[&6NFCNotes&8] &7Use a number higher than &60&7."),
    WITHDRAW_SUCCESSFUL("withdraw-successful", "&8[&6NFCNotes&8] &7You withdrawn &6{money}&7$."),
    CREATENOTE_SUCCESSFUL("createnote-successful", "&8[&6NFCNotes&8] &7You created a note with a value of &6{money}&7$."),
    INSUFFICIENT_FUNDS("insufficient-funds", "&8[&6NFCNotes&8] &7You don't have enough money."),
    DEPOSIT_SUCCESSFUL("deposit-successful", "&8[&6NFCNotes&8] &7You redeemed &6{money}&7$."),
    MASSDEPOSIT_SUCCESSFUL("massdeposit-successful", "&8[&6NFCNotes&8] &7You redeemed &6{money}&7$."),
    UNEXPECTED_ERROR("unexpected-error", "&8[&6NFCNotes&8] &7An unexpected error has occurred. This action has been cancelled."),
    DEPOSIT_USAGE("deposit-usage", "&8[&6NFCNotes&8] &7Usage: /deposit [all/stack/amount of notes]"),
    NO_NOTES_FOUND("no-notes-found", "&8[&6NFCNotes&8] &7No notes have been found in your inventory."),
    NOT_A_NOTE("not-a-note", "&8[&6NFCNotes&8] &7No notes have been found in your inventory."),
    DISABLED_WORLD("disabled-world", "&8[&6NFCNotes&8] &7Notes are disabled in this world."),
    STAFF_WARN_WITHDRAW("staff.warn-withdraw", "&8[&6NFCNotes&8] &7{player} has withdrawn &6{money}&7$."),
    STAFF_WARN_DEPOSIT("staff.warn-deposit", "&8[&6NFCNotes&8] &7{player} has redeemed &6{money}&7$."),
    STAFF_PLUGIN_RELOADED("staff.plugin-reloaded", "&8[&6NFCNotes&8] &7NFCNotes has successfully restarted."),
    UPDATES_UPDATE_AVAILABLE("updates.update-available", "&8[&6NFCNotes&8] &7NFCNotes {version} is available. Click here to download it."),
    UPDATES_NO_UPDATES("updates.no-updates", "&8[&6NFCNotes&8] &7You are using the latest version."),
    MODULE_DISABLED("module-disabled", "&8[&6NFCNotes&8] &7This action is currently disabled."),
    INSUFFICIENT_NOTES("insufficient-notes", "&8[&6NFCNotes&8] &7You don't have enough notes."),
    INCORRECT_FORMAT("incorrect-format", "&8[&6NFCNotes&8] &7Incorrect format. Use dots &f&o.&7 to separate decimals."),
    PLAYER_NOT_FOUND("player-not-found", "&8[&6NFCNotes&8] &7Player {player} could not be found."),
    COUNT_USAGE("count-usage", "&8[&6NFCNotes&8] &7Usage: /count [player]"),
    COUNT_SELF("count-self", "&8[&6NFCNotes&8] &7You have &6{money}&7$ in your inventory."),
    COUNT_OTHER("count-other", "&8[&6NFCNotes&8] &6{player}&7 has &6{money}&7$ in their inventory."),
    NOTE_CONVERTED("note-converted", "&8[&6NFCNotes&8] &6You tried to use an outdated note, but we have automatically updated it for you! If you want to redeem it, click it again."),

    VERSION("messages-version", 0);


    private static FileConfiguration messages;
    private final Object message;
    private final Object def;

    NFCMessages(String message, String def) {
        this.message = message;
        this.def = def;
    }

    NFCMessages(String message, int def){
        this.message = message;
        this.def = def;
    }

    public String getString(){
        return Utils.parseMessage(messages.getString((String) this.message, (String) this.def));
    }

    public int getInt(){ return messages.getInt((String) this.message, (int) this.def);}

    public static void setMessagesFile(FileConfiguration messages){
        NFCMessages.messages = messages;
    }
}
