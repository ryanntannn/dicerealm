package com.dicerealm.core.dice;

public class MultiDice extends Dice {
    private int numDice; // Number of dice to roll

    public MultiDice(int numDice, int sides) {
        super(sides);
        this.numDice = numDice;
    }

    public int getNumDice() {
        return numDice;
    }
    
    @Override
    public int roll() {
        int total = 0;
        for (int i = 0; i < numDice; i++) {
            total += super.roll(); // Roll each die
        }
        return total;
    }

    @Override
    public String toString() {
        return numDice + "d" + getSides();
    }
}

