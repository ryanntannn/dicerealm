package com.example.dicerealmandroid;

import android.util.Log;

import com.dicerealm.core.command.ChangeLocationCommand;
import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.UpdatePlayerDetailsCommand;
import com.dicerealm.core.command.combat.CombatEndCommand;
import com.dicerealm.core.command.combat.CombatEndTurnCommand;
import com.dicerealm.core.command.combat.CombatStartCommand;
import com.dicerealm.core.command.combat.CombatStartTurnCommand;
import com.dicerealm.core.command.combat.CombatEndTurnCommand;
import com.dicerealm.core.command.dialogue.DialogueTurnActionCommand;
import com.dicerealm.core.command.dialogue.EndTurnCommand;
import com.dicerealm.core.command.dialogue.StartTurnCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.command.PlayerLeaveCommand;

import com.dicerealm.core.room.RoomState;
import com.example.dicerealmandroid.game.GameRepo;
import com.example.dicerealmandroid.game.combat.CombatRepo;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.example.dicerealmandroid.player.PlayerRepo;
import com.example.dicerealmandroid.room.RoomRepo;
import com.example.dicerealmandroid.util.Message;
import com.example.dicerealmandroid.util.Serialization;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DicerealmClient extends WebSocketClient {
    private Gson gson = Serialization.makeDicerealmGsonInstance();

    private String roomCode;

    private final static String baseUrl = "wss://better-tonye-dicerealm-f2e6ebbb.koyeb.app/room/";
    private final PlayerRepo playerRepo = new PlayerRepo();
    private final RoomRepo roomRepo = new RoomRepo();
    private final DialogRepo dialogRepo = new DialogRepo();
    private final GameRepo gameRepo = new GameRepo();
    private final CombatRepo combatRepo = new CombatRepo();

    @Override
    public void onOpen() {
        System.out.println("onOpen");
        Message.showMessage("You joined the room.");
    }

    @Override
    public void onTextReceived(String message) {
        Log.d("info", "message received + " + message);
        Command command = gson.fromJson(message, Command.class);
        System.out.println("Command is of type: " + command.getType());

        try {
            switch (command.getType()) {
                case "FULL_ROOM_STATE":
                    FullRoomStateCommand fullRoomStateCommand = gson.fromJson(message, FullRoomStateCommand.class);

                    RoomState roomState = fullRoomStateCommand.getRoomState();
                    Player myPlayer = roomState.getPlayerMap().get(UUID.fromString(fullRoomStateCommand.getMyId()));

                    roomRepo.setRoomState(roomState);
                    playerRepo.setPlayer(myPlayer);
                    gameRepo.changeLocation(roomState.getLocationGraph().getCurrentLocation());
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
                    // Update player details
                    UpdatePlayerDetailsCommand updatePlayerDetailsCommand = gson.fromJson(message, UpdatePlayerDetailsCommand.class);
                    playerRepo.setPlayer(updatePlayerDetailsCommand.player);
                    break;

                case "START_GAME":
                    Message.showMessage("Initializing your game, please wait");
                    roomRepo.changeState(RoomState.State.DIALOGUE_PROCESSING);
                    break;

                case "DIALOGUE_START_TURN":
                    // Re-Enable the buttons for the player
                    StartTurnCommand startTurnCommand = gson.fromJson(message, StartTurnCommand.class);

                    int turnNumber = startTurnCommand.getDialogueTurn().getTurnNumber();
                    String dialog_msg = startTurnCommand.getDialogueTurn().getDungeonMasterText();

                    Dialog dialogueTurn = new Dialog(dialog_msg, null, turnNumber);
                    dialogRepo.updateTurnHistory(dialogueTurn);
                    roomRepo.changeState(RoomState.State.DIALOGUE_TURN);

                    Message.showMessage("Turn " + startTurnCommand.getDialogueTurn().getTurnNumber());
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
                    EndTurnCommand endTurnCommand = gson.fromJson(message, EndTurnCommand.class);
                    // Disable the buttons for the player
                    roomRepo.changeState(RoomState.State.DIALOGUE_PROCESSING);
                    dialogRepo.setActionResult(endTurnCommand.getActionResultDetail());
                    Message.showMessage("Turn ended.");
                    break;

                case "PLAYER_EQUIP_ITEM_RESPONSE":
                    PlayerEquipItemResponse playerEquipItemResponse = gson.fromJson(message, PlayerEquipItemResponse.class);

                    // Show msg if the item was equipped successfully to the specific player
                    if(playerRepo.equipItem(playerEquipItemResponse)){
                        Message.showMessage("Equipped " + playerEquipItemResponse.getItem().getDisplayName() + " to " + playerEquipItemResponse.getBodyPart());
                    }
                    break;

                case "CHANGE_LOCATION":
                    ChangeLocationCommand changeLocationCommand = gson.fromJson(message, ChangeLocationCommand.class);
                    gameRepo.changeLocation(changeLocationCommand.getLocation());
                    Message.showMessage("Party has moved to " + changeLocationCommand.getLocation().getDisplayName());
                    break;

                case "COMBAT_START":
                    CombatStartCommand combatStartCommand = gson.fromJson(message, CombatStartCommand.class);
                    combatRepo.setLatestTurn(combatStartCommand.getDisplayText());
                    combatRepo.setInitiativeResults(combatStartCommand.getInitiativeResults());
                    Message.showMessage("Enemies found! Switching to combat mode.");
                    roomRepo.changeState(RoomState.State.BATTLE);
                    break;

                case "COMBAT_START_TURN":
                    CombatStartTurnCommand combatStartTurnCommand = gson.fromJson(message, CombatStartTurnCommand.class);
                    if(playerRepo.getPlayerId().equals(combatStartTurnCommand.getCurrentTurnEntityId())){
                        Message.showMessage("Your turn!");
                    }
                    break;

                case "COMBAT_END_TURN":
                    CombatEndTurnCommand combatEndTurnCommand = gson.fromJson(message, CombatEndTurnCommand.class);
                    if(combatEndTurnCommand.getCombatResult() != null){
                        UUID targetId = combatEndTurnCommand.getCombatResult().getTargetID();
                        combatRepo.takeDamage(targetId, combatEndTurnCommand.getCombatResult().getDamageRoll());
                        String damageLog = "";
                        if (combatEndTurnCommand.getCombatResult().getDamageLog() != null) {
                            damageLog = combatEndTurnCommand.getCombatResult().getDamageLog();
                        }
                        combatRepo.setLatestTurn("Turn " + combatEndTurnCommand.getTurnNumber() + "\n" + combatEndTurnCommand.getCombatResult().getHitLog() + "\n" + damageLog);
                        combatRepo.rotateCombatSequence();
                    }
                    break;

                case "COMBAT_END":
                    // TODO: Add combat end navigate back to the main menu and leave the room/server
                    // TODO: Add left hand attack
                    // TODO: Show the initial combat msg as a seperate overlay
                    CombatEndCommand combatEndCommand = gson.fromJson(message, CombatEndCommand.class);
                    if(combatEndCommand.getStatus() == CombatEndCommand.CombatEndStatus.WIN){
                        roomRepo.changeState(RoomState.State.DIALOGUE_PROCESSING);
                        Message.showMessage("You won the battle!");
                    }
                    else if (combatEndCommand.getStatus() == CombatEndCommand.CombatEndStatus.LOSE){
                        roomRepo.leaveRoom();
                        Message.showMessage("You have died! Returning back to main menu.");
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
//        this.setConnectTimeout(6000000); // 100 minutes
        this.setReadTimeout(600000); // 10 minute of idle time
        this.addHeader("Origin", "http://developer.example.com");
        this.enableAutomaticReconnection(5000);
        this.connect();
    }


    public String getRoomCode() {
        return roomCode;
    }
}