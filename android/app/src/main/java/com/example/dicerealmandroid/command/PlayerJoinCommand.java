package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.Player;

public class PlayerJoinCommand extends Command {
    private Player player;
    public PlayerJoinCommand(){super("PLAYER_JOIN");}

    public Player getPlayer() {
        return player;
    }
}
