package com.dicerealm.core;

import com.dicerealm.core.command.Command;

public class MockBroadcastStrategy implements BroadcastStrategy {

	@Override
	public void sendToAllPlayers(Command command) {
		System.out.println("MockBroadcastStrategy: " + command);
	}

	@Override
	public void sendToPlayer(Command command, Player player) {
		System.out.println("MockBroadcastStrategy: " + command + " to " + player);
	}

	@Override
	public void sendToAllPlayersExcept(Command command, Player player) {
		System.out.println("MockBroadcastStrategy: " + command + " except " + player);
	}
}
