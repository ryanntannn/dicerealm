package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.RoomState;

public class FullRoomStateCommand extends Command {
    private RoomState state;
    private String myId;
    public FullRoomStateCommand() {
        super("FULL_ROOM_STATE");
    }

    public RoomState getRoomState() {
        return state;
    }

    public String getMyId() {
        return myId;
    }
}
