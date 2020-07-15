package es.kikisito.nfcnotes.utils;

import org.bukkit.configuration.file.FileConfiguration;

public enum Messages {
    TEST("test-text", "Test text lol");

    private static FileConfiguration messages;
    private String message;
    private String def;

    Messages(String message, String def) {
        this.message = message;
        this.def = def;
    }

    public String getString(){
        return Utils.parseMessage(messages.getString(this.message, this.def));
    }

    public static void setMessagesFile(FileConfiguration messages){
        Messages.messages = messages;
    }
}
