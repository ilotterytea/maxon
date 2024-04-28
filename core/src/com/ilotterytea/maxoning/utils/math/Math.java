package com.ilotterytea.maxoning.utils.math;

public class Math {
    /**
     * Get random number from min value to max value
     * @param min Minimal value
     * @param max Maximum value
     * @return Random number between minimal and maximum values
     */
    public static int getRandomNumber(int min, int max) {
        return (int) ((java.lang.Math.random() * (max - min)) + min);
    }

    public static float percentFromValue(float percentage, int value) {
        return percentage / 100f * value;
    }
}
