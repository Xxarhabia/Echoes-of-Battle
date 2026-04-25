package com.xarhabia.client.stamina;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class StaminaHud {

    private static final Identifier BG =
            new Identifier("combatmod", "textures/gui/stamina_bar_bg.png");

    private static final Identifier FILL =
            new Identifier("combatmod", "textures/gui/stamina_bar_fill.png");

    public static void register() {
        HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null) return;

            double stamina = ClientStaminaData.stamina;
            double max = ClientStaminaData.maxStamina;

            //ocultar si esta llena
            if (stamina >= max) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            int barWidth = 100;
            int barHeight = 10;

            int x = width / 2 - barWidth / 2;
            int y = height - 50;

            //porcentaje
            double percent = stamina / max;
            int filled = (int) (percent * barWidth);

            // Fondo (textura
            drawContext.drawTexture(
                    BG,
                    x, y,
                    0, 0,
                    barWidth, barHeight,
                    barWidth, barHeight
            );

            // barra (Textura recortada)
            if (filled > 0) {
                drawContext.drawTexture(
                        FILL,
                        x, y,
                        0, 0,
                        filled, barHeight,
                        barWidth, barHeight
                );
            }
        }));
    }
}
