package com.xarhabia.service;


import com.xarhabia.manager.PlayerStatsManager;
import com.xarhabia.model.PlayerStats;
import com.xarhabia.util.CombatRandom;
import net.minecraft.entity.player.PlayerEntity;

public class ProgressionService {

    public static void addXp(PlayerEntity player, int amount) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());
        stats.addXp(amount);

        System.out.println("XP added: " + stats.getXp());
    }

    public static float calculateDamage(PlayerEntity player, float baseDamage) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

        float damage = baseDamage;

        if (CombatRandom.roll(stats.getCriticChance())) {
            damage *= stats.getCriticChance();
            System.out.println("CRITICO!");
        }

        damage += stats.getLevel() * 0.5f;

        return damage;
    }
}
