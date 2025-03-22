package com.example.dicerealmandroid.command.dialogue;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.core.dialogue.DialogueTurn;

public class StartTurnCommand extends Command {
    private DialogueTurn dialogueTurn;
    public StartTurnCommand() {
        super("DIALOGUE_START_TURN");
    }
    public DialogueTurn getDialogueTurn() {
        return dialogueTurn;
    }
}

