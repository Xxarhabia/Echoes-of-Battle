package com.xarhabia.client.gui.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HintHud {

    public static final Identifier KEY_HINT =
            new Identifier("combatmod", "textures/gui/key_k.png");

    public static void drawStatsHint() {
        HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int height = client.getWindow().getScaledHeight();

            int size = 16;

            int x = 10;
            int y = height - 30;

            drawContext.drawTexture(
                    KEY_HINT,
                    x, y,
                    0, 0,
                    size, size,
                    size, size
            );

            String text = "Ver stats";
            int textX = x + size + 5;
            int textY = y + 4;

            drawContext.drawText(
                    client.textRenderer,
                    text,
                    textX,
                    textY,
                    0xFFFFFF,
                    true
            );
        }));
    }
}
