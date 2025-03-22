package com.example.dicerealmandroid.core.dialogue;

import com.example.dicerealmandroid.core.entity.Entity;

public class DialogueTurnAction {
    private String action;
    private Entity.StatsMap skillCheck;

    public DialogueTurnAction(String action, Entity.StatsMap skillCheck) {
        this.action = action;
        this.skillCheck = skillCheck;
    }

    /**
     * A string representing the action to be taken
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * A map of stats representing the skill check for the action.
     * This should be used during the resolution of the action.
     * @return
     */
    public Entity.StatsMap getSkillCheck() {
        return skillCheck;
    }
}