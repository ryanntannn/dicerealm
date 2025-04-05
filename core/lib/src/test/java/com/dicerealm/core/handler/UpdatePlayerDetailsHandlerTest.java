package com.dicerealm.core.handler;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.command.UpdatePlayerDetailsRequestCommand;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.mock.MockBroadcastStrategy;

public class UpdatePlayerDetailsHandlerTest {
	@Test
	public void testUpdatePlayerDetailsHandler() {
		BroadcastStrategy broadcastStrategy = new MockBroadcastStrategy();

		UpdatePlayerDetailsHandler updatePlayerDetailsHandler = new UpdatePlayerDetailsHandler();

		RoomState roomState = new RoomState();
		roomState.setState(RoomState.State.LOBBY);

		Player player = new Player();
		UUID playerId = player.getId();

		roomState.getPlayerMap().put(playerId, player);

		UpdatePlayerDetailsRequestCommand updatePlayerDetailsRequestCommand = new UpdatePlayerDetailsRequestCommand();

		updatePlayerDetailsRequestCommand.displayName = "Test";
		updatePlayerDetailsRequestCommand.race = Race.HUMAN;
		updatePlayerDetailsRequestCommand.entityClass = EntityClass.CLERIC;
		updatePlayerDetailsRequestCommand.baseStats = player.getStats();

		updatePlayerDetailsHandler.handle(playerId, updatePlayerDetailsRequestCommand, new RoomContext(roomState, null, broadcastStrategy, null, null, null, null));

		Player updatedPlayer = roomState.getPlayerMap().get(playerId);

		assert(updatedPlayer.getDisplayName().equals("Test"));

		roomState.setState(RoomState.State.DIALOGUE_TURN);

		assertThrows(RuntimeException.class, () -> {
			updatePlayerDetailsHandler.handle(playerId, updatePlayerDetailsRequestCommand, new RoomContext(roomState, null, broadcastStrategy, null, null, null, null));
		});

		UUID newPlayerId = UUID.randomUUID();

		assertThrows(RuntimeException.class, () -> {
			updatePlayerDetailsHandler.handle(newPlayerId, updatePlayerDetailsRequestCommand, new RoomContext(roomState, null, broadcastStrategy, null, null, null, null));
		});
	}
}
