package com.dicerealm.core.entity;
public class AbilityModifierCalculator {

    // Calculates the modifier for a given ability score using the DND 5E formula: (Ability Score - 10) / 2
    public static int getAbilityModifier(StatsMap stats, Stat abilityStat) {
        int abilityScore = stats.get(abilityStat);
        return Math.floorDiv(abilityScore - 10, 2);
    }

    // Helper methods to calculate ability modifier for each specific ability stat
    public static int getStrengthModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.STRENGTH);
    }

    public static int getDexterityModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.DEXTERITY);
    }

    public static int getConstitutionModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.CONSTITUTION);
    }

    public static int getIntelligenceModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.INTELLIGENCE);
    }

    public static int getWisdomModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.WISDOM);
    }

    public static int getCharismaModifier(StatsMap stats) {
        return getAbilityModifier(stats, Stat.CHARISMA);
    }

}
