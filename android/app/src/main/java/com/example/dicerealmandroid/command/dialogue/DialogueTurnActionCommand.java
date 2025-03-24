package com.example.dicerealmandroid.command.dialogue;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.core.entity.Entity;

import java.util.UUID;

public class DialogueTurnActionCommand extends Command {
    private int turnNumber;
    private UUID playerId;
    private String action;
    private Entity.StatsMap skillCheck;

    public DialogueTurnActionCommand(int turnNumber, UUID playerId, String action, Entity.StatsMap skillCheck) {
        super("DIALOGUE_TURN_ACTION");
        this.turnNumber = turnNumber;
        this.playerId = playerId;
        this.action = action;
        this.skillCheck = skillCheck;
    }

    public UUID getPlayerId() {
        return playerId;
    }
    public String getAction() {
        return action;
    }
    public Entity.StatsMap getSkillCheck() {
        return skillCheck;
    }
    public int getTurnNumber() {
        return turnNumber;
    }
}