package com.xarhabia.stamina;

import com.xarhabia.stats.StatConfig;
import com.xarhabia.stats.PlayerStats;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class StaminaHandler {

    public static void handleStamina(ServerPlayerEntity player, PlayerStats stats) {
        boolean wants = stats.isWantsToSprint();
        double currentStamina = stats.getStamina();

        double regen =  StatConfig.BASE_STAMINA_REGEN +
                (stats.getAgility() * StatConfig.STAMINA_REGEN_PER_AGI);

        boolean hasStamina = currentStamina > 1; //Tendra stamina solo si la stamina actual es mayor a 1%

        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        double baseSpeed = 0.1 + (stats.getAgility() * StatConfig.WALK_SPEED_PER_AGI);

        if (wants) {
            player.setSprinting(true);

            if (hasStamina) {
                attr.setBaseValue(baseSpeed);
                stats.setStamina(currentStamina - StatConfig.STAMINA_DRAIN);
            } else {
                attr.setBaseValue(baseSpeed * 0.5);
            }
        } else {
            player.setSprinting(false);
            attr.setBaseValue(baseSpeed);
            stats.setStamina(currentStamina + regen);
        }

        if (stats.getStamina() < 0) stats.setStamina(0);
        if (stats.getStamina() > stats.getMaxStamina()) {
            stats.setStamina(stats.getMaxStamina());
        }
    }
}
