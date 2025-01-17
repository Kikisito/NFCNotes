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
import es.kikisito.nfcnotes.enums.NFCConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {

    public static boolean depositSuccessful(Main plugin, Player player, double money){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.depositPlayer(player, money).transactionSuccess();
            case "Essentials":
            case "EssentialsX":
                try {
                    com.earth2me.essentials.api.Economy.add(player.getUniqueId(), BigDecimal.valueOf(money));
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
        }
        return false;
    }

    public static boolean withdrawSuccessful(Main plugin, Player player, double money){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.withdrawPlayer(player, money).transactionSuccess();
            case "Essentials":
            case "EssentialsX":
                try {
                    com.earth2me.essentials.api.Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(money));
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
        }
        return false;
    }

    @NotNull
    public static Double getPlayerBalance(Main plugin, Player player){
        switch(plugin.getEco()){
            case "Vault":
                Economy vault = plugin.getVaultEco();
                return vault.getBalance(player);
            case "Essentials":
            case "EssentialsX":
                try {
                    return com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId()).doubleValue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }
        return 0.0;
    }

    public static boolean isInteger(Object o){
        boolean isInteger = false;
        try {
            Integer.parseInt(o.toString());
            isInteger = true;
        } catch (NumberFormatException ignored) {}
        return isInteger;
    }

    public static DecimalFormat getDecimalFormat(){
        DecimalFormat decimalFormat;
        if(NFCConfig.USE_EUROPEAN_FORMAT.getBoolean()) {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);
            decimalFormatSymbols.setDecimalSeparator(',');
            decimalFormatSymbols.setGroupingSeparator('.');
            decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString(), decimalFormatSymbols);
        } else decimalFormat = new DecimalFormat(NFCConfig.NOTE_DECIMAL_FORMAT.getString());
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat;
    }
}
