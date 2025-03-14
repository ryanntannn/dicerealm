package com.example.dicerealmandroid;

import android.content.Context;
import android.util.Log;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.command.FullRoomStateCommand;
import com.example.dicerealmandroid.command.PlayerJoinCommand;
import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.core.RoomState;
import com.example.dicerealmandroid.util.Message;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DicerealmClient extends WebSocketClient {
    private Gson gson = new Gson();

    private RoomState roomState = new RoomState();
    private String roomCode;
    private Context context;
    private Player you;


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
        switch (command.getType()) {
            case "FULL_ROOM_STATE":
                roomState = gson.fromJson(message, FullRoomStateCommand.class).getRoomState();
                break;
            case "PLAYER_JOIN":
                // TODO
                Player player = gson.fromJson(message, PlayerJoinCommand.class).getPlayer();
                if(you == null){
                    you = player;
                    Message.showMessage("You have joined.");
                }else{
                    Message.showMessage("A player has joined.");
                }
//                PlayerRepo.getInstance().setPlayer(player);
                break;
            case "PLAYER_LEAVE":
                // TODO
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

    public RoomState getRoomState() {
        return roomState;
    }

    public String getRoomCode() {
        return roomCode;
    }



}
