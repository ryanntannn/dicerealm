package com.dicerealm.core.combat.systems;

import com.dicerealm.core.entity.Entity;

public class InitiativeResult {
    public int totalInitiative;
    private int initiativeRoll;
    private int initiativeModifier;
    private String initiativeLog;
    private Entity entity;
    public InitiativeResult(Entity entity, int totalInitiative, int initiativeRoll, int initiativeModifier, String initiativeLog){
        this.entity = entity;
        this.totalInitiative = totalInitiative;
        this.initiativeRoll  = initiativeRoll;
        this.initiativeModifier = initiativeModifier;
        this.initiativeLog = initiativeLog;
    }

    public Entity getEntity(){ return entity; }
    public int getTotalInitiative(){ return totalInitiative; }
    public int getInitiativeRoll(){ return initiativeRoll; }
    public int getInitiativeModifier() { return initiativeModifier; }
    public String getInitiativeLog() { return initiativeLog; }
    public InitiativeResult clone() {return new InitiativeResult(this.entity, this.totalInitiative,this.initiativeRoll,this.initiativeModifier,this.initiativeLog);}

}
