package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.ContainerInfo;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.RecipeCooking;
import net.minecraft.server.v1_14_R1.Recipes;
import net.minecraft.server.v1_14_R1.TileEntityTypes;

public class EntityBlastFurnace extends EntityFurnace {

    public EntityBlastFurnace(ContainerInfo containerInfo) {
        super(containerInfo, TileEntityTypes.BLAST_FURNACE, Recipes.BLASTING);
    }

    protected EntityBlastFurnace(ContainerInfo containerInfo, TileEntityTypes<?> tileentitytypes, Recipes<? extends RecipeCooking> recipes) {
        super(containerInfo, tileentitytypes, recipes);
    }

    @Override
    protected void setTileEntityInContainerInfoNull() {
        containerInfo.setEntityBlastFurnace(null);
    }

    @Override
    protected int fuelTime(ItemStack itemstack) {
        return super.fuelTime(itemstack) / 2;
    }

    @Override
    public EntityTypes getType() {
        return EntityTypes.BLAST_FURNACE;
    }
}
