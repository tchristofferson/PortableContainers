package com.tchristofferson.portablecontainers.core;

import com.tchristofferson.portablecontainers.core.tileentities.EntityBlastFurnace;
import com.tchristofferson.portablecontainers.core.tileentities.EntityBrewingStand;
import com.tchristofferson.portablecontainers.core.tileentities.EntityFurnace;
import net.minecraft.server.v1_14_R1.TileEntitySmoker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TickManager {

    private final Plugin plugin;

    private int taskId;
    private EntityFurnace entityFurnace;
    private EntityBlastFurnace entityBlastFurnace;
    private EntityBrewingStand entityBrewingStand;
    private TileEntitySmoker tileEntitySmoker;

    protected TickManager(Plugin plugin) {
        this.plugin = plugin;
        this.taskId = -1;
        this.entityFurnace = null;
        this.entityBlastFurnace = null;
        this.entityBrewingStand = null;
        this.tileEntitySmoker = null;
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

    public void setTileEntitySmoker(TileEntitySmoker tileEntitySmoker) {
        this.tileEntitySmoker = tileEntitySmoker;
    }

    public void startTicking() {
        if (isTicking()) throw new IllegalStateException("TickManager is already ticking");
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (entityFurnace == null && entityBlastFurnace == null && entityBrewingStand == null && tileEntitySmoker == null) {
                    TickManager.this.stopTicking();
                    return;
                }

                if (entityFurnace != null) entityFurnace.tick();
                if (entityBlastFurnace != null) entityBlastFurnace.tick();
                if (entityBrewingStand != null) entityBrewingStand.tick();
                //TODO: if (entitySmoker != null) entitySmoker.tick();
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
