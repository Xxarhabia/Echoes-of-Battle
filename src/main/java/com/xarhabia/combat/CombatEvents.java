package com.xarhabia.combat;

import com.xarhabia.stats.PlayerStatsManager;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.progression.XpService;
import com.xarhabia.progression.ProgressionService;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public class CombatEvents {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResutl) -> {
            if (!world.isClient && entity instanceof LivingEntity target) {
                PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

                float baseDamage = 2.0f;
                float finalDamage = ProgressionService.calculateDamage(player, baseDamage);

                //Daño normal
                target.damage(target.getDamageSources().playerAttack(player), finalDamage);

                //contar golpes
                stats.registerHit();

                //progreso visual
                float progress = stats.getComboProgress();
                CombatVisualService.showComboProgress(player, progress);

                //casi lleno
                if (progress >= 0.8f && progress < 1.0f) {
                    CombatVisualService.showAoETrigger(player);
                }

                //validar si activamos el aoe
                if (stats.shouldTriggerAoE()) {
                    float aoeDamage = finalDamage * (float) stats.getAoeBurstMultiplier();
                    System.out.println("💥 AOE ACTIVADO 💥");
                    AoECombatService.applyAoE(player, target, aoeDamage);
                    stats.resetHitCounter();
                }

                ProgressionService.addXp(player, 1);

                if(target.isDead() || target.getHealth() <= 0) {
                    int xp = XpService.getXpForKill(target);
                    ProgressionService.addXp(player, xp);

                    System.out.println("Mob Eliminado: " + target.getName().getString());
                    System.out.println("XP por kill: " + xp);
                }
            }
            return ActionResult.PASS;
        });
    }
}
