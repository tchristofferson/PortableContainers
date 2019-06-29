package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import net.minecraft.server.v1_14_R1.TileEntitySmoker;

public class EntitySmoker extends TileEntitySmoker {

    private final TickManager tickManager;

    public EntitySmoker(TickManager tickManager) {
        super();
        this.tickManager = tickManager;
    }

    @Override
    public void tick() {
        //TODO
    }

}
