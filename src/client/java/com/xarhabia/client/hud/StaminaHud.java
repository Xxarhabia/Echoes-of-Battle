package com.xarhabia.client.hud;

import com.xarhabia.client.network.ClientStaminaData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class StaminaHud {

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

            int barWidth = 120;
            int barHeight = 10;

            int x = width / 2 - barWidth / 2;
            int y = height - 50;

            //fondo
            drawContext.fill(x, y, x + barWidth, y + barHeight, 0x88000000);

            //porcentaje
            double percent = stamina / max;
            int filled = (int) (percent * barWidth);

            //color dinamico
            int color;
            if (percent > 0.6) color = 0xFF00FF00;
            else if (percent > 0.3) color = 0xFFFFFF00;
            else color = 0xFFFF0000;

            //barra
            drawContext.fill(x, y, x + filled, y + barHeight, color);
        }));
    }
}
