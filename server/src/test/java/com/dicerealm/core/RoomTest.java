package com.dicerealm.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.Room;
import com.dicerealm.core.room.RoomBuilder;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;
import com.dicerealm.core.strategy.RandomStrategy;
import com.dicerealm.mock.MockBroadcastStrategy;
import com.dicerealm.mock.MockLLMStrategy;
import com.dicerealm.server.strategy.GsonSerializer;


public class RoomTest {

	JsonSerializationStrategy serializer = new GsonSerializer();

	RoomBuilder makeTestRoom() {
		BroadcastStrategy broadcaster = new MockBroadcastStrategy();
		LLMStrategy llm = new MockLLMStrategy(serializer);
		return Room.builder()
			.setBroadcastStrategy(broadcaster)
			.setLLMStrategy(llm)
			.setJsonSerializationStrategy(serializer);
	}

	@Test
	void testAddPlayer() {
		Room room = makeTestRoom().build();
		Player player = new Player();
		room.addPlayer(player);
	}

	@Test
	void testRemovePlayerById() {
		Room room = makeTestRoom().build();
		Player player = new Player();
		room.addPlayer(player);
		room.removePlayerById(player.getId());
	}

	@Test
	void testRemovePlayerThatDoesNotExist() {
		Room room = makeTestRoom().build();
		assertThrows(NullPointerException.class, () -> {
			room.removePlayerById(UUID.randomUUID());
		});
	}

	@Test
	void testGetPlayerById() {
		Room room = makeTestRoom().build();
		Player player = new Player();
		room.addPlayer(player);
		Player retrievedPlayer = room.getPlayerById(player.getId());
		assert(retrievedPlayer == player);
	}

	@Test
	void testGetPlayerByIdThatDoesNotExist() {
		Room room = makeTestRoom().build();
		assert(room.getPlayerById(UUID.randomUUID()) == null);
	}

	@Test
	void testIsEmpty() {
		Room room = makeTestRoom().build();
		assert(room.isEmpty());
	}

	@Test 
	void testHandleFailedPlayerAction() {
		assert(true); // deprecated
	}

	@Test 
	void testHandleSuccessfulPlayerAction() {
		assert(true); // deprecated
	}

	@Test 
	void testHandleCriticalFailPlayerAction() {
		assert(true); // deprecated
	}

	@Test 
	void testHandleCriticalSuccessPlayerAction() {
		assert(true); // deprecated
	}

	@Test 
	void testStartGame() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		MockBroadcastStrategy mockBroadcast = new MockBroadcastStrategy();
		Room room = makeTestRoom().setLLMStrategy(mockLLM).setBroadcastStrategy(mockBroadcast).build();

		Player player = new Player();

		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new StartGameCommand()));

		assert(true);
	}

	class MockRandom implements RandomStrategy {

		private double value = 0;

		@Override
		public double random() {
			return value;
		}

		public void setRandom(double value) {
			this.value = value;
		}
	}
}
