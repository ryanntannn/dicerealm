package com.example.dicerealmandroid.command;

public class PlayerLeaveCommand extends Command{
    private String playerId;
    public PlayerLeaveCommand(){
        super("PLAYER_LEAVE");
    }

    public String getPlayerId() {
        return playerId;
    }
}
