package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.ContainerInfo;
import net.minecraft.server.v1_14_R1.Recipes;
import net.minecraft.server.v1_14_R1.TileEntityTypes;

public class EntitySmoker extends EntityBlastFurnace {

    public EntitySmoker(ContainerInfo containerInfo) {
        super(containerInfo, TileEntityTypes.SMOKER, Recipes.SMOKING);
    }

    @Override
    protected void setTileEntityInContainerInfoNull() {
        containerInfo.setEntitySmoker(null);
    }

    @Override
    public EntityTypes getType() {
        return EntityTypes.SMOKER;
    }

}
