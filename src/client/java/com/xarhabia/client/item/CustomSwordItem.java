package com.xarhabia.client.item;

import com.xarhabia.item.ModItems;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.stats.PlayerStatsManager;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CustomSwordItem {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if (stack.getItem() != ModItems.CUSTOM_SWORD) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            PlayerStats stats = PlayerStatsManager.getStats(client.player.getUuid());

            double bonus = stats.getAgility() * 0.4;

            double baseDamage = 0;

            var modifiers = stack.getAttributeModifiers(net.minecraft.entity.EquipmentSlot.MAINHAND)
                    .get(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            for (EntityAttributeModifier mod: modifiers) {
                baseDamage += mod.getValue();
            }

            double total = baseDamage + bonus;

            tooltip.removeIf(text -> text.getString().contains("Daño por golpe"));
            tooltip.add(Text.literal("Daño total: " + (int) total)
                    .formatted(Formatting.RED));
            tooltip.add(Text.literal("Base: " + (int) baseDamage)
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Bonus: " + (int) bonus)
                    .formatted(Formatting.GREEN));
        });
    }
}
