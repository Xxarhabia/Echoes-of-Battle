package com.xarhabia.model;

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
    }

    public void addXp(int amount) {
        this.xp += amount;
        if (xp >= level * 10) {
            xp = 0;
            level++;

            criticChance += 0.01;
            criticDamage += 0.05;

            System.out.println("Subiste a nivel " + level);
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
}
