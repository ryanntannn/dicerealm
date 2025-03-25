package com.example.dicerealmandroid;

import android.util.Log;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.UpdatePlayerDetailsCommand;
import com.dicerealm.core.command.dialogue.DialogueTurnActionCommand;
import com.dicerealm.core.command.dialogue.StartTurnCommand;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.command.PlayerLeaveCommand;

import com.dicerealm.core.room.RoomState;
import com.example.dicerealmandroid.game.GameRepo;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.example.dicerealmandroid.player.PlayerRepo;
import com.example.dicerealmandroid.room.RoomRepo;
import com.example.dicerealmandroid.util.Message;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DicerealmClient extends WebSocketClient {
    private Gson gson = Serialization.makeDicerealmGsonInstance();

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
        DialogRepo dialogRepo = new DialogRepo();
        GameRepo gameRepo = new GameRepo();

        try {
            switch (command.getType()) {
                case "FULL_ROOM_STATE":
                    FullRoomStateCommand fullRoomStateCommand = gson.fromJson(message, FullRoomStateCommand.class);

                    RoomState roomState = fullRoomStateCommand.getRoomState();
                    Player myPlayer = roomState.getPlayerMap().get(UUID.fromString(fullRoomStateCommand.getMyId()));

                    roomRepo.setRoomState(roomState);
                    playerRepo.setPlayer(myPlayer);
                    Message.showMessage("You joined the room.");
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
                    // Update player details while keeping the item inventory and skills inventory intact
                    UpdatePlayerDetailsCommand updatePlayerDetailsCommand = gson.fromJson(message, UpdatePlayerDetailsCommand.class);
                    playerRepo.setPlayer(updatePlayerDetailsCommand.player);
                    break;

                case "START_GAME":
                    Message.showMessage("Game started.");
                    gameRepo.gameStarted();
                    break;

                case "DIALOGUE_START_TURN":
                    Message.showMessage("Turn started.");
                    StartTurnCommand startTurnCommand = gson.fromJson(message, StartTurnCommand.class);
                    int turnNumber = startTurnCommand.getDialogueTurn().getTurnNumber();
                    String dialog_msg = startTurnCommand.getDialogueTurn().getDungeonMasterText();

                    Dialog dialogueTurn = new Dialog(dialog_msg, null, turnNumber);
                    dialogRepo.updateTurnHistory(dialogueTurn);
                    break;

                case "SHOW_PLAYER_ACTIONS":
                    ShowPlayerActionsCommand showPlayerActionsCommand = gson.fromJson(message, ShowPlayerActionsCommand.class);
                    dialogRepo.setPlayerActions(showPlayerActionsCommand);
                    break;

                case "DIALOGUE_TURN_ACTION":
                    // Get all messages/actions by other players as well
                    DialogueTurnActionCommand dialogueTurnActionCommand = gson.fromJson(message, DialogueTurnActionCommand.class);

                    Dialog dialogueTurnAction = new Dialog(dialogueTurnActionCommand.getAction(), dialogueTurnActionCommand.getPlayerId(), dialogueTurnActionCommand.getTurnNumber());
                    dialogRepo.updateTurnHistory(dialogueTurnAction);
                    break;

                case "DIALOGUE_END_TURN":
                    // Send whatever the player selected to the server
                    Message.showMessage("Turn ended.");
                    break;

                case "PLAYER_EQUIP_ITEM_RESPONSE":
                    try {
                        PlayerEquipItemResponse playerEquipItemResponse = gson.fromJson(message, PlayerEquipItemResponse.class);
                        playerRepo.equipItem(playerEquipItemResponse);
                        Message.showMessage("Equipped " + playerEquipItemResponse.getItem().getDisplayName() + " to " + playerEquipItemResponse.getBodyPart());
                    }catch(IllegalArgumentException e){
                        Message.showMessage(e.getMessage());
                        break;
                    }
                    break;

                default:
                    System.out.println("Command Not Handled: " + command.getType());
            }
        } catch (Exception e) {
            Log.e("Error", "Something went wrong", e);
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
        this.setConnectTimeout(6000000); // 10 minutes
        this.setReadTimeout(60000);
        this.addHeader("Origin", "http://developer.example.com");
        this.enableAutomaticReconnection(5000);
        this.connect();
    }


    public String getRoomCode() {
        return roomCode;
    }


}

