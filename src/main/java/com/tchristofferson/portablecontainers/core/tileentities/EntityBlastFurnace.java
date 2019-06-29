package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import net.minecraft.server.v1_14_R1.ItemStack;

public class EntityBlastFurnace extends EntityFurnace {

    public EntityBlastFurnace(TickManager tickManager) {
        super(tickManager);
    }

    @Override
    protected void setTileEntityInTickerNull() {
        tickManager.setEntityBlastFurnace(null);
    }

    @Override
    protected int fuelTime(ItemStack itemstack) {
        return super.fuelTime(itemstack) / 2;
    }
}
