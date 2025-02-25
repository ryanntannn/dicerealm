package com.dicerealm.core.skills;

import java.util.UUID;
import com.dicerealm.core.dice.Dice;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.inventory.Identifiable;

/**
 * Base class for all skills
 */
public abstract class Skill implements Identifiable {
    private UUID id;
    private String displayName;
    private String description;
    private EntityClass entityClass;
    private Dice damageDice;

    public Skill(String name, String description, EntityClass entityClass, int diceSides) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
        this.entityClass = entityClass;
        this.damageDice = new Dice(diceSides);
    }
    public String getDisplayName() { return displayName; }

    public String getDescription() { return description; }

    public EntityClass getEntityClass(){ return entityClass; }

    public Dice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    public UUID getId() { return id; }

    @Override
    public String toString() { return displayName + " (Damage: " + damageDice + ")"; }
}