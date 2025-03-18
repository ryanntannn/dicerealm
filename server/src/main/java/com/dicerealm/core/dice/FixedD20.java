package com.dicerealm.core.dice;

public class FixedD20 extends D20 {
    private final int fixedRoll;

    public FixedD20(int fixedRoll) {
        this.fixedRoll = fixedRoll;
    }

    @Override
    public int roll() {
        return fixedRoll;
    }
}