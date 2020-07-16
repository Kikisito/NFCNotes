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

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
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
}
