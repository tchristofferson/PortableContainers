package com.tchristofferson.portablecontainers.listeners;

import com.tchristofferson.portablecontainers.PortableContainers;
import com.tchristofferson.portablecontainers.core.ContainerInfo;
import com.tchristofferson.portablecontainers.core.TickManager;
import com.tchristofferson.portablecontainers.core.tileentities.*;
import net.minecraft.server.v1_14_R1.TileEntityContainer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    //Sections for TileEntities are getType().name()
    private static final String CHEST_SECTION = InventoryType.CHEST.name();
    private static final String BARREL_SECTION = InventoryType.BARREL.name();
    private static final String BREWING_STAND_SECTION = IEntityContainer.EntityTypes.BREWING_STAND.name();
    private final PortableContainers plugin;

    public PlayerListener(PortableContainers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ContainerInfo containerInfo = loadContainerInfo(player);
        plugin.getPlayerContainers().put(player.getUniqueId(), containerInfo);
        containerInfo.getTickManager().startTicking(containerInfo);
    }

    //FIXME
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ContainerInfo containerInfo = plugin.getPlayerContainers().remove(player.getUniqueId());
        TickManager tickManager = containerInfo.getTickManager();
        if (tickManager.isTicking(player.getUniqueId())) tickManager.stopTicking(containerInfo);

        FileConfiguration savesConfig = plugin.getSavesConfig();
        Inventory chest = containerInfo.getChest();
        Inventory barrel = containerInfo.getBarrel();

        saveInventoryItems(savesConfig, chest, player, CHEST_SECTION);
        saveInventoryItems(savesConfig, barrel, player, BARREL_SECTION);

        EntityFurnace entityFurnace = containerInfo.getEntityFurnace();
        EntityFurnace entityBlastFurnace = containerInfo.getEntityBlastFurnace();
        EntityFurnace entitySmoker = containerInfo.getEntitySmoker();
        EntityBrewingStand entityBrewingStand = containerInfo.getEntityBrewingStand();
        saveTileInventories(savesConfig, player, entityFurnace, entityBlastFurnace, entitySmoker, entityBrewingStand);
    }

    private ContainerInfo loadContainerInfo(Player player) {
        if (getPlayerSection(player) == null) return new ContainerInfo(plugin, player);
        ContainerInfo containerInfo = new ContainerInfo(plugin, player);

        Inventory chest = loadInventory(player, InventoryType.CHEST);
        Inventory barrel = loadInventory(player, InventoryType.BARREL);
        containerInfo.setChest(chest);
        containerInfo.setBarrel(barrel);

        EntityFurnace entityFurnace = new EntityFurnace(containerInfo);
        EntityBlastFurnace entityBlastFurnace = new EntityBlastFurnace(containerInfo);
        EntitySmoker entitySmoker = new EntitySmoker(containerInfo);
        EntityBrewingStand entityBrewingStand = new EntityBrewingStand(containerInfo, player);
        loadEntities(player, entityFurnace, entityBlastFurnace, entitySmoker, entityBrewingStand);

        containerInfo.setEntityFurnace(entityFurnace);
        containerInfo.setEntityBlastFurnace(entityBlastFurnace);
        containerInfo.setEntitySmoker(entitySmoker);
        containerInfo.setEntityBrewingStand(entityBrewingStand);

        return containerInfo;
    }

    private void saveTileInventories(FileConfiguration savesConfig, Player player, IEntityContainer... containers) {
        for (IEntityContainer container : containers) {
            if (container == null) continue;//TODO: Set to null in savesConfig
            if (container instanceof EntityFurnace) {
                saveFurnaceInventory((EntityFurnace) container, savesConfig, player, container.getType().name());
            } else if (container instanceof EntityBrewingStand) {
                saveBrewingStandInventory((EntityBrewingStand) container, savesConfig, player);
            }
        }
    }

    private void saveFurnaceInventory(EntityFurnace tileEntity, FileConfiguration savesConfig, Player player, String section) {
        if (tileEntity != null) {
            String uuid = player.getUniqueId().toString();
            Inventory furnaceInventory = new CraftInventory(tileEntity);
            savesConfig.set(uuid + "." + section + ".burnTime", tileEntity.burnTime);
            savesConfig.set(uuid + "." + section + ".ticksForCurrentFuel", tileEntity.getProperty(1));
            savesConfig.set(uuid + "." + section + ".cookTime", tileEntity.cookTime);
            savesConfig.set(uuid + "." + section + ".cookTimeTotal", tileEntity.cookTimeTotal);
            saveInventoryItems(savesConfig, furnaceInventory, player, section);
        }
    }

    private void saveBrewingStandInventory(EntityBrewingStand entityBrewingStand, FileConfiguration savesConfig, Player player) {
        if (entityBrewingStand != null) {
            String uuid = player.getUniqueId().toString();
            Inventory brewingInventory = new CraftInventory(entityBrewingStand);
            savesConfig.set(uuid + ".brewingStand.brewTime", entityBrewingStand.brewTime);
            savesConfig.set(uuid + ".brewingStand.fuelLevel", entityBrewingStand.fuelLevel);
            saveInventoryItems(savesConfig, brewingInventory, player, BREWING_STAND_SECTION);
        }
    }

    private void saveInventoryItems(FileConfiguration savesConfig, Inventory inventory, Player player, String section) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null) savesConfig.set(player.getUniqueId().toString() + "." + section + "." + i, itemStack.serialize());
        }
    }

    private Inventory loadInventory(Player player, InventoryType inventoryType) {
        ConfigurationSection playerSection = getPlayerSection(player);

        if (inventoryType == InventoryType.CHEST) {
            if (playerSection == null || !playerSection.contains(CHEST_SECTION)) {
                return Bukkit.createInventory(player, 54, InventoryType.CHEST.getDefaultTitle());
            } else {
                return loadInventory(player, inventoryType, getSection(player, CHEST_SECTION));
            }
        } else if (inventoryType == InventoryType.BARREL) {
            if (playerSection == null || !playerSection.contains(BARREL_SECTION)) {
                return Bukkit.createInventory(player, InventoryType.BARREL);
            } else {
                return loadInventory(player, inventoryType, getSection(player, BARREL_SECTION));
            }
        } else {
            return null;
        }
    }

    private Inventory loadInventory(Player player, InventoryType inventoryType, ConfigurationSection section) {
        Inventory inventory;

        if (inventoryType == InventoryType.CHEST) {
            inventory = Bukkit.createInventory(player, 54, inventoryType.getDefaultTitle());
        } else if (inventoryType == InventoryType.BARREL) {
            inventory = Bukkit.createInventory(player, inventoryType);
        } else {
            return null;
        }

        loadInventory(inventory, section);
        return inventory;
    }

    private void loadInventory(Inventory inventory, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            int slot;
            try {
                slot = Integer.parseInt(key);
            } catch (NumberFormatException ignored) {
                continue;
            }
            inventory.setItem(slot, section.getItemStack(key));
        }
    }

    private void loadEntities(Player player, IEntityContainer... containers) {
        for (IEntityContainer container : containers) {
            if (container instanceof EntityFurnace) {
                loadFurnace(player, (EntityFurnace) container);
            } else if (container instanceof EntityBrewingStand) {
                loadBrewing(player, (EntityBrewingStand) container);
            }
            ((TileEntityContainer) container).setWorld(((CraftWorld) player.getWorld()).getHandle());
        }
    }

    private void loadFurnace(Player player, EntityFurnace entityFurnace) {
        ConfigurationSection section = getSection(player, entityFurnace.getType().name());
        if (section == null) return;
         entityFurnace.burnTime = section.getInt("burnTime");
         entityFurnace.setProperty(1, section.getInt("ticksForCurrentFuel"));
         entityFurnace.cookTime = section.getInt("cookTime");
         entityFurnace.cookTimeTotal = section.getInt("cookTimeTotal");
         Inventory inventory = new CraftInventoryFurnace(entityFurnace);
         loadInventory(inventory, section);
    }

    private void loadBrewing(Player player, EntityBrewingStand entityBrewingStand) {
        ConfigurationSection section = getSection(player, BREWING_STAND_SECTION);
        if (section == null) return;
        entityBrewingStand.brewTime = section.getInt("brewTime");
        entityBrewingStand.fuelLevel = section.getInt("fuelLevel");
        Inventory inventory = new CraftInventoryBrewer(entityBrewingStand);
        loadInventory(inventory, section);
    }

    private ConfigurationSection getPlayerSection(Player player) {
        return plugin.getSavesConfig().getConfigurationSection(player.getUniqueId().toString());
    }

    private ConfigurationSection getSection(Player player, String section) {
        return plugin.getSavesConfig().getConfigurationSection(player.getUniqueId().toString() + "." + section);
    }
}
