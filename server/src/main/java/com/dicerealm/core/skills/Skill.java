package com.dicerealm.core.skills;

import java.util.UUID;
import com.dicerealm.core.dice.Dice;

/**
 * Base class for all skills
 */
public abstract class Skill {
    private UUID id;
    private String displayName;
    private String description;
    private String entityClass;
    private Dice damageDice;

    public Skill(String name, String description, String entityClass, int diceSides) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
        this.entityClass = entityClass;
        this.damageDice = new Dice(diceSides);
    }
    public String getDisplayName() { return displayName; }

    public String getDescription() { return description; }

    public String getEntityClass(){ return entityClass; }

    public String getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    public UUID getId() { return id; }

    @Override
    public String toString() { return displayName + " (Damage: " + damageDice + ")"; }
}