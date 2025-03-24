package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.DungeonMasterResponse;

public class ShowPlayerActionsCommand extends Command {
    public DungeonMasterResponse.PlayerAction[] actions;
    public ShowPlayerActionsCommand() {
        super("SHOW_PLAYER_ACTIONS");
    }

    public DungeonMasterResponse.PlayerAction[] getActions() {
        return actions;
    }
}

