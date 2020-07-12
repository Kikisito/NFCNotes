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

package es.kikisito.nfcnotes.events;

import es.kikisito.nfcnotes.enums.WithdrawMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WithdrawEvent extends Event implements Cancellable {

    private Player player;
    private Double money;
    private Integer amount;
    private boolean isCancelled;
    private WithdrawMethod withdrawMethod;

    /**
     * @deprecated see WithdrawtEvent(Player player, Double money, WithdrawMethod withdrawtMethod)
     * Deprecated since 2.1. This constructor will be removed soon. Added WithdrawMethod.
     */
    @Deprecated
    public WithdrawEvent(Player player, Double money, Integer amount){
        this.player = player;
        this.money = money;
        this.amount = amount;
        this.isCancelled = false;
        this.withdrawMethod = WithdrawMethod.DEPRECATED_METHOD;
    }

    public WithdrawEvent(Player player, Double money, Integer amount, WithdrawMethod withdrawMethod){
        this.player = player;
        this.money = money;
        this.amount = amount;
        this.isCancelled = false;
        this.withdrawMethod = withdrawMethod;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer(){
        return this.player;
    }

    public Double getMoney(){
        return this.money;
    }

    public void setMoney(Double money){
        this.money = money;
    }

    public Integer getAmount(){
        return this.amount;
    }

    public void setAmount(Integer amount){
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
