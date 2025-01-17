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

import es.kikisito.nfcnotes.commands.*;
import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.listeners.BlockPlaceListener;
import es.kikisito.nfcnotes.listeners.CraftListener;
import es.kikisito.nfcnotes.listeners.InteractListener;
import es.kikisito.nfcnotes.listeners.JoinListener;
import es.kikisito.nfcnotes.enums.NFCMessages;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    private Configuration config;
    private FileConfiguration messages;
    @Deprecated
    private String economyPlugin;
    // Vault eco API
    private Economy eco;

    public List<Object> forbiddeninventories = new ArrayList<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.loadMessages();
        config = this.getConfig();
        NFCConfig.setConfig(config);
        NFCMessages.setMessagesFile(messages);

        if(NFCConfig.UPDATE_CHECKER_IS_ENABLED.getBoolean()) {
            new UpdateChecker(this).getVersion((version) -> {
                if (!getDescription().getVersion().equals(version)) {
                    this.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "A new version of NFCNotes is available.");
                    this.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Version installed: " + this.getDescription().getVersion() + ". Latest version: " + version);
                    this.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Download it from " + ChatColor.GOLD + "https://github.com/Kikisito/NFCNotes/releases");
                }
            });
        }

        if(!isEconomy()){
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "-------------------------------------------------------------");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "NFCNotes couldn't detect any economy plugin in your server.");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "If you are using Vault, check that you have installed an economy plugin");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "-------------------------------------------------------------");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(NFCConfig.VERSION.getInt() < 12) {
            String outdatedconfig = ChatColor.RED + "Your NFCNotes configuration is outdated. Please, update it or some features will be missed and support won't be provided.";
            this.getServer().getConsoleSender().sendMessage(outdatedconfig);
            // In case of this plugin being reloaded using Plugman or ServerUtils.
            for(Player player : this.getServer().getOnlinePlayers()) if(player.isOp()) player.sendMessage(outdatedconfig);
        }

        if(NFCMessages.VERSION.getInt() < 7) {
            String outdatedmsgs = ChatColor.RED + "Your NFCNotes messages file is outdated. Please, update it or some features will be missed and support won't be provided.";
            this.getServer().getConsoleSender().sendMessage(outdatedmsgs);
            // In case of this plugin being reloaded using Plugman or ServerUtils.
            for(Player player : this.getServer().getOnlinePlayers()) if(player.isOp()) player.sendMessage(outdatedmsgs);
        }

        this.getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CraftListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        if(NFCConfig.UPDATE_CHECKER_NOTIFY_ON_JOIN.getBoolean()) this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        this.getCommand("withdraw").setExecutor(new Withdraw(this));
        this.getCommand("createnote").setExecutor(new CreateNote(this));
        this.getCommand("nfcnotes").setExecutor(new NFCNotes(this));
        this.getCommand("deposit").setExecutor(new Deposit(this));
        this.getCommand("count").setExecutor(new Count(this));

        Metrics metrics = new Metrics(this, 8048);
        new CustomMetrics(this, metrics);

        // Set forbidden inventories
        for(String invtype : NFCConfig.DISABLED_TABLES.getList()){
            this.forbiddeninventories.add(InventoryType.valueOf(invtype));
        }
    }

    private boolean isEconomy() {
        try {
            if (NFCConfig.ECONOMY_PLUGIN.getString().equalsIgnoreCase("Vault")) {
                RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    return false;
                } else {
                    eco = rsp.getProvider();
                    economyPlugin = "Vault";
                    return true;
                }
            } else if (NFCConfig.ECONOMY_PLUGIN.getString().equalsIgnoreCase("Essentials")) {
                this.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Essentials Economy won't be supported anymore soon in the future. Please, switch to a Vault compatible economy plugin as soon as possible.");
                economyPlugin = "Essentials";
                return true;
            }
            return false;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public Economy getVaultEco() { return this.eco; }

    @Deprecated
    public String getEco() { return this.economyPlugin; }

    public FileConfiguration getMessages() { return this.messages; }

    public void loadMessages() {
        try {
            File messages = new File(getDataFolder(), "messages.yml");
            if (!messages.exists()) {
                messages.getParentFile().mkdirs();
                this.saveResource("messages.yml", false);
            }
            this.messages = new YamlConfiguration();
            this.messages.load(messages);
            NFCMessages.setMessagesFile(this.messages);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadPlugin(){
        try {
            File config = new File(getDataFolder(), "config.yml");
            if(!config.exists()){
                config.getParentFile().mkdirs();
                this.saveResource("config.yml", false);
            }
            this.getConfig().load(config);
            loadMessages();

            // Reset forbidden inventories
            this.forbiddeninventories = new ArrayList<>();
            for(String invtype : NFCConfig.DISABLED_TABLES.getList()){
                this.forbiddeninventories.add(InventoryType.valueOf(invtype));
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
