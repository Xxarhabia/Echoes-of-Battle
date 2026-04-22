package com.xarhabia.config;

public class StatConfig {

    //Fuerza
    public static final float MAX_DAMAGE_BONUS = 10f;
    public static final float DAMAGE_PER_STR = 0.4f;

    public static final float MAX_AOE_RADIUS = 5.0f;
    public static final float AOE_RADIUS_PER_STR = 0.05f;

    public static final float MAX_AOE_DAMAGE_MULTI = 2.0f;
    public static final float AOE_DAMAGE_PER_STR = 0.02f;

    //Agilidad
    public static final double MAX_WALK_SPEED = 0.15;
    public static final double MAX_SPRINT_SPEED = 0.25;

    public static final double WALK_SPEED_PER_AGI = 0.002;
    public static final double SPRINT_SPEED_PER_AGI = 0.004;

    public static final int MAX_SPRINT_TIME = 200; // ticks (10s)
    public static final int SPRINT_TIME_PER_AGI = 2;

    // VITALIDAD
    public static final double MAX_HEALTH_BONUS = 40;
    public static final double HEALTH_PER_VIT = 1.0;

    //STAMINA
    public static final double STAMINA_DRAIN = 0.8;
    public static final double BASE_STAMINA_REGEN = 0.5;
    public static final double STAMINA_REGEN_PER_AGI = 0.02;
}
