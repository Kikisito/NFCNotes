package es.kikisito.nfcnotes.commands;

import java.text.DecimalFormat;

import es.kikisito.nfcnotes.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateNote implements CommandExecutor {
    private Main plugin;
    private FileConfiguration messages;

    public CreateNote(Main plugin){
        this.plugin = plugin;
        this.messages = plugin.getMessages();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can execute this command. Console, get away!
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.parseMessage(messages.getString("only-players")));
            return false;
        }
        Player p = (Player) sender;
        // Check if the player is allowed to withdraw money and its inventory is not full
        if (!p.hasPermission("nfcnotes.withdraw")) {
            p.sendMessage(plugin.parseMessage(messages.getString("no-permission")));
            return false;
        } else if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(plugin.parseMessage(messages.getString("full-inventory")));
            return false;
        }
        // Define variables
        double money;
        int amount;
        try {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("all")) {
                        money = plugin.getEco().getBalance(p);
                        withdraw(p, money, 1);
                        return true;
                    } else {
                        money = Double.parseDouble(args[0]);
                        withdraw(p, money, 1);
                    }
                    break;
                case 2:
                    money = Double.parseDouble(args[0]);
                    amount = Integer.parseInt(args[1]);
                    withdraw(p, money, amount);
                    break;
                default:
                    p.sendMessage(plugin.parseMessage(messages.getString("withdraw-usage")));
                    break;
            }
        } catch (NumberFormatException ex) {
            p.sendMessage(plugin.parseMessage(messages.getString("only-integers")));
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
        String formattedMoney = new DecimalFormat("#,###.##").format(m * a);
        // Create the note and give it to the player
        ItemStack paper = plugin.createNote(formattedMoney, m, a);
        p.getInventory().addItem(paper);
        p.sendMessage(plugin.parseMessage(messages.getString("withdraw-successful").replace("{money}", formattedMoney)));
    }
}
