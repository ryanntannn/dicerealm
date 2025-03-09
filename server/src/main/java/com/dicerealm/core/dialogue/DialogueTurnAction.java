package com.dicerealm.core.dialogue;

import com.dicerealm.core.entity.StatsMap;

/**
 * Represents an action that can be taken during a dialogue turn
 */
public class DialogueTurnAction {
	private String action;
	private StatsMap skillCheck;

	public DialogueTurnAction(String action, StatsMap skillCheck) {
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
	public StatsMap getSkillCheck() {
		return skillCheck;
	}
}
