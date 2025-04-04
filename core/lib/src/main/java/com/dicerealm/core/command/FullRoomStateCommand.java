package com.dicerealm.core.command;

import com.dicerealm.core.room.RoomState;

public class FullRoomStateCommand extends Command {
	@SuppressWarnings("unused")
	private RoomState state;
	@SuppressWarnings("unused")
	private String myId;

	public FullRoomStateCommand(RoomState state, String myId) {
		this.type = "FULL_ROOM_STATE";
		this.state = state;
		this.myId = myId;
	}

	public RoomState getRoomState() {
		return state;
	}

	public String getMyId() {
		return myId;
	}
}
