package com.xarhabia.stats;

public class PlayerStats {

    private int xp;
    private int level;
    private int statPoints;

    private double criticChance;
    private double criticDamage;

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
    private double stamina;
    private double maxStamina;
    private boolean wantsToSprint;

    public PlayerStats() {
        this.xp = 0;
        this.level = 1;
        this.statPoints = 0;

        this.criticChance = 0.1;
        this.criticDamage = 1.5;
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
        this.maxStamina = 100;
        this.stamina = maxStamina;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void addStatPoints(int points) {
        this.statPoints += points;
    }

    public boolean spendPoint() {
        if (statPoints > 0) {
            statPoints--;
            return true;
        }
        return false;
    }

    public boolean isWantsToSprint() {
        return wantsToSprint;
    }

    public void setWantsToSprint(boolean wantsToSprint) {
        this.wantsToSprint = wantsToSprint;
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

    public int getRequiredXp() {
        return 10 + (level * level * 8);
    }

    public void addXp(int amount) {
        this.xp += amount;

        while (xp >= getRequiredXp()) {
            xp -= level * 10;
            level++;

            addStatPoints(2);

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
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
    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getVitality() {
        return vitality;
    }

   public void setVitality(int vitality) {
        this.vitality = vitality;
   }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }
}
