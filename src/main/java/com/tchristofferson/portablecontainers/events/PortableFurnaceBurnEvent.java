package com.tchristofferson.portablecontainers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PortableFurnaceBurnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack fuel;
    private int burnTime;
    private boolean cancelled;
    private boolean burning;

    public PortableFurnaceBurnEvent(ItemStack fuel, int burnTime) {
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.cancelled = false;
        this.burning = true;
    }

    public ItemStack getFuel() {
        return this.fuel;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public boolean isBurning() {
        return this.burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
