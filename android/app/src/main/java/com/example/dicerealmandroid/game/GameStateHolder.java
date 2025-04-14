package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dialogue.SkillCheck;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
import com.example.dicerealmandroid.game.combat.CombatRepo;
import com.example.dicerealmandroid.game.combat.CombatSequence;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.player.PlayerRepo;
import com.example.dicerealmandroid.room.RoomRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameStateHolder extends ViewModel {

    private GameRepo gameRepo;
    public GameStateHolder() {
        gameRepo = new GameRepo();
    }

    public void startGame() {
        gameRepo.startGame();
    }


    public int[] getStatsIds() {
        return gameRepo.getStatsIds();
    }


    // Location
//    public LiveData<Location> subscribeCurrentLocation() {
//        return gameRepo.subscribeCurrentLocation();
//    }

    // Send text input to server
    public void sendTextInput(String text) {
        gameRepo.sendTextInput(text);
    }


    public LiveData<Location> getCurrentLocation(){
        return Transformations.map(gameRepo.getLocationGraph(), LocationGraph::getCurrentLocation);
    }

    public LiveData<List<Location>> getAdjacentLocations(){
        return Transformations.map(gameRepo.getLocationGraph(), locationGraph -> {
            List<Location> locations = new ArrayList<>();
            Location[] adjacentLocations = locationGraph.getAdjacentLocations();
            if (adjacentLocations == null) return locations;

            for (Location location : adjacentLocations) {
                if (location != null) {
                    locations.add(location);
                }
            }
            return locations;
        });
    }

}
