package com.dicerealm.core;

import com.dicerealm.core.command.CommandDeserializerStrategy;

public class RoomBuilder { 
	private BroadcastStrategy broadcastStrategy;
	private LLMStrategy llmStrategy;
	private CommandDeserializerStrategy commandDeserializerStrategy;

	public RoomBuilder setBroadcastStrategy(BroadcastStrategy broadcastStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		return this;
	}

	public RoomBuilder setLLMStrategy(LLMStrategy llmStrategy) {
		this.llmStrategy = llmStrategy;
		return this;
	}

	public RoomBuilder setCommandDeserializerStrategy(CommandDeserializerStrategy commandDeserializerStrategy) {
		this.commandDeserializerStrategy = commandDeserializerStrategy;
		return this;
	}

	public Room build() {
		return new Room(broadcastStrategy, llmStrategy, commandDeserializerStrategy);
	}
}
