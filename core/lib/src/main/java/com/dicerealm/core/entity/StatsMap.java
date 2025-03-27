package com.dicerealm.core.entity;

import java.util.HashMap;
import java.util.Map;

public class StatsMap extends HashMap<Stat, Integer> implements Stats {
	@Override
	public int getStat(Stat stat) {
		return super.get(stat);
	}

	public StatsMap() {
		super();
	}

	public StatsMap(Map<Stat, Integer> stats) {
		super.putAll(stats);
	}

	public static String getStatText(Stat stat) {
		switch (stat) {
			case MAX_HEALTH:
				return "Max Health";
			case ARMOUR_CLASS:
				return "Armour Class";
			case STRENGTH:
				return "Strength";
			case DEXTERITY:
				return "Dexterity";
			case CONSTITUTION:
				return "Constitution";
			case INTELLIGENCE:
				return "Intelligence";
			case WISDOM:
				return "Wisdom";
			case CHARISMA:
				return "Charisma";
			default:
				return "Unknown";
		}
	}
}
