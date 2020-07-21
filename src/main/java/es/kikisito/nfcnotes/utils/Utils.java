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

package es.kikisito.nfcnotes.utils;

import es.kikisito.nfcnotes.Main;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean depositSuccessful(Main plugin, Player player, double money){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.depositPlayer(player, money).transactionSuccess();
            case "PlayerPoints":
                PlayerPoints playerPoints = plugin.getPlayerPoints();
                return playerPoints.getAPI().give(playerPoints.translateNameToUUID(player.getName()), (int) money);
        }
        return false;
    }

    public static boolean withdrawSuccessful(Main plugin, Player player, double money){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.withdrawPlayer(player, money).transactionSuccess();
            case "PlayerPoints":
                PlayerPoints playerPoints = plugin.getPlayerPoints();
                return playerPoints.getAPI().take(playerPoints.translateNameToUUID(player.getName()), (int) money);
        }
        return false;
    }

    @NotNull
    public static Double getPlayerBalance(Main plugin, Player player){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.getBalance(player);
            case "PlayerPoints":
                PlayerPoints playerPoints = plugin.getPlayerPoints();
                return (double) playerPoints.getAPI().look(playerPoints.translateNameToUUID(player.getName()));
        }
        return 0.0;
    }

    public static String parseMessage(String string){
        String finalmessage;
        Integer version = null;
        // Check version
        Pattern n = Pattern.compile("^(\\d)\\.(\\d+)");
        Matcher nm = n.matcher(Bukkit.getServer().getBukkitVersion());
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

    public static boolean isInteger(Object o){
        boolean isInteger = false;
        try {
            Integer.parseInt(o.toString());
            isInteger = true;
        } catch (NumberFormatException ignored) {}
        return isInteger;
    }
}
