package com.dicerealm.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dicerealm.core.command.PlayerActionCommand;
import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
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

	@Test 
	void testHandleFailedPlayerAction() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		MockRandom mockRandom = new MockRandom();
		mockRandom.setRandom(0.3);
		Room room = makeTestRoom().setRandomStrategy(mockRandom).setLLMStrategy(mockLLM).build();

		Player player = new Player();
		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new PlayerActionCommand("test action", new StatsMap(
			Map.of(Stat.CHARISMA, 10)
		))));

		assert(mockLLM.getLatestPrompt().contains("fail"));
	}

	@Test 
	void testHandleSuccessfulPlayerAction() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		MockRandom mockRandom = new MockRandom();
		mockRandom.setRandom(0.7);
		Room room = makeTestRoom().setRandomStrategy(mockRandom).setLLMStrategy(mockLLM).build();

		Player player = new Player();
		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new PlayerActionCommand("test action", new StatsMap(
			Map.of(Stat.CHARISMA, 10)
		))));

		assert(mockLLM.getLatestPrompt().contains("success"));
	}

	@Test 
	void testHandleCriticalFailPlayerAction() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		MockRandom mockRandom = new MockRandom();

		// this will roll a 1, which should be a critical fail and return a failure message no matter what
		mockRandom.setRandom(0);
		Room room = makeTestRoom().setRandomStrategy(mockRandom).setLLMStrategy(mockLLM).build();

		Player player = new Player();
		player.getStats().put(Stat.CHARISMA, Integer.MAX_VALUE);
		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new PlayerActionCommand("test action", new StatsMap(
			Map.of(Stat.CHARISMA, -Integer.MAX_VALUE)
		))));

		assert(mockLLM.getLatestPrompt().contains("fail"));
	}

	@Test 
	void testHandleCriticalSuccessPlayerAction() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		MockRandom mockRandom = new MockRandom();

		// this will roll a 20, which should be a critical success and return a success message no matter what
		mockRandom.setRandom(0.99);
		Room room = makeTestRoom().setRandomStrategy(mockRandom).setLLMStrategy(mockLLM).build();

		Player player = new Player();
		player.getStats().put(Stat.CHARISMA, -100);
		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new PlayerActionCommand("test action", new StatsMap(
			Map.of(Stat.CHARISMA, Integer.MAX_VALUE)
		))));

		assert(mockLLM.getLatestPrompt().contains("success"));
	}

	@Test 
	void testStartGame() {
		MockLLMStrategy mockLLM = new MockLLMStrategy(new GsonSerializer());
		Room room = makeTestRoom().setLLMStrategy(mockLLM).build();

		Player player = new Player();

		room.addPlayer(player);

		room.handlePlayerCommand(player.getId(), serializer.serialize(new StartGameCommand()));

		assert(mockLLM.getLatestPrompt().contains("start"));
	}
	
}
