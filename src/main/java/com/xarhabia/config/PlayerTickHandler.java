package com.xarhabia.config;

import com.xarhabia.manager.PlayerStatsManager;
import com.xarhabia.model.PlayerStats;
import com.xarhabia.service.ProgressionService;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerTickHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

                if (player.isSprinting()) {
                    // 1. si esta en cooldown
                    if (stats.getSprintCooldownTicks() > 0) {
                        stats.incrementSprintCooldown();

                        if (stats.getSprintCooldownTicks() >= stats.getMaxSprintCooldown()) {
                            System.out.println("Cooldown Terminado");
                            stats.resetSprintCooldown();
                        }

                        //velocidad normal
                        ProgressionService.applyAgility(player);
                        return;
                    }

                    // 2. sprint activo
                    stats.incrementSprintTicks();

                    int current = stats.getSprintTicks();
                    int max = stats.getMaxSprintTime();

                    if (current <= max) {
                        if (current % 10 == 0) {
                            System.out.println("Sprint " + current + " / " + max);
                        }
                        ProgressionService.applySprintBoost(player);
                    } else {
                        // 3. se agota
                        System.out.println("Sprint agotado -> cooldown");

                        stats.resetSprintTicks();
                        stats.incrementSprintCooldown(); //iniciamos el cooldown

                        ProgressionService.applyAgility(player);
                    }
                } else {
                    // reset total si deja de correr
                    stats.resetSprintTicks();
                    stats.resetSprintCooldown();

                    ProgressionService.applyAgility(player);
                }
            }
        });
    }
}
