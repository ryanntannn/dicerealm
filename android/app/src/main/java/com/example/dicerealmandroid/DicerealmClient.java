package com.example.dicerealmandroid;

import android.content.Context;
import android.util.Log;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.command.FullRoomStateCommand;
import com.example.dicerealmandroid.command.PlayerJoinCommand;
import com.example.dicerealmandroid.command.PlayerLeaveCommand;
import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.core.RoomState;
import com.example.dicerealmandroid.player.PlayerDataSource;
import com.example.dicerealmandroid.player.PlayerRepo;
import com.example.dicerealmandroid.room.RoomRepo;
import com.example.dicerealmandroid.util.Message;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DicerealmClient extends WebSocketClient {
    private Gson gson = new Gson();

    private String roomCode;

    private final static String baseUrl = "wss://better-tonye-dicerealm-f2e6ebbb.koyeb.app/room/";

    @Override
    public void onOpen() {
        System.out.println("onOpen");
    }

    @Override
    public void onTextReceived(String message) {
        Log.d("info", "message received + " + message);
        Command command = gson.fromJson(message, Command.class);
        System.out.println("Command is of type: " + command.getType());

        PlayerRepo playerRepo = new PlayerRepo();
        RoomRepo roomRepo = new RoomRepo();

        switch (command.getType()) {
            case "FULL_ROOM_STATE":
                FullRoomStateCommand fullRoomStateCommand  = gson.fromJson(message, FullRoomStateCommand.class);
                UUID myId = UUID.fromString(fullRoomStateCommand.getMyId());
                RoomState roomState = fullRoomStateCommand.getRoomState();

                roomRepo.setRoomState(roomState);

                // Indicate that you (the player) has join the room
                if(!playerRepo.getPlayerId().equals(myId)){
                    Player myPlayer = roomState.getPlayerMap().get(UUID.fromString(fullRoomStateCommand.getMyId()));
                    playerRepo.setPlayer(myPlayer);
                    Message.showMessage("You joined the room.");
                }

                break;
            case "PLAYER_JOIN":
                Player player = gson.fromJson(message, PlayerJoinCommand.class).getPlayer();

                roomRepo.addRoomStatePlayer(player);
                Message.showMessage(player.getDisplayName() + " has joined.");
                break;
            case "PLAYER_LEAVE":
                String playerId = gson.fromJson(message, PlayerLeaveCommand.class).getPlayerId();
                roomRepo.removeRoomStatePlayer(playerId);
                Message.showMessage("A player has left.");
                break;
            case "UPDATE_PLAYER_DETAILS":
                // TODO
                break;
            default:
                System.out.println("Command Not Handled: " + command.getType());
        }
    }

    @Override
    public void onBinaryReceived(byte[] data) {
        System.out.println("onBinaryReceived");
    }

    @Override
    public void onPingReceived(byte[] data) {
        System.out.println("onPingReceived");
    }

    @Override
    public void onPongReceived(byte[] data) {
        System.out.println("onPongReceived");
    }

    @Override
    public void onException(Exception e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onCloseReceived(int reason, String description) {
        System.out.println("onCloseReceived");
        this.roomCode = null;
        Message.showMessage("You left the room.");
    }

    public void connectToRoom(String roomCode) {
        this.setConnectTimeout(10000);
        this.setReadTimeout(60000);
        this.addHeader("Origin", "http://developer.example.com");
        this.enableAutomaticReconnection(5000);
        this.connect();
    }

    public DicerealmClient (String roomCode) throws URISyntaxException {
        super(new URI(DicerealmClient.baseUrl + roomCode));
        this.roomCode = roomCode;
        this.setConnectTimeout(10000);
        this.setReadTimeout(60000);
        this.addHeader("Origin", "http://developer.example.com");
        this.enableAutomaticReconnection(5000);
        this.connect();
    }


    public String getRoomCode() {
        return roomCode;
    }



}