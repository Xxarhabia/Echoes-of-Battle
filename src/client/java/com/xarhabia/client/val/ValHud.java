package com.xarhabia.client.val;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ValHud {

    public static void register() {
        HudRenderCallback.EVENT.register(ValHud::render);
    }

    private static void render(DrawContext context, float tickDelta) {
        if (!ValHealthData.visible) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Ocultar despues de 5 segundos sin cambios
        if (ValHealthData.hideTimer > 0) {
            ValHealthData.hideTimer--;
        } else {
            ValHealthData.visible = false;
            return;
        }

        int screenWidth = context.getScaledWindowWidth();
        int barWidth = 150;
        int barHeight = 10;
        int x = (screenWidth / 2) - (barWidth / 2);
        int y = 20;

        float ratio = ValHealthData.health / ValHealthData.maxHealth;
        int filledWidth = (int) (barWidth * ratio);

        // fondo de al barra
        context.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

        // Color segun estado: rojo si hostil, doradi si pacifica
        int barColor = ValHealthData.isAngry ? 0xFFCC2200 : 0xFFFFD700;
        context.fill(x, y, x + filledWidth, y + barHeight, barColor);

        // Texto
        String label = ValHealthData.isAngry ? "⚔ Val - Ira Divina" : "✦ Val";
        String healthText = (int) ValHealthData.health + " / " + (int) ValHealthData.maxHealth;

        context.drawTextWithShadow(client.textRenderer, Text.literal(label), x, y - 10, 0xFFFFD700);
        context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(healthText),
                x + barWidth - client.textRenderer.getWidth(healthText),
                y - 10,
                0xFFFFFFFF
        );
    }
}
