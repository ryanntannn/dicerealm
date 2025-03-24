package com.dicerealm.core.combat.systems;

public class InitiativeResult {
    public int totalInitiative;
    private int initiativeRoll;
    private int initiativeModifier;
    private String initiativeLog;

    public InitiativeResult(int totalInitiative, int initiativeRoll, int initiativeModifier, String initiativeLog){
        this.totalInitiative = totalInitiative;
        this.initiativeRoll  = initiativeRoll;
        this.initiativeModifier = initiativeModifier;
        this.initiativeLog = initiativeLog;
    }

    public int getTotalInitiative(){ return totalInitiative; }
    public int getInitiativeRoll(){ return initiativeRoll; }
    public int getInitiativeModifier() { return initiativeModifier; }
    public String getInitiativeLog() { return initiativeLog; }

}
