package com.dicerealm.core.command;

import com.dicerealm.core.RoomState;

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
}
