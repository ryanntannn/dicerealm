package com.example.dicerealmandroid.game.combat;

public class CombatTurnModal {

    private String message;
    private String hitLog;
    private int turn;

    public CombatTurnModal(String message, String hitLog, int turn) {
        this.message = message;
        this.hitLog = hitLog;
        this.turn = turn;
    }

    public String getMessage() {
        return message;
    }

    public int getTurn() {
        return turn;
    }

    public String getHitLog() {
        return hitLog;
    }

}
