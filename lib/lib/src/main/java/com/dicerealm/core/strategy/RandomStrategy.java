package com.dicerealm.core.strategy;

/**
 * Use this strategy to generate random numbers.
 */
public interface RandomStrategy {
	/**
	 * Generate a random double between 0 and 1.
	 * @return A random double between 0 and 1.
	 */
	public double random();
}
