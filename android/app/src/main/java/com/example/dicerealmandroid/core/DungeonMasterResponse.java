package com.example.dicerealmandroid.core;

public class DungeonMasterResponse{

    public String displayText;
    public PlayerAction[] actionChoices;
    public String locationId;
    public String contextSummary;

    public class PlayerAction{
        public String action;
        public String playerId;
        public SkillCheck skillCheck;
    }

    public class SkillCheck {
        public int STRENGTH;
        public int DEXTERITY;
        public int CONSTITUTION;
        public int INTELLIGENCE;
        public int WISDOM;
        public int CHARISMA;
    }
}
