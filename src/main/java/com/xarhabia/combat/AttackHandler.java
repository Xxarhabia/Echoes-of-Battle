package com.xarhabia.combat;

import com.xarhabia.item.ModItems;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.stats.PlayerStatsManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class AttackHandler {
    public static void register() {

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            if (world.isClient) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;
            if (player.getStackInHand(hand).getItem() != ModItems.CUSTOM_SWORD) {
                return ActionResult.PASS;
            }

            PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

            double bonusDamage = stats.getAgility() * 0.4;

            entity.damage(
                    world.getDamageSources().playerAttack(player),
                    (float) bonusDamage
            );
            return ActionResult.PASS;
        });
    }
}
