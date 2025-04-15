package com.dicerealm.core.dm;

import com.dicerealm.core.dm.DungeonMasterResponse.Stats;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;

public class DungeonMasterMonsterResponse {
    public Enemy[] enemies;
    
    public class Enemy {
			public String name;
			public Race race;
			public EntityClass entityClass;
			public Stats stats;
		}
}
