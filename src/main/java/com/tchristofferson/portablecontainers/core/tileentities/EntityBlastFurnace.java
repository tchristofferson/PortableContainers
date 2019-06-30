package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.RecipeCooking;
import net.minecraft.server.v1_14_R1.Recipes;
import net.minecraft.server.v1_14_R1.TileEntityTypes;

public class EntityBlastFurnace extends EntityFurnace {

    public EntityBlastFurnace(TickManager tickManager) {
        super(tickManager, TileEntityTypes.BLAST_FURNACE, Recipes.BLASTING);
    }

    protected EntityBlastFurnace(TickManager tickManager, TileEntityTypes<?> tileentitytypes, Recipes<? extends RecipeCooking> recipes) {
        super(tickManager, tileentitytypes, recipes);
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
