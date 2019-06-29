package com.tchristofferson.portablecontainers.core;

import com.tchristofferson.portablecontainers.PortableContainers;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBlastFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBrewingStand;
import com.tchristofferson.portablecontainers.core.tileentities.EntityFurnace;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_14_R1.inventory.util.CraftTileInventoryConverter;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ContainerInfo {

    private final Player owner;
    private final TickManager tickManager;

    private final Inventory chest;
    private final Inventory barrel;

    private EntityFurnace entityFurnace;
    private EntityBlastFurnace entityBlastFurnace;
    private EntityBrewingStand entityBrewingStand;
    private TileEntitySmoker tileEntitySmoker;

    public ContainerInfo(PortableContainers plugin, Player owner) {

        entityFurnace = null;
        entityBlastFurnace = null;
        entityBrewingStand = null;
        tileEntitySmoker = null;

        this.owner = owner;
        this.tickManager = new TickManager(plugin);

        chest = Bukkit.createInventory(owner, 54, InventoryType.CHEST.getDefaultTitle());
        barrel = Bukkit.createInventory(owner, InventoryType.BARREL);
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public void openChest() {
        owner.openInventory(chest);
    }

    public void openBarrel() {
        owner.openInventory(barrel);
    }

    public void openFurnace() {
        if (entityFurnace == null || !tickManager.isTicking()) {
            entityFurnace = new EntityFurnace(tickManager);
            entityFurnace.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.FURNACE.getDefaultTitle()));
            entityFurnace.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntityFurnace(entityFurnace);
        }

        owner.openInventory(new CraftInventoryFurnace(entityFurnace));
        if (!tickManager.isTicking()) tickManager.startTicking();
    }

    public void openBlastFurnace() {
        if (entityBlastFurnace == null || !tickManager.isTicking()) {
            entityBlastFurnace = new EntityBlastFurnace(tickManager);
            entityBlastFurnace.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BLAST_FURNACE.getDefaultTitle()));
            entityBlastFurnace.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntityBlastFurnace(entityBlastFurnace);
        }

        owner.openInventory(new CraftInventory(entityBlastFurnace));
        if (!tickManager.isTicking()) tickManager.startTicking();
    }

    public void openBrewingStand() {
        if (entityBrewingStand == null || !tickManager.isTicking()) {
            entityBrewingStand = new EntityBrewingStand(tickManager, owner);
            entityBrewingStand.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BREWING.getDefaultTitle()));
            entityBrewingStand.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntityBrewingStand(entityBrewingStand);
        }

        owner.openInventory(new CraftInventory(entityBrewingStand));
        if (!tickManager.isTicking()) tickManager.startTicking();
    }

    public void openSmoker() {
        if (tileEntitySmoker == null || !tickManager.isTicking()) {
            CraftTileInventoryConverter.Smoker smokerCreator = new CraftTileInventoryConverter.Smoker();
            tileEntitySmoker = (TileEntitySmoker) smokerCreator.getTileEntity();
            tileEntitySmoker.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.SMOKER.getDefaultTitle()));
            tickManager.setTileEntitySmoker(tileEntitySmoker);
        }

        owner.openInventory(new CraftInventory(tileEntitySmoker));
        if (!tickManager.isTicking()) tickManager.startTicking();
    }

}
