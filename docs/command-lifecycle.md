# System Architecture

The system architecture is a high-level overview of the system's components and their interactions. The architecture is designed to be modular and scalable, allowing for easy integration of new capabilities.

## Commands

Commands are the primary way that the client and server communicate. Commands are JSON objects that have a `type` field that specifies the type of command, and additional fields that provide the necessary data for the command. The server processes commands by deserializing them and executing the appropriate command handler.

Example Command, `CHANGE_LOCATION`, that changes the party's location to a tavern:

```json
{
  "type": "CHANGE_LOCATION",
  "location": {
    "id": "<SOME-UUID>",
    "displayName": "Tavern",
    "description": "A cozy tavern with a warm fire and friendly patrons."
  }
}
```

```java
public class ChangeLocationCommand extends Command {
	private Location location;

	public ChangeLocationCommand(Location newLocation) {
		super.type = "CHANGE_LOCATION";
		this.location = newLocation;
	}

	public Location getLocation() {
		return location;
	}
}
```

## Command Handler

The command handler is responsible for processing commands received from the client. The command handler deserializes the command and executes the appropriate command handler based on the command type. The command handler is responsible for updating the game state and sending responses back to the client.

Each command handler should handle the command in the following way:

1. Validate the command. E.g. can the player equip the item? Is the player in the correct location?
2. Update the game state via the `RoomContext`.
3. Broadcast the updated game state to all players in the room.

Command handlers are registered with the `CommandRouter` in the `Room` class, but can also be used independently during testing.

```java
public class StartGameHandler extends CommandHandler<StartGameCommand> {
	PlayerMessageHandler playerMessageHandler = new PlayerMessageHandler();
	public StartGameHandler() {
		super("START_GAME");
	}

	@Override
	public void handle(UUID playerId, StartGameCommand command, RoomContext context) {
		// STEP 1: Validate the command
		if (context.getRoomState().getState() != RoomState.State.LOBBY) {
			throw new IllegalStateException("Game has already started");
		}
		// STEP 2: Update the game state
		context.getRoomState().setState(RoomState.State.DIALOGUE);

		// STEP 3: Broadcast the updated game state
		context.getBroadcastStrategy().sendToAllPlayers(command);
		playerMessageHandler.handleNormalMessage(playerId, "Let's start the adventure!", context);
	}
}
```

## Room Context

The `RoomContext` is a container that holds the game state and provides access to the various components of the game. The `RoomContext` is passed to the command handler, allowing the handler to access the game state and update it as needed.

The `RoomContext` provides access to the following components:

- `RoomState`: The current state of the room.
- `BroadcasterStrategy`: The strategy used to broadcast messages to players.
- `DungeonMaster`: The AI powered DM that can perform state transitions.
- `CombatManager`: The manager that handles combat between players and monsters.
