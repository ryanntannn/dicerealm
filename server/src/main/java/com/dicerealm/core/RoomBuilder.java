package com.dicerealm.core;

import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;
import com.dicerealm.core.strategy.RandomStrategy;

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
	private RandomStrategy randomStrategy = new RandomStrategy() {
		@Override
		public double random() {
			return Math.random();
		}
	};

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
	 * Set the RandomStrategy for the Room
	 * @param randomStrategy
	 * @return RoomBuilder
	 * 
	 * @see RandomStrategy
	 */
	public RoomBuilder setRandomStrategy(RandomStrategy randomStrategy) {
		this.randomStrategy = randomStrategy;
		return this;
	}

	/**
	 * Build the Room
	 * @return Room
	 * @see Room
	 */
	public Room build() {
		return new Room(broadcastStrategy, llmStrategy, jsonSerializationStrategy, randomStrategy);
	}
}
