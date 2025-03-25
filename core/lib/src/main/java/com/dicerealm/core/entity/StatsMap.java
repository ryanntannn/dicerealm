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
}
