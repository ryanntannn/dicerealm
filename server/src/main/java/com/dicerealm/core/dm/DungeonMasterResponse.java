package com.dicerealm.core.dm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonMasterResponse(
    @JsonProperty(required = true, value = "displayText") String displayText,
		@JsonProperty(required = true, value = "actionChoices") PlayerAction[] actionChoices,
		@JsonProperty(required = true, value = "removedItems") RemovedItems[] removedItems
){
		public record PlayerAction(
			@JsonProperty(required = true, value = "displayText") String action,
			@JsonProperty(required = true, value = "playerId") String playerId
		) {
		}

		public record RemovedItems(
			@JsonProperty(required = true, value = "itemId") String itemId,
			@JsonProperty(required = true, value = "playerId") String playerId
		) {
		}
}