package com.dicerealm.core.entity;

public interface Stats {
	public enum Stat {
		MAX_HEALTH,
		ARMOUR_CLASS,
		STRENGTH,
		DEXTERITY,
		CONSTITUTION,
		INTELLIGENCE,
		WISDOM,
		CHARISMA
	}
	public abstract int getStat(Stat stat);
}
