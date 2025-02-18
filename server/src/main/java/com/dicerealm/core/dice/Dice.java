package com.dicerealm.core.dice;

import com.dicerealm.core.strategy.RandomStrategy;

public class Dice {
	private int sides;
	private RandomStrategy randomStrategy = new DefaultRandomStrategy();

	public Dice(int sides) {
		this.sides = sides;
	}

	public int roll() {
		return (int) (randomStrategy.random() * sides) + 1;
	}

	public void setRandomStrategy(RandomStrategy randomStrategy) {
		this.randomStrategy = randomStrategy;
	}

	private class DefaultRandomStrategy implements RandomStrategy {
		@Override
		public double random() {
			return Math.random();
		}
	}
}
