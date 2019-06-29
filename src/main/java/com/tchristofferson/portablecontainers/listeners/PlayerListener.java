package com.tchristofferson.portablecontainers.listeners;

import com.tchristofferson.portablecontainers.PortableContainers;
import com.tchristofferson.portablecontainers.core.ContainerInfo;
import com.tchristofferson.portablecontainers.core.TickManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PortableContainers plugin;

    public PlayerListener(PortableContainers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ContainerInfo containerInfo = new ContainerInfo(plugin, player);
        plugin.getPlayerContainers().put(player.getUniqueId(), containerInfo);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        ContainerInfo containerInfo = plugin.getPlayerContainers().remove(event.getPlayer().getUniqueId());
        TickManager tickManager = containerInfo.getTickManager();
        if (tickManager.isTicking()) tickManager.stopTicking();
    }

}
