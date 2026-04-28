package com.xarhabia.progression;


import com.xarhabia.stats.StatConfig;
import com.xarhabia.stats.PlayerStatsManager;
import com.xarhabia.stats.PlayerStats;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public class ProgressionService {

    public static void addXp(PlayerEntity player, int amount) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());
        stats.addXp(amount);

        System.out.println("Subiste de nivel: " + stats.getXp());
    }

    public static float calculateDamage(PlayerEntity player, float baseDamage) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

       float strengthBonus = Math.min(
               stats.getStrength() * StatConfig.DAMAGE_PER_STR,
               StatConfig.MAX_DAMAGE_BONUS
       );

       boolean isCritic = Math.random() < stats.getCriticChance();
       float damage = baseDamage + strengthBonus;

       if (isCritic) {
           damage *= (float) stats.getCriticDamage();
           System.out.println("CRITICO!");

       }
       return damage;
    }

    public static void applyVitality(PlayerEntity player) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

        double extraHealth = Math.min(
                stats.getVitality() * StatConfig.HEALTH_PER_VIT,
                StatConfig.MAX_HEALTH_BONUS
        );

        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .setBaseValue(20 + extraHealth);

        player.setHealth(player.getMaxHealth());
        System.out.println("Vitalidad aumentada: " + stats.getVitality());
    }

    public static void applyAgility(PlayerEntity player) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

        double walkBonus = Math.min(
                stats.getAgility() * StatConfig.WALK_SPEED_PER_AGI,
                StatConfig.MAX_WALK_SPEED
        );

        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.1 + walkBonus);

        System.out.println("Agility aumentada: " + stats.getAgility());
    }
}
