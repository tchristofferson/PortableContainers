package com.tchristofferson.portablecontainers;

import com.tchristofferson.portablecontainers.commands.*;
import com.tchristofferson.portablecontainers.core.ContainerInfo;
import com.tchristofferson.portablecontainers.core.TickManager;
import com.tchristofferson.portablecontainers.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortableContainers extends JavaPlugin {

    private final Map<UUID, ContainerInfo> playerContainers = new HashMap<>();
    private FileConfiguration savesConfig;
    private TickManager tickManager;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        //Register Commands
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

        tickManager = new TickManager(this);

        saveDefaultConfig();
        File savesFile = new File(getDataFolder(), "save-data.yml");
        if (!savesFile.exists()) {
            try {
                savesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        savesConfig = YamlConfiguration.loadConfiguration(savesFile);
    }

    @Override
    public void onDisable() {
        try {
            savesConfig.save(new File(getDataFolder(), "save-data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, ContainerInfo> getPlayerContainers() {
        return playerContainers;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public FileConfiguration getSavesConfig() {
        return savesConfig;
    }

}
