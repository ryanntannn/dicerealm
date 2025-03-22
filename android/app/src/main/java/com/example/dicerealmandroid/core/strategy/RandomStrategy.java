package com.example.dicerealmandroid.core.strategy;

public interface RandomStrategy {
    /**
     * Generate a random double between 0 and 1.
     * @return A random double between 0 and 1.
     */
    public double random();
}