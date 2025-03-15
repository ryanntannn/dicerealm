package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.player.Player;

public class PlayerJoinCommand extends Command {
    public Player player;
    public PlayerJoinCommand(){super("PLAYER_JOIN");}

    public Player getPlayer() {
        return player;
    }
}
