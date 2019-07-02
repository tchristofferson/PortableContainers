package com.tchristofferson.portablecontainers.core.tileentities;

public interface IEntityContainer {

    EntityTypes getType();

    enum EntityTypes {
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        BREWING_STAND
    }

}
