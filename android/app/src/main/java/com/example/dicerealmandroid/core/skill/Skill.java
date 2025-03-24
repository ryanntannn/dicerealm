package com.example.dicerealmandroid.core.skill;

import com.example.dicerealmandroid.core.Identifiable;
import com.example.dicerealmandroid.core.dice.MultiDice;
import com.example.dicerealmandroid.core.entity.Entity;

import java.util.UUID;

public class Skill implements Identifiable {
    private UUID id;
    private String displayName;
    private String description;
    private Entity.EntityClass entityClass;
    private MultiDice damageDice;
    private int spellSlotCost;

    public Skill(String name, String description, Entity.EntityClass entityClass, int spellSlotCost, int numDice, int diceSides) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
        this.entityClass = entityClass;
        this.spellSlotCost = spellSlotCost;
        this.damageDice = new MultiDice(numDice, diceSides);
    }
    public String getDisplayName() { return displayName; }

    public String getDescription() { return description; }

    public Entity.EntityClass getEntityClass(){ return entityClass; }

    public int getSpellSlotCost() { return spellSlotCost; }

    public MultiDice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    public UUID getId() { return id; }

    @Override
    public String toString() { return displayName + " (Spell Slot Cost: " + spellSlotCost + ", Damage: " + damageDice + ")"; }
}
