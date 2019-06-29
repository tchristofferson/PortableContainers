package com.tchristofferson.portablecontainers;

import com.tchristofferson.portablecontainers.commands.*;
import com.tchristofferson.portablecontainers.core.ContainerInfo;
import com.tchristofferson.portablecontainers.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortableContainers extends JavaPlugin {

    private final Map<UUID, ContainerInfo> playerContainers = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

//        CraftBlockData data = CraftBlockData.newData(Material.FURNACE, null);
//        CraftFurnaceFurace furnace = new CraftFurnaceFurace(data.getState());
//        furnace.getState().b();
//        CraftInventory craftInventory = new CraftInventory();
        getCommand("penchantmenttable").setExecutor(new PEnchantmentCommand(this));
        getCommand("pcraftingtable").setExecutor(new PCraftingCommand());
        getCommand("pchest").setExecutor(new PChestCommand(this));
        getCommand("pbarrel").setExecutor(new PBarrelCommand(this));
        getCommand("pfurnace").setExecutor(new PFurnaceCommand(this));
        getCommand("pblastfurnace").setExecutor(new PBlastFurnaceCommand(this));
        getCommand("pbrewingstand").setExecutor(new PBrewingStandCommand(this));
        getCommand("psmoker").setExecutor(new PSmokerCommand(this));
        getCommand("panvil").setExecutor(new PAnvilCommand());
        getCommand("penderchest").setExecutor(new PEnderChestCommand());
    }

    public Map<UUID, ContainerInfo> getPlayerContainers() {
        return playerContainers;
    }

}
