package com.xarhabia.handlers;

import com.xarhabia.config.StatConfig;
import com.xarhabia.model.PlayerStats;
import com.xarhabia.service.ProgressionService;
import com.xarhabia.util.enums.SprintState;
import net.minecraft.server.network.ServerPlayerEntity;

public class SprintStateHandler {

    public static void handle(ServerPlayerEntity player, PlayerStats stats) {
        if (stats.getStamina() <= 0) {
            player.setSprinting(false);
        }

        switch (stats.getSprintState()) {
            case IDLE -> handleIdle(player, stats);
            case SPRINTING -> handleSprinting(player, stats);
            case EXHAUSTED -> handleExhausted(player, stats);
            case COOLDOWN -> handleCooldown(player, stats);
        }
    }

    private static void handleIdle(ServerPlayerEntity player, PlayerStats stats) {

        double regen = StatConfig.BASE_STAMINA_REGEN +
                (stats.getAgility() * StatConfig.STAMINA_REGEN_PER_AGI);

        stats.setStamina(stats.getStamina() + regen);

        ProgressionService.applyAgility(player);

        if (stats.getStamina() <= 1) {
            player.setSprinting(false);
            return;
        }

        if (player.isSprinting()) {
            stats.setSprintState(SprintState.SPRINTING);
        }
    }

    private static void handleSprinting(ServerPlayerEntity player, PlayerStats stats) {

        // si deja de sprintar manualmente
        if (!player.isSprinting()) {
            stats.setSprintState(SprintState.IDLE);
            return;
        }

        stats.setStamina(stats.getStamina() - StatConfig.STAMINA_DRAIN);

        if (stats.getStamina() > 0) {
            ProgressionService.applySprintBoost(player);
        } else {
            stats.setStamina(0);
            player.setSprinting(false); //BLOQUEAMOS SPRINT
            stats.setSprintState(SprintState.EXHAUSTED);
        }
    }

    private static void handleExhausted(ServerPlayerEntity player, PlayerStats stats) {

        //forzar detener sprint
        player.setSprinting(false);

        double regen = StatConfig.BASE_STAMINA_REGEN +
                (stats.getAgility() * StatConfig.STAMINA_REGEN_PER_AGI);

        stats.setStamina(stats.getStamina() + regen);

        ProgressionService.applyAgility(player);

        // 🔥 IMPORTANTE: no salir demasiado pronto
        if (stats.getStamina() > stats.getMaxStamina() * 0.3) {
            stats.setSprintState(SprintState.IDLE);
        }
    }

    private static void handleCooldown(ServerPlayerEntity player, PlayerStats stats) {
        player.setSprinting(false);
        stats.incrementSprintTicks();
        ProgressionService.applyAgility(player);
        if (stats.getSprintCooldownTicks() >= stats.getMaxSprintCooldown()) {
            stats.resetSprintTicks();
            stats.setSprintState(SprintState.IDLE);
        }
    }
}
