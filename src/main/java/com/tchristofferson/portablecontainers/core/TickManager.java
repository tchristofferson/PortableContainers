package com.tchristofferson.portablecontainers.core;

import com.tchristofferson.portablecontainers.core.tileentities.EntityBlastFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBrewingStand;
import com.tchristofferson.portablecontainers.core.tileentities.EntityFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntitySmoker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TickManager {

    private final Plugin plugin;

    private int taskId;
    private EntityFurnace entityFurnace;
    private EntityBlastFurnace entityBlastFurnace;
    private EntityBrewingStand entityBrewingStand;
    private EntitySmoker entitySmoker;

    protected TickManager(Plugin plugin) {
        this.plugin = plugin;
        this.taskId = -1;
        this.entityFurnace = null;
        this.entityBlastFurnace = null;
        this.entityBrewingStand = null;
        this.entitySmoker = null;
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

    public void startTicking() {
        if (isTicking()) throw new IllegalStateException("TickManager is already ticking");
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (entityFurnace == null && entityBlastFurnace == null && entityBrewingStand == null && entitySmoker == null) {
                    TickManager.this.stopTicking();
                    return;
                }

                if (entityFurnace != null) entityFurnace.tick();
                if (entityBlastFurnace != null) entityBlastFurnace.tick();
                if (entityBrewingStand != null) entityBrewingStand.tick();
                if (entitySmoker != null) entitySmoker.tick();
            }
        }.runTaskTimer(plugin, 1, 1).getTaskId();
    }

    public void stopTicking() {
        if (!isTicking()) return;
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    public boolean isTicking() {
        return taskId != -1;
    }

}
