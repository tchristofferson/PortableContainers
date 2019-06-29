package com.tchristofferson.portablecontainers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PortableFurnaceSmeltEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack source;
    private ItemStack result;
    private boolean cancelled;

    public PortableFurnaceSmeltEvent(ItemStack source, ItemStack result) {
        this.source = source;
        this.result = result;
        this.cancelled = false;
    }

    public ItemStack getSource() {
        return this.source;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
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
