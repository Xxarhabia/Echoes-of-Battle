package com.xarhabia.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class CombatVisualService {

    public static void showComboProgress(PlayerEntity player, float progress) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        double x = player.getX();
        double y = player.getY() + 1;
        double z = player.getZ();

        int particleCount = Math.max(3, (int) (progress * 10));

        System.out.println("Mostrando partículas con progreso: " + progress);

        world.spawnParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                particleCount,
                0.5,0.5,0.5,
                0.1
        );
    }

    public static void showAoETrigger(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                player.getX(),
                player.getY(),
                player.getZ(),
                10,
                0.5,0.5,0.5,
                0.1
        );
    }
}
