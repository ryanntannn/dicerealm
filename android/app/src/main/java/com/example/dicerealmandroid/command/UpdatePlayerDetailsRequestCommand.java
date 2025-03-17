package com.example.dicerealmandroid.command;


import com.example.dicerealmandroid.core.entity.Entity;

public class UpdatePlayerDetailsRequestCommand extends Command {
    public String displayName;
    public Entity.Race race;
    public Entity.EntityClass entityClass;
    public Entity.StatsMap baseStats;
    public UpdatePlayerDetailsRequestCommand(String displayName, Entity.Race race, Entity.EntityClass entityClass, Entity.StatsMap baseStats) {
        super("UPDATE_PLAYER_DETAILS_REQUEST");
        this.displayName = displayName;
        this.race = race;
        this.entityClass = entityClass;
        this.baseStats = baseStats;
    }

}

