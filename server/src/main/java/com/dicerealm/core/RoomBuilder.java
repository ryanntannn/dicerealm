package com.dicerealm.core;


public class RoomBuilder { 
	private BroadcastStrategy broadcastStrategy;
	private LLMStrategy llmStrategy;
	private JsonSerializationStrategy jsonSerializationStrategy;

	public RoomBuilder setBroadcastStrategy(BroadcastStrategy broadcastStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		return this;
	}

	public RoomBuilder setLLMStrategy(LLMStrategy llmStrategy) {
		this.llmStrategy = llmStrategy;
		return this;
	}

	public RoomBuilder setJsonSerializationStrategy(JsonSerializationStrategy jsonSerializationStrategy) {
		this.jsonSerializationStrategy = jsonSerializationStrategy;
		return this;
	}

	public Room build() {
		return new Room(broadcastStrategy, llmStrategy, jsonSerializationStrategy);
	}
}
