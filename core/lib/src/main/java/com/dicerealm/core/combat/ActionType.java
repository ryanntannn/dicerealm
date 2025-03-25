package com.dicerealm.core.combat;

public enum ActionType {
    MELEE,
    RANGED,
    MAGIC,
    SKILL;

    public boolean isMelee() {
        return this ==  MELEE;
    }
}