package es.kikisito.nfcnotes.commands;

import java.text.DecimalFormat;

import es.kikisito.nfcnotes.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateNote implements CommandExecutor {
    private Main plugin;
    private Configuration config;
    private FileConfiguration messages;

    public CreateNote(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.messages = plugin.getMessages();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the player is allowed to withdraw money and its inventory is not full
        if (!sender.hasPermission("nfcnotes.withdraw")) {
            sender.sendMessage(plugin.parseMessage(messages.getString("no-permission")));
            return false;
        } else if (sender instanceof Player && ((Player) sender).getInventory().firstEmpty() == -1) {
            sender.sendMessage(plugin.parseMessage(messages.getString("full-inventory")));
            return false;
        }
        // Define variables
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        money = Double.parseDouble(args[0]);
                        withdraw(p, money, 1);
                    } else sender.sendMessage(plugin.parseMessage(messages.getString("only-players")));
                    break;
                case 2:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        withdraw(player, money, 1);
                    } else {
                        if(sender instanceof Player) {
                            Player p = (Player) sender;
                            money = Double.parseDouble(args[0]);
                            amount = Integer.parseInt(args[1]);
                            withdraw(p, money, amount);
                        } else sender.sendMessage(plugin.parseMessage(messages.getString("only-players")));
                    }
                    break;
                case 3:
                    if(plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[0]))) {
                        Player player = plugin.getServer().getPlayer(args[0]);
                        money = Double.parseDouble(args[1]);
                        amount = Integer.parseInt(args[2]);
                        withdraw(player, money, amount);
                        break;
                    }
                default:
                    sender.sendMessage(plugin.parseMessage(messages.getString("withdraw-usage")));
                    break;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(plugin.parseMessage(messages.getString("only-integers")));
        }
        return true;
    }

    private void withdraw(Player p, Double m, Integer a){
        // Check if given number is positive and is an integer.
        if (m <= 0) {
            p.sendMessage(plugin.parseMessage(messages.getString("use-a-number-higher-than-zero")));
            return;
        } else if(!(m % 1 == 0)) {
            p.sendMessage(plugin.parseMessage(messages.getString("only-integers")));
            return;
        }
        // Make the amount readable
        String formattedMoney = new DecimalFormat(config.getString("notes.decimal-format")).format(m);
        // Create the note and give it to the player
        ItemStack paper = plugin.createNote(formattedMoney, m, a);
        p.getInventory().addItem(paper);
        p.sendMessage(plugin.parseMessage(messages.getString("withdraw-successful").replace("{money}", formattedMoney)));
    }
}
