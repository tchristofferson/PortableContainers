package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;

public class EntityBlastFurnace extends EntityFurnace {

    public EntityBlastFurnace(TickManager tickManager) {
        super(tickManager);
    }

    @Override
    protected void setTileEntityInTickerNull() {
        tickManager.setEntityBlastFurnace(null);
    }
}
