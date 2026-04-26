package com.xarhabia.client.item;

import com.xarhabia.item.ModItems;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.stats.PlayerStatsManager;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
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

            tooltip.add(Text.literal(" "));
            tooltip.add(Text.literal("Bonus por agilidad: +" + (int) bonus)
                    .formatted(Formatting.GREEN));
        });
    }
}
