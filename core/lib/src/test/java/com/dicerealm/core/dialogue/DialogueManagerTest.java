package com.dicerealm.core.dialogue;

import org.junit.jupiter.api.Test;

import com.dicerealm.core.dm.DungeonMaster;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.RandomStrategy;
import com.dicerealm.mock.MockBroadcastStrategy;
import com.dicerealm.mock.MockJsonSerializationStrategy;
import com.dicerealm.mock.MockLLMStrategy;

public class DialogueManagerTest {
	@Test 
	public void testStartDialogue() {
		RoomState state = new RoomState();
		MockBroadcastStrategy broadcast = new MockBroadcastStrategy();
		RandomStrategy random = new RandomStrategy() {
			@Override
			public double random() {
				return 0.5;
			}
		};

		RoomContext context = new RoomContext(state, null, broadcast, random, null, null, null);
		DialogueManager.startNewDialogueTurn("Test", context);
		assert(context.getRoomState().getCurrentDialogueTurn().getDungeonMasterText().equals("Test"));
		DialogueManager.startNewDialogueTurn("Test2", context);
		assert(context.getRoomState().getCurrentDialogueTurn().getDungeonMasterText().equals("Test2"));
	}


	@Test
	public void testEndDialogueTurn() {
		JsonSerializationStrategy json = new MockJsonSerializationStrategy();
		MockLLMStrategy llm = new MockLLMStrategy(json);

		DungeonMasterResponse mockResponse = new DungeonMasterResponse();


		RoomState state = new RoomState();

		mockResponse.locationId = state.getLocationGraph().getCurrentLocation().getId().toString();
		mockResponse.actionChoices = new DungeonMasterResponse.PlayerAction[0];
		mockResponse.contextSummary = "Test";
		mockResponse.displayText = "Test";

		llm.setPromptSchemaResponse(mockResponse);

		MockBroadcastStrategy broadcast = new MockBroadcastStrategy();

		RandomStrategy random = new RandomStrategy() {
			@Override
			public double random() {
				return 0.5;
			}
		};

		RoomContext context = new RoomContext(state, new DungeonMaster(llm, json, state), broadcast, random, null, null, null);

		Player player1 = new Player();
		Player player2 = new Player();

		context.getRoomState().addPlayer(new Player());
		DialogueManager.startNewDialogueTurn("Test", context);

		context.getRoomState().getCurrentDialogueTurn().setDialogueTurnAction(player1.getId(), new DialogueTurnAction("Action 1", new StatsMap(){{
			put(Stat.STRENGTH, 10);
		}}));

		context.getRoomState().getCurrentDialogueTurn().setDialogueTurnAction(player2.getId(), new DialogueTurnAction("Action 2", new StatsMap(){{
			put(Stat.WISDOM, 10);
		}}));

		DialogueManager.endDialogueTurn(context);
	}
}
