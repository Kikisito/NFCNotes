package es.kikisito.nfcnotes.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WithdrawEvent extends Event implements Cancellable {

    private Player player;
    private Double money;
    private Integer amount;
    private boolean isCancelled;

    public WithdrawEvent(Player player, Double money, Integer amount){
        this.player = player;
        this.money = money;
        this.amount = amount;
        this.isCancelled = false;
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
