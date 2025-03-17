package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.player.Player;
public class UpdatePlayerDetailsCommand extends Command {
    public Player player;
    public UpdatePlayerDetailsCommand(Player player) {
        super("UPDATE_PLAYER_DETAILS");
        this.player = player;
    }
}

