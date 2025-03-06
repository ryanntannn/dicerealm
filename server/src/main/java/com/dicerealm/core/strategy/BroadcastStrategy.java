package com.dicerealm.core.strategy;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.player.Player;

public interface BroadcastStrategy {
		public abstract void sendToAllPlayers(Command command);
		public abstract void sendToPlayer(Command command, Player player);
		public abstract void sendToAllPlayersExcept(Command command, Player player);
}
