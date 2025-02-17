package com.dicerealm.core;

import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

/**
 * Builder for creating a Room
 * 
 * @see Room
 * @see BroadcastStrategy
 * @see LLMStrategy
 * @see JsonSerializationStrategy
 */
public class RoomBuilder { 
	private BroadcastStrategy broadcastStrategy;
	private LLMStrategy llmStrategy;
	private JsonSerializationStrategy jsonSerializationStrategy;

	/**
	 * Set the BroadcastStrategy for the Room
	 * @param broadcastStrategy
	 * @return RoomBuilder
	 * 
	 * @see BroadcastStrategy
	 */
	public RoomBuilder setBroadcastStrategy(BroadcastStrategy broadcastStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		return this;
	}

	/**
	 * Set the LLMStrategy for the Room
	 * @param llmStrategy
	 * @return RoomBuilder
	 * 
	 * @see LLMStrategy
	 */
	public RoomBuilder setLLMStrategy(LLMStrategy llmStrategy) {
		this.llmStrategy = llmStrategy;
		return this;
	}

	/**
	 * Set the JsonSerializationStrategy for the Room
	 * @param jsonSerializationStrategy
	 * @return RoomBuilder
	 * 
	 * @see JsonSerializationStrategy
	 */
	public RoomBuilder setJsonSerializationStrategy(JsonSerializationStrategy jsonSerializationStrategy) {
		this.jsonSerializationStrategy = jsonSerializationStrategy;
		return this;
	}

	/**
	 * Build the Room
	 * @return Room
	 * @see Room
	 */
	public Room build() {
		return new Room(broadcastStrategy, llmStrategy, jsonSerializationStrategy);
	}
}
