package com.tchristofferson.portablecontainers.core;

import com.tchristofferson.portablecontainers.PortableContainers;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBlastFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBrewingStand;
import com.tchristofferson.portablecontainers.core.tileentities.EntityFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntitySmoker;
import net.minecraft.server.v1_14_R1.Container;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ContainerInfo {

    private final Player owner;
    private final TickManager tickManager;

    private Inventory chest;
    private Inventory barrel;

    private EntityFurnace entityFurnace;
    private EntityBlastFurnace entityBlastFurnace;
    private EntityBrewingStand entityBrewingStand;
    private EntitySmoker entitySmoker;

    public ContainerInfo(PortableContainers plugin, Player owner) {

        entityFurnace = null;
        entityBlastFurnace = null;
        entityBrewingStand = null;
        entitySmoker = null;

        this.owner = owner;
        this.tickManager = plugin.getTickManager();

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
        boolean notTicking = !tickManager.isTicking(owner.getUniqueId());
        if (entityFurnace == null || notTicking) {
            entityFurnace = new EntityFurnace(this);
            entityFurnace.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.FURNACE.getDefaultTitle()));
            entityFurnace.setWorld(((CraftWorld) owner.getWorld()).getHandle());
        }

        if (notTicking) tickManager.startTicking(this);
        openFurnace(entityFurnace);
    }

    public void openBlastFurnace() {
        boolean notTicking = !tickManager.isTicking(owner.getUniqueId());
        if (entityBlastFurnace == null || notTicking) {
            entityBlastFurnace = new EntityBlastFurnace(this);
            entityBlastFurnace.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BLAST_FURNACE.getDefaultTitle()));
            entityBlastFurnace.setWorld(((CraftWorld) owner.getWorld()).getHandle());
        }

        if (notTicking) tickManager.startTicking(this);
        openFurnace(entityBlastFurnace);
    }

    public void openBrewingStand() {
        boolean notTicking = !tickManager.isTicking(owner.getUniqueId());
        if (entityBrewingStand == null || notTicking) {
            entityBrewingStand = new EntityBrewingStand(this, owner);
            entityBrewingStand.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BREWING.getDefaultTitle()));
            entityBrewingStand.setWorld(((CraftWorld) owner.getWorld()).getHandle());
        }

        if (notTicking) tickManager.startTicking(this);
        owner.openInventory(new CraftInventoryBrewer(entityBrewingStand));
    }

    public void openSmoker() {
        boolean notTicking = !tickManager.isTicking(owner.getUniqueId());
        if (entitySmoker == null || notTicking) {
            entitySmoker = new EntitySmoker(this);
            entitySmoker.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.SMOKER.getDefaultTitle()));
            entitySmoker.setWorld(((CraftWorld) owner.getWorld()).getHandle());
        }

        if (notTicking) tickManager.startTicking(this);
        openFurnace(entitySmoker);
    }

    public Player getOwner() {
        return owner;
    }

    public Inventory getChest() {
        return chest;
    }

    public Inventory getBarrel() {
        return barrel;
    }

    public EntityFurnace getEntityFurnace() {
        return entityFurnace;
    }

    public EntityBlastFurnace getEntityBlastFurnace() {
        return entityBlastFurnace;
    }

    public EntityBrewingStand getEntityBrewingStand() {
        return entityBrewingStand;
    }

    public EntitySmoker getEntitySmoker() {
        return entitySmoker;
    }

    public void setChest(Inventory chest) {
        this.chest = chest;
    }

    public void setBarrel(Inventory barrel) {
        this.barrel = barrel;
    }

    public void setEntityFurnace(EntityFurnace entityFurnace) {
        this.entityFurnace = entityFurnace;
    }

    public void setEntityBlastFurnace(EntityBlastFurnace entityBlastFurnace) {
        this.entityBlastFurnace = entityBlastFurnace;
    }

    public void setEntityBrewingStand(EntityBrewingStand entityBrewingStand) {
        this.entityBrewingStand = entityBrewingStand;
    }

    public void setEntitySmoker(EntitySmoker entitySmoker) {
        this.entitySmoker = entitySmoker;
    }

    private void openFurnace(EntityFurnace entityFurnace) {
        CraftPlayer craftPlayer = (CraftPlayer) owner;
        Container formerContainer = craftPlayer.getHandle().activeContainer;
        ((CraftPlayer) owner).getHandle().openContainer(entityFurnace);
        if (craftPlayer.getHandle().activeContainer != formerContainer) craftPlayer.getHandle().activeContainer.checkReachable = false;
    }
}
