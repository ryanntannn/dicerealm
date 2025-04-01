package com.dicerealm.mock;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.core.locations.Path;
import com.dicerealm.core.monster.Monster;

public class MockLocationGraph {
	public static LocationGraph makeLocationGraph() {
		Location tavern = new Location("Springfield Village Tavern", "A small village tavern bustling with activity.");
		Location townSquare = new Location("Springfield Village Town Square", "The center of the village, where all the villagers gather.");

		townSquare.getEntities().add(new Monster("Lesser Demon", Race.DEMON, EntityClass.WARRIOR, new StatsMap.Builder().set(Stat.STRENGTH, 3).set(Stat.MAX_HEALTH, 10).build()));

		Location blacksmith = new Location("Springfield Village Blacksmith", "A small blacksmith shop.");

		Location caveEntrance = new Location("Mysterious Cave Entrance", "A hidden cave entrance, protected by a magical barrier. Players must solve a riddle to enter.");

		Location dungeonHallway1 = new Location("Dungeon Hallway 1", "A dark, damp hallway in the dungeon. The walls are lined with torches. Players must be careful of traps.");

		Location dungeonRoom1 = new Location("Dungeon Room 1", "A large room in the dungeon. The room is filled with treasure, but guarded by a powerful monster.");

		Path tavernToTownSquare = new Path(tavern, townSquare, 1);
		Path townSquareToTavern = new Path(townSquare, tavern, 1);
		Path townSquareToBlacksmith = new Path(townSquare, blacksmith, 1);
		Path townSquareToCaveEntrance = new Path(townSquare, caveEntrance, 100);
		Path caveEntranceToDungeonHallway1 = new Path(caveEntrance, dungeonHallway1, 1);
		Path dungeonHallway1ToDungeonRoom1 = new Path(dungeonHallway1, dungeonRoom1, 30);

		LocationGraph graph = new LocationGraph(tavern);

		graph.addN(tavern);
		graph.addN(townSquare);
		graph.addN(blacksmith);
		graph.addN(caveEntrance);
		graph.addN(dungeonHallway1);
		graph.addN(dungeonRoom1);
		
		graph.addE(tavernToTownSquare);
		graph.addE(townSquareToTavern);
		graph.addE(townSquareToBlacksmith);
		graph.addE(townSquareToCaveEntrance);
		graph.addE(caveEntranceToDungeonHallway1);
		graph.addE(dungeonHallway1ToDungeonRoom1);

		return graph;
	}
}
