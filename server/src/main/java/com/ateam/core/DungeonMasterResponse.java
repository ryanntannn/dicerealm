package com.ateam.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DungeonMasterResponse(
    @JsonProperty(required = true, value = "displayText") String displayText,
		@JsonProperty(required = true, value = "actionChoices") PlayerAction[] actionChoices
){
		record PlayerAction(
			@JsonProperty(required = true, value = "displayText") String action,
			@JsonProperty(required = true, value = "actionId") String actionId
		) {
		}
}