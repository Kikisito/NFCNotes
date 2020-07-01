package es.kikisito.nfcnotes.commands;

import java.text.DecimalFormat;

import es.kikisito.nfcnotes.Main;
import es.kikisito.nfcnotes.events.WithdrawEvent;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Withdraw implements CommandExecutor {
    private Main plugin;
    private Configuration config;
    private FileConfiguration messages;

    public Withdraw(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
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
        if (!p.hasPermission("nfcnotes.withdraw") || !config.getBoolean("modules.withdraw")) {
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
                    if (args[0].equalsIgnoreCase("all") && config.getBoolean("modules.withdraw-all")) {
                        money = plugin.getEco().getBalance(p);
                        withdraw(p, money, 1);
                        return true;
                    } else {
                        money = Double.parseDouble(args[0]);
                        withdraw(p, money, 1);
                    }
                    break;
                case 2:
                    if(config.getBoolean("modules.multiple-withdraw")) {
                        money = Double.parseDouble(args[0]);
                        amount = Integer.parseInt(args[1]);
                        withdraw(p, money, amount);
                        break;
                    }
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
        // Call WithdrawEvent and check if it was cancelled
        WithdrawEvent withdrawEvent = new WithdrawEvent(p, m, a);
        plugin.getServer().getPluginManager().callEvent(withdrawEvent);
        if(!withdrawEvent.isCancelled()) {
            // Get variables from event
            Player player = withdrawEvent.getPlayer();
            Double money = withdrawEvent.getMoney();
            Integer amount = withdrawEvent.getAmount();
            // Make the amount readable
            String formattedMoney = new DecimalFormat(config.getString("notes.decimal-format")).format(money * amount);
            // Execute if the event wasn't cancelled
            // Execute withdraw and get Vault's response
            EconomyResponse response = Main.getEco().withdrawPlayer(player, money * amount);
            if (response.type.equals(ResponseType.SUCCESS)) {
                // Create the note and give it to the player
                ItemStack paper = plugin.createNote(formattedMoney, money, amount);
                player.getInventory().addItem(paper);
                player.sendMessage(plugin.parseMessage(messages.getString("withdraw-successful").replace("{money}", formattedMoney)));
            } else if(response.amount == 0){
                // Insufficient funds
                player.sendMessage(plugin.parseMessage(messages.getString("insufficient-funds")));
            } else {
                // Unexpected error
                player.sendMessage(ChatColor.RED + response.errorMessage);
            }
            if (money * amount >= config.getInt("warn-staff-if-value-is-higher-than")) {
                for (Player pl : plugin.getServer().getOnlinePlayers()) {
                    if (pl.hasPermission("nfcnotes.staff.warn") && player != pl) {
                        pl.sendMessage(plugin.parseMessage(messages.getString("staff.warn-withdraw")).replace("{player}", player.getName()).replace("{money}", formattedMoney));
                        plugin.getLogger().info(plugin.parseMessage(messages.getString("staff.warn-withdraw")).replace("{player}", player.getName()).replace("{money}", formattedMoney));
                    }
                }
            }
        }
    }
}
