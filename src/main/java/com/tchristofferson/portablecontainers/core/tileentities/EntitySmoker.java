package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import net.minecraft.server.v1_14_R1.Recipes;
import net.minecraft.server.v1_14_R1.TileEntityTypes;

public class EntitySmoker extends EntityBlastFurnace {

    public EntitySmoker(TickManager tickManager) {
        super(tickManager, TileEntityTypes.SMOKER, Recipes.SMOKING);
    }

    @Override
    protected void setTileEntityInTickerNull() {
        tickManager.setEntitySmoker(null);
    }

}
