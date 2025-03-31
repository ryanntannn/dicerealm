package com.dicerealm.core.handler;

import java.util.ArrayList;
import java.util.UUID;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.dialogue.DialogueManager;
import com.dicerealm.core.dm.DungeonMasterLocationResponse;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.core.locations.Path;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class StartGameHandler extends CommandHandler<StartGameCommand> {
	public StartGameHandler() {
		super("START_GAME");
	}

	@Override
	public void handle(UUID playerId, StartGameCommand command, RoomContext context) {
		if (context.getRoomState().getState() != RoomState.State.LOBBY) {
			throw new IllegalStateException("Game has already started");
		}
		context.getBroadcastStrategy().sendToAllPlayers(command);
		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
		DungeonMasterLocationResponse locationResponse = context.getDungeonMaster().handleLocationGeneration("Forgotten Kingdom of Shadows");
		context.getRoomState().setLocationGraph(generateLocations(locationResponse));
		context.getRoomState().getLocationGraph();
		DungeonMasterResponse response = context.getDungeonMaster().handleDialogueTurn("Start the adventure.");
		DialogueManager.broadcastPlayerActions(response.actionChoices, context);
		DialogueManager.broadcastLocationChange(response, context);
		DialogueManager.startNewDialogueTurn(response.displayText, context);
	}

	public LocationGraph generateLocations(DungeonMasterLocationResponse response){
		ArrayList<Location> locations = new ArrayList<>();
		ArrayList<Path> paths = new ArrayList<>();
		for (DungeonMasterLocationResponse.LocationList location : response.locations) {
			Location loc = new Location(location.displayName, location.description);
			locations.add(loc);
		}
		for (DungeonMasterLocationResponse.PathList path : response.paths) {
			Location from = locations.stream()
					.filter(Location -> path.from.equals(Location.getDisplayName()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Location not found: " + path.from));
			Location to = locations.stream()
					.filter(Location -> path.to.equals(Location.getDisplayName()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Location not found: " + path.to));
			Path p = new Path(from, to, path.distance);
			paths.add(p);
		}
		LocationGraph graph = new LocationGraph(locations.get(0));
		for (Location location : locations) {
			graph.addN(location);
		}
		for (Path path : paths) {
			graph.addE(path);
		}

		return graph;
	}
}
