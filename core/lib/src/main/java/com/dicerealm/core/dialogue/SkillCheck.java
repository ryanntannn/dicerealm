package com.dicerealm.core.dialogue;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.AbilityModifierCalculator;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.strategy.RandomStrategy;

public class SkillCheck {
	public static enum Result {
		SUCCESS, FAILURE, CRITICAL_SUCCESS, CRITICAL_FAILURE
	}

	public static String getResultsText(Result result) {
		switch (result) {
			case SUCCESS:
				return "Success";
			case FAILURE:
				return "Failure";
			case CRITICAL_SUCCESS:
				return "Critical Success";
			case CRITICAL_FAILURE:
				return "Critical Failure";
			default:
				return "Unknown";
		}
	}

	public static class RollResultDetail {
		private Result result;
		private int roll;
		private int target;
		private int modifier;
		private Stat stat;

		public RollResultDetail(Result result, int roll, int target, int modifier, Stat stat) {
			this.result = result;
			this.roll = roll;
			this.target = target;
			this.modifier = modifier;
			this.stat = stat;
		}

		public Result getResult() {
			return result;
		}

		public int getRoll() {
			return roll;
		}

		public int getTarget() {
			return target;
		}

		public int getModifier() {
			return modifier;
		}

		public Stat getStat() {
			return stat;
		}

		@Override
		public String toString() {
			return StatsMap.getStatText(stat) + " check: Rolled " + roll + " + " + modifier
					+ "(modifier) vs " + target + " - " + getResultsText(result);
		}
	}

	public static class ActionResultDetail {
		private RollResultDetail[] rollResultDetails;
		private String action;

		public ActionResultDetail() {
			this.action = "No action";
			this.rollResultDetails = new RollResultDetail[0];
		}

		public ActionResultDetail(String action, RollResultDetail[] rollResultDetails) {
			this.action = action;
			this.rollResultDetails = rollResultDetails;
		}

		public String getAction() {
			return action;
		}

		public RollResultDetail[] getRollResultDetails() {
			return rollResultDetails;
		}

		public Result getResult() {
			boolean success = true;
			for (RollResultDetail rollResultDetail : rollResultDetails) {
				if (rollResultDetail.getResult() == Result.FAILURE
						|| rollResultDetail.getResult() == Result.CRITICAL_FAILURE) {
					success = false;
					break;
				}
			}
			return success ? Result.SUCCESS : Result.FAILURE;
		}

		@Override
		public String toString() {
			if (rollResultDetails.length == 0) {
				return action + ": No skill checks";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(action + ":\n With skill checks: \n");
			for (RollResultDetail rollResultDetail : rollResultDetails) {
				sb.append(rollResultDetail.toString() + "\n");
			}
			sb.append("Overall Result: " + getResultsText(getResult()));
			return sb.toString();
		}
	}

	private D20 d20 = new D20();

	public SkillCheck(RandomStrategy randomStrategy) {
		d20.setRandomStrategy(randomStrategy);
	}

	public ActionResultDetail check(DialogueTurnAction action, StatsMap playerStats) {
		StatsMap filteredSkillCheck = new StatsMap();

		for (Stat key : action.getSkillCheck().keySet()) {
			if (action.getSkillCheck().get(key) != 0) {
				filteredSkillCheck.put(key, action.getSkillCheck().get(key));
			}
		}

		if (filteredSkillCheck.isEmpty()) {
			return new ActionResultDetail(action.getAction(), new RollResultDetail[0]);
		}

		RollResultDetail[] results = new RollResultDetail[filteredSkillCheck.size()];


		int i = 0;
		for (Stat key : filteredSkillCheck.keySet()) {
			int roll = d20.roll();
			int target = filteredSkillCheck.get(key);
			int modifier = AbilityModifierCalculator.getAbilityModifier(playerStats, key);
			Result result = Result.FAILURE;
			if (roll >= 20) {
				result = Result.CRITICAL_SUCCESS;
			} else if (roll <= 1) {
				result = Result.CRITICAL_FAILURE;
			} else if (roll + modifier >= target) {
				result = Result.SUCCESS;
			}
			results[i] = new RollResultDetail(result, roll, target, modifier, key);
			i++;
		}

		return new ActionResultDetail(action.getAction(), results);
	}
}
