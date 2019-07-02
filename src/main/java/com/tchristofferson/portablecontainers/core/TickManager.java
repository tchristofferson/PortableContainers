package com.tchristofferson.portablecontainers.core;

import com.tchristofferson.portablecontainers.core.tileentities.EntityBlastFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBrewingStand;
import com.tchristofferson.portablecontainers.core.tileentities.EntityFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntitySmoker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TickManager {

    private final Plugin plugin;
    private final Map<UUID, Integer> taskIds;

    public TickManager(Plugin plugin) {
        this.plugin = plugin;
        this.taskIds = new HashMap<>();
    }

    public void startTicking(ContainerInfo containerInfo) {
        if (taskIds.containsKey(containerInfo.getOwner().getUniqueId())) throw new IllegalStateException("TickManager is already ticking for specified container info");

        EntityFurnace entityFurnace = containerInfo.getEntityFurnace();
        EntityBlastFurnace entityBlastFurnace = containerInfo.getEntityBlastFurnace();
        EntityBrewingStand entityBrewingStand = containerInfo.getEntityBrewingStand();
        EntitySmoker entitySmoker = containerInfo.getEntitySmoker();

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (entityFurnace == null && entityBlastFurnace == null && entityBrewingStand == null && entitySmoker == null) {
                    TickManager.this.stopTicking(containerInfo);
                    return;
                }

                if (entityFurnace != null) entityFurnace.tick();
                if (entityBlastFurnace != null) entityBlastFurnace.tick();
                if (entityBrewingStand != null) entityBrewingStand.tick();
                if (entitySmoker != null) entitySmoker.tick();
            }
        }.runTaskTimer(plugin, 1, 1).getTaskId();

        taskIds.put(containerInfo.getOwner().getUniqueId(), taskId);
    }

    public void stopTicking(ContainerInfo containerInfo) {
        Integer taskId = taskIds.remove(containerInfo.getOwner().getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public boolean isTicking(UUID uuid) {
        return taskIds.containsKey(uuid);
    }
}
