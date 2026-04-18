package com.xarhabia.service;

import com.xarhabia.config.StatConfig;
import com.xarhabia.manager.PlayerStatsManager;
import com.xarhabia.model.PlayerStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class AoECombatService {
    public static void applyAoE(PlayerEntity player, LivingEntity mainTarget, float baseDamage) {
        PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

        double radius = Math.min(
                stats.getAoeRadius() + stats.getStrength() * StatConfig.AOE_RADIUS_PER_STR,
                StatConfig.MAX_AOE_RADIUS
        );
        float aoeDamage = baseDamage * (float) Math.min(
                stats.getAoeDamageMultiplayer() + stats.getStrength() * StatConfig.AOE_DAMAGE_PER_STR,
                StatConfig.MAX_AOE_DAMAGE_MULTI
        );

        World world = player.getWorld();

        Box area = mainTarget.getBoundingBox().expand(radius);

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity != player && entity != mainTarget && entity.isAlive()
        );

        for (LivingEntity target : targets) {
            target.damage(
                    target.getDamageSources().playerAttack(player),
                    aoeDamage
            );
            System.out.println("AOE hit a: " + target.getName().getString());
        }
    }
}
