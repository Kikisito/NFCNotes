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

public class DepositEvent extends Event implements Cancellable {

    private Player player;
    private Double money;
    private boolean isCancelled;
    private ActionMethod depositMethod;

    /**
     * This constructor is used to create a new DepositEvent with given parameters
     * @param player Player who rdeemed money
     * @param money Amount of money redeemed
     * @param depositMethod {@see ActionMethod} Method used in this transaction
     */
    public DepositEvent(Player player, Double money, ActionMethod depositMethod){
        this.player = player;
        this.money = money;
        this.isCancelled = false;
        this.depositMethod = depositMethod;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the player who redeemed money
     * @return Player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Get the total amount of money redeemed
     * @return Double
     */
    public Double getMoney(){
        return this.money;
    }

    /**
     * Change the total amount of money redeemed
     * @param money Double
     */
    public void setMoney(Double money){ this.money = money; }

    /**
     * Get the ActionMethod used in this transaction
     * @return {@see ActionMethod}
     */
    public ActionMethod getDepositMethod(){ return this.depositMethod; }

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
