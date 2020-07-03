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

import es.kikisito.nfcnotes.commands.CreateNote;
import es.kikisito.nfcnotes.commands.NFCNotes;
import es.kikisito.nfcnotes.commands.Withdraw;
import es.kikisito.nfcnotes.listeners.InteractListener;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaPlugin implements Listener {

    private Configuration config;
    private FileConfiguration messages;
    private static Economy eco;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.loadMessages();
        config = this.getConfig();
        if(!getVault()){
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "-------------------------------------------------------------");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "NFCNotes couldn't detect Vault in your server.");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please, download Vault from " + ChatColor.GOLD + "https://github.com/MilkBowl/Vault");
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "-------------------------------------------------------------");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        this.getCommand("withdraw").setExecutor(new Withdraw(this));
        this.getCommand("createnote").setExecutor(new CreateNote(this));
        this.getCommand("nfcnotes").setExecutor(new NFCNotes(this));
        Metrics metrics = new Metrics(this, 8048);
    }

    private boolean getVault() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp == null){
                return false;
            } else {
                eco = rsp.getProvider();
                return true;
            }
        }
    }

    public static Economy getEco() { return eco; }

    public FileConfiguration getMessages() { return this.messages; }

    public void loadMessages() {
        File messages = new File(getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            messages.getParentFile().mkdirs();
            this.saveResource("messages.yml", false);
        }
        this.messages = new YamlConfiguration();
        try {
            this.messages.load(messages);
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
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNote(ItemStack item){
        ItemMeta im = item.getItemMeta();
        return im.hasAttributeModifiers() && im.getAttributeModifiers(Attribute.GENERIC_LUCK).iterator().next().getName().equalsIgnoreCase("noteValue");
    }

    public ItemStack createNote(String formattedMoney, Double money, Integer amount){
        ItemStack is = new ItemStack(Material.PAPER, amount);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(this.parseMessage(config.getString("notes.name")).replace("{money}", formattedMoney));
        // Parse lore
        List<String> lore = new ArrayList<>();
        for(String s : config.getStringList("notes.lore")){
            lore.add(this.parseMessage(s).replace("{money}", formattedMoney));
        }
        im.setLore(lore);
        // Note value is stored as an Attribute and then it's hidden, so its name and lore can be safely edited or removed
        im.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier(UUID.fromString(config.getString("notes.identifier")), "noteValue", money, AttributeModifier.Operation.ADD_NUMBER));
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(im);
        return is;
    }

    public String parseMessage(String string){
        String finalmessage;
        Integer version = null;
        // Check version
        Pattern n = Pattern.compile("^(\\d)\\.(\\d+)");
        Matcher nm = n.matcher(this.getServer().getBukkitVersion());
        while(nm.find()){
            version = Integer.parseInt(nm.group(2));
        }
        // Minimum version: 1.16
        if(version >= 16){
            Pattern pattern = Pattern.compile("&#([0-9a-fA-F]){6}");
            Matcher matcher = pattern.matcher(string);
            StringBuffer sb = new StringBuffer();
            while(matcher.find()){
                String hex = matcher.group();
                matcher.appendReplacement(sb, ChatColor.of(hex.substring(1)).toString());
            }
            matcher.appendTail(sb);
            finalmessage = ChatColor.translateAlternateColorCodes('&', sb.toString());
        } else {
            finalmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
        }
        return finalmessage;
    }

}
