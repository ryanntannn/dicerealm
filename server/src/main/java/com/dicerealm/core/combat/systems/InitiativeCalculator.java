package com.dicerealm.core.combat.systems;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.AbilityModifierCalculator;
import com.dicerealm.core.entity.Entity;

public class InitiativeCalculator {

    private D20 d20 = new D20();

    // Default Constructor
    public InitiativeCalculator() {}

    // Custom D20 For Testing
    public InitiativeCalculator(D20 d20) { this.d20 = d20; }

    public InitiativeResult rollInitiative(Entity entity){
        int initiativeRoll = d20.roll();
        int initiativeModifier = AbilityModifierCalculator.getDexterityModifier(entity.getStats());
        int totalInitiative = initiativeRoll + initiativeModifier;
        String initiativeLog = String.format("%s rolls a d20: %d + Dex Modifier (%d) = %d Total Initiative! ",
                entity.getDisplayName(), initiativeRoll, initiativeModifier, totalInitiative);
        return new InitiativeResult(totalInitiative, initiativeRoll, initiativeModifier, initiativeLog);

    }

}