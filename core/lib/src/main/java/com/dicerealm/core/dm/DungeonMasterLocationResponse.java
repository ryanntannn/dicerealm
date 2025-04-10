package com.dicerealm.core.dm;

import com.dicerealm.core.dm.DungeonMasterResponse.Stats;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;

public class DungeonMasterLocationResponse {
    public Location[] locations;
    public PathList[] paths;

    public class Location {
        public String displayName;
        public String description;
				public Enemy[] enemies;
    }

    public class PathList {
        public String from;
        public String to;
        public int distance;
    }

		public class Enemy {
			public String name;
			public Race race;
			public EntityClass entityClass;
			public Stats stats;
		}
}
