package com.example.dicerealmandroid.game.dialog;

import java.util.Optional;
import java.util.UUID;

public class Dialog {
    private String message;
    private Optional<UUID> sender;
    private int turnNumber;

    public Dialog(String message, UUID sender, int turnNumber) {
        this.message = message;
        this.sender = Optional.ofNullable(sender);
        this.turnNumber = turnNumber;
    }

    public String getMessage() {
        return message;
    }

    public Optional<UUID> getSender() {
        return sender;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setSender(Optional<UUID> sender) {
        this.sender = sender;
    }
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }
}
