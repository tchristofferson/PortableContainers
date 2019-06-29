package com.tchristofferson.portablecontainers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PortableBrewingStandFuelEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack fuel;
    private int fuelPower;
    private boolean cancelled;
    private boolean consuming = true;

    public PortableBrewingStandFuelEvent(ItemStack fuel, int fuelPower) {
        this.fuel = fuel;
        this.fuelPower = fuelPower;
    }

    public ItemStack getFuel() {
        return this.fuel;
    }

    public int getFuelPower() {
        return this.fuelPower;
    }

    public void setFuelPower(int fuelPower) {
        this.fuelPower = fuelPower;
    }

    public boolean isConsuming() {
        return this.consuming;
    }

    public void setConsuming(boolean consuming) {
        this.consuming = consuming;
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
