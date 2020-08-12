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

import es.kikisito.nfcnotes.enums.ActionMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WithdrawEvent extends Event implements Cancellable {

    private Player player;
    private Double money;
    private Integer amount;
    private boolean isCancelled;
    private ActionMethod actionMethod;

    /**
     * This constructor is used to create a new WithdrawEvent with given parameters
     * @param player Player who has withdrawn money
     * @param money Value of each note
     * @param amount Amount of notes
     * @param actionMethod {@see ActionMethod} Method used in this transaction
     */
    public WithdrawEvent(Player player, Double money, Integer amount, ActionMethod actionMethod){
        this.player = player;
        this.money = money;
        this.amount = amount;
        this.isCancelled = false;
        this.actionMethod = actionMethod;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the player who withdrawn money
     * @return Player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Get the value of one note
     * @return Double
     */
    public Double getMoney(){
        return this.money;
    }

    /**
     * Change the amount of each note withdrawn
     * @param money Double
     */
    public void setMoney(Double money){
        this.money = money;
    }

    /**
     * Get the amount of notes withdrawn in the operation
     * @return Integer
     */
    public Integer getAmount(){
        return this.amount;
    }

    /**
     * Change the amount of notes withdrawn
     * @param amount Integer
     */
    public void setAmount(Integer amount){
        this.amount = amount;
    }

    /**
     * Get the ActionMethod used in this transaction
     * @return {@see ActionMethod}
     */
    public ActionMethod getWithdrawMethod(){
        return actionMethod;
    }

    /**
     * Get if the event was cancelled or not
     * @return Boolean
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Cancel the event
     * @param b Boolean
     */
    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
