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

    private final Inventory chest;
    private final Inventory barrel;

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

        if (!tickManager.isTicking()) tickManager.startTicking();
        CraftPlayer player = (CraftPlayer) owner;
        Container formerContainer = player.getHandle().activeContainer;
        ((CraftPlayer) owner).getHandle().openContainer(entityFurnace);
        if (player.getHandle().activeContainer != formerContainer) player.getHandle().activeContainer.checkReachable = false;
    }

    public void openBlastFurnace() {
        if (entityBlastFurnace == null || !tickManager.isTicking()) {
            entityBlastFurnace = new EntityBlastFurnace(tickManager);
            entityBlastFurnace.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BLAST_FURNACE.getDefaultTitle()));
            entityBlastFurnace.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntityBlastFurnace(entityBlastFurnace);
        }

        if (!tickManager.isTicking()) tickManager.startTicking();
        CraftPlayer player = (CraftPlayer) owner;
        Container formerContainer = player.getHandle().activeContainer;
        ((CraftPlayer) owner).getHandle().openContainer(entityBlastFurnace);
        if (player.getHandle().activeContainer != formerContainer) player.getHandle().activeContainer.checkReachable = false;
    }

    public void openBrewingStand() {
        if (entityBrewingStand == null || !tickManager.isTicking()) {
            entityBrewingStand = new EntityBrewingStand(tickManager, owner);
            entityBrewingStand.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.BREWING.getDefaultTitle()));
            entityBrewingStand.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntityBrewingStand(entityBrewingStand);
        }

        if (!tickManager.isTicking()) tickManager.startTicking();
        owner.openInventory(new CraftInventoryBrewer(entityBrewingStand));
    }

    public void openSmoker() {
        if (entitySmoker == null || !tickManager.isTicking()) {
            entitySmoker = new EntitySmoker(tickManager);
            entitySmoker.setCustomName(CraftChatMessage.fromStringOrNull(InventoryType.SMOKER.getDefaultTitle()));
            entitySmoker.setWorld(((CraftWorld) owner.getWorld()).getHandle());
            tickManager.setEntitySmoker(entitySmoker);
        }

        if (!tickManager.isTicking()) tickManager.startTicking();
        CraftPlayer player = (CraftPlayer) owner;
        Container formerContainer = player.getHandle().activeContainer;
        ((CraftPlayer) owner).getHandle().openContainer(entitySmoker);
        if (player.getHandle().activeContainer != formerContainer) player.getHandle().activeContainer.checkReachable = false;
    }

}
