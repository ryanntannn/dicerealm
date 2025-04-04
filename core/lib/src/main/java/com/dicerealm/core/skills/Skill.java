package com.dicerealm.core.skills;

import java.util.UUID;

import com.dicerealm.core.combat.ActionType;
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
    private ActionType actiontype;
    private MultiDice damageDice;
    private int spellSlotCost;
    private int cooldown;
    private int remainingCooldown;

    public Skill(String name, String description, EntityClass entityClass, ActionType actiontype, int spellSlotCost, int diceSides, int cooldown) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
        this.entityClass = entityClass;
        this.actiontype = actiontype;
        this.spellSlotCost = spellSlotCost;
        this.damageDice = new MultiDice(1, diceSides);
        this.cooldown = cooldown;
        this.remainingCooldown = 0;
    }
    public Skill(String name, String description, EntityClass entityClass, ActionType actiontype, int spellSlotCost, int numDice, int diceSides, int cooldown) {
      this.id = UUID.randomUUID();
      this.displayName = name;
      this.description = description;
      this.entityClass = entityClass;
      this.actiontype = actiontype;
      this.spellSlotCost = spellSlotCost;
      this.damageDice = new MultiDice(numDice, diceSides);
      this.cooldown = cooldown;
      this.remainingCooldown = 0;
  }
    public String getDisplayName() { return displayName; }

    public String getDescription() { return description; }

    public EntityClass getEntityClass(){ return entityClass; }

    public ActionType getActionType() { return actiontype; }

    public int getSpellSlotCost() { return spellSlotCost; }

    public MultiDice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    public int getMaxDamage() { return damageDice.getNumDice() * damageDice.getSides(); }

    public UUID getId() { return id; }

    public int getCooldown() { return cooldown; }

    public int getRemainingCooldown() { return remainingCooldown; }

    public boolean isUsable() {
      return remainingCooldown <= 0;
    }

    public void reduceCooldown() {
        if (remainingCooldown > 0) {
          remainingCooldown--;
        }
    }

    public void activateCooldown() {
        this.remainingCooldown = cooldown;
    }

    @Override
    public String toString() { return displayName + " (Spell Slot Cost: " + spellSlotCost + ", Damage: " + damageDice + ")"; }

		@Override
		public String getType() {
			return type;
		}
}