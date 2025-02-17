package com.dicerealm.core.strategy;

import com.dicerealm.core.Player;
import com.dicerealm.core.command.Command;

public interface BroadcastStrategy {
		public abstract void sendToAllPlayers(Command command);
		public abstract void sendToPlayer(Command command, Player player);
		public abstract void sendToAllPlayersExcept(Command command, Player player);
}
