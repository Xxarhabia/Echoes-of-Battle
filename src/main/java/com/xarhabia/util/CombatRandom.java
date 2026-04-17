package com.xarhabia.util;

import java.util.Random;

public class CombatRandom {

    private static final Random random = new Random();

    public static boolean roll(double chance) {
        return random.nextDouble() < chance;
    }
}
