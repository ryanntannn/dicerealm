package com.example.dicerealmandroid.command.dialogue;

import com.example.dicerealmandroid.command.Command;

public class StartTurnCommand extends Command {
    private DialogueTurn dialogueTurn;
    public StartTurnCommand() {
        super("DIALOGUE_START_TURN");
    }
    public DialogueTurn getDialogueTurn() {
        return dialogueTurn;
    }
}

