package com.tchristofferson.portablecontainers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.BrewerInventory;

public class PortableBrewEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private BrewerInventory contents;
    private int fuelLevel;
    private boolean cancelled;

    public PortableBrewEvent(BrewerInventory contents, int fuelLevel) {
        this.contents = contents;
        this.fuelLevel = fuelLevel;
    }

    public BrewerInventory getContents() {
        return this.contents;
    }

    public int getFuelLevel() {
        return this.fuelLevel;
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
