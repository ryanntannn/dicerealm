package com.dicerealm.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dicerealm.core.command.CommandDeserializerStrategy;
import com.dicerealm.server.handlers.GsonDeserializer;


public class RoomTest {
	private BroadcastStrategy broadcaster = new MockBroadcastStrategy();
	private LLMStrategy llm = new MockLLMStrategy("{\"displayText\": \"mock response\", \"actionChoices\":[]}");
	private CommandDeserializerStrategy deserializer = new GsonDeserializer();
	private Room room = Room.builder()
		.setBroadcastStrategy(broadcaster)
		.setLLMStrategy(llm)
		.setCommandDeserializerStrategy(deserializer)
		.build();

	@Test
	void testAddPlayer() {
		Player player = new Player();
		room.addPlayer(player);
	}

	@Test
	void testRemovePlayerById() {
		Player player = new Player();
		room.addPlayer(player);
		room.removePlayerById(player.getId());
	}

	@Test
	void testRemovePlayerThatDoesNotExist() {
		assertThrows(NullPointerException.class, () -> {
			room.removePlayerById(UUID.randomUUID());
		});
	}

	@Test
	void testGetPlayerById() {
		Player player = new Player();
		room.addPlayer(player);
		Player retrievedPlayer = room.getPlayerById(player.getId());
		assert(retrievedPlayer == player);
	}

	@Test
	void getPlayerByIdThatDoesNotExist() {
		assert(room.getPlayerById(UUID.randomUUID()) == null);
	}
}
