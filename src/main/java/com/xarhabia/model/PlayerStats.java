package com.xarhabia.model;

import com.xarhabia.config.StatConfig;
import com.xarhabia.util.enums.SprintState;

public class PlayerStats {

    private int xp;
    private int level;

    private double criticChance;
    private double criticDamage;
    private double damageBonus;

    private double aoeRadius;
    private double aoeDamageMultiplayer;
    private int hitCounter;
    private int hitsForAoE;
    private double aoeBurstMultiplier;
    private long lastHitTime;
    private long comboTimeout;

    private int strength;
    private int vitality;
    private int agility;
    private int sprintTicks;
    private int sprintCooldownTicks;
    private double stamina;
    private double maxStamina;

    private SprintState sprintState;

    public PlayerStats() {
        this.xp = 0;
        this.level = 1;

        this.criticChance = 0.1;
        this.criticDamage = 1.5;
        this.damageBonus = 0.0;
        this.aoeRadius = 2.5;
        this.aoeDamageMultiplayer = 0.5;
        this.hitCounter = 0;
        this.hitsForAoE = 4;
        this.aoeBurstMultiplier = 1.5;
        this.lastHitTime = 0;
        this.comboTimeout = 3000;

        this.strength = 1;
        this.vitality = 1;
        this.agility = 1;
        this.sprintTicks = 0;
        this.sprintCooldownTicks = 0;
        this.sprintState = SprintState.IDLE;
        this.maxStamina = 100;
        this.stamina = maxStamina;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = Math.max(0, Math.min(stamina, getMaxStamina()));
    }

    public double getMaxStamina() {
        return 100 + (agility * 2);
    }

    public SprintState getSprintState() {
        return sprintState;
    }

    public void setSprintState(SprintState sprintState) {
        this.sprintState = sprintState;
    }

    public void incrementSprintCooldown() {
        this.sprintCooldownTicks++;
    }

    public int getSprintCooldownTicks() {
        return this.sprintCooldownTicks;
    }

    public int getMaxSprintCooldown() {
        return 100;
    }

    public void addXp(int amount) {
        this.xp += amount;

        while (xp >= level * 10) {
            xp -= level * 10;
            level++;

            strength++;
            vitality++;
            agility++;

            criticChance += 0.01;
            criticDamage += 0.05;

            System.out.println("Subiste de nivel " + level);
        }
    }

    public void registerHit() {
        long now = System.currentTimeMillis();

        if (now - lastHitTime > comboTimeout) {
            hitCounter = 0;
            System.out.println("Combo reiniciado por tiempo");
        }

        hitCounter++;
        lastHitTime = now;
    }
    public void incrementSprintTicks() {
        this.sprintTicks++;
    }

    public void resetSprintCooldown() {
        this.sprintCooldownTicks = 0;
    }
    public void resetSprintTicks() {
        this.sprintTicks = 0;
    }

    public int getSprintTicks() {
        return sprintTicks;
    }

    public int getMaxSprintTime() {
        return Math.min(
                agility * StatConfig.SPRINT_TIME_PER_AGI,
                StatConfig.MAX_SPRINT_TIME
        );
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public double getCriticDamage() {
        return criticDamage;
    }

    public double getCriticChance() {
        return criticChance;
    }

    public double getAoeRadius() {
        return aoeRadius;
    }

    public double getAoeDamageMultiplayer() {
        return aoeDamageMultiplayer;
    }

    public void incrementHitCounter() {
        this.hitCounter++;
    }

    public boolean shouldTriggerAoE() {
        return hitCounter >= hitsForAoE;
    }

    public void resetHitCounter() {
        this.hitCounter = 0;
    }

    public double getAoeBurstMultiplier() {
        return aoeBurstMultiplier;
    }

    public float getComboProgress() {
        return (float) hitCounter / hitsForAoE;
    }

    public int getStrength() {
        return strength;
    }

    public int getVitality() {
        return vitality;
    }

    public int getAgility() {
        return agility;
    }
}
