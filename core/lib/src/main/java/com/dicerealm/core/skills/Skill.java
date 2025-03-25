package com.dicerealm.core.skills;

import java.util.UUID;
import com.dicerealm.core.dice.MultiDice;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.inventory.Identifiable;

/**
 * Base class for all skills
 */
public class Skill implements Identifiable {
		private String type = "SKILL";
    private UUID id;
    private String displayName;
    private String description;
    private EntityClass entityClass;
    private MultiDice damageDice;
    private int spellSlotCost;

    public Skill(String name, String description, EntityClass entityClass, int spellSlotCost, int numDice, int diceSides) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
        this.entityClass = entityClass;
        this.spellSlotCost = spellSlotCost;
        this.damageDice = new MultiDice(numDice, diceSides);
    }
    public String getDisplayName() { return displayName; }

    public String getDescription() { return description; }

    public EntityClass getEntityClass(){ return entityClass; }

    public int getSpellSlotCost() { return spellSlotCost; }

    public MultiDice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    public UUID getId() { return id; }

    @Override
    public String toString() { return displayName + " (Spell Slot Cost: " + spellSlotCost + ", Damage: " + damageDice + ")"; }

		@Override
		public String getType() {
			return type;
		}
}