package com.xarhabia.client.gui;

import com.xarhabia.progression.UpgradeStatPacket;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.stats.PlayerStatsManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class StatsScreen extends Screen {

    public StatsScreen() {
        super(Text.literal("Stats"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("+ Agilidad"),
                button -> increaseAgility()
        ).dimensions(centerX - 50, 60, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("+ Fuerza"),
                button -> increaseStrength()
        ).dimensions(centerX - 50, 90, 100, 20).build());
    }

    private void increaseAgility() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("AGILITY");
        ClientPlayNetworking.send(UpgradeStatPacket.ID, buf);
    }

    private void increaseStrength() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("STRENGTH");
        ClientPlayNetworking.send(UpgradeStatPacket.ID, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        this.renderBackground(context);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            PlayerStats stats = PlayerStatsManager.getStats(client.player.getUuid());
            context.drawText(textRenderer, "STATS DEL JUGADOR", width / 2 - 50, 20, 0xFFFFFF, false);
            context.drawText(textRenderer, "Agilidad: " + stats.getAgility() , width / 2 - 50, 140, 0x00FF00, false);
            context.drawText(textRenderer, "Fuerza: " + stats.getStrength() , width / 2 - 50, 160, 0xFF5555, false);
            context.drawText(textRenderer, "Puntos disponibles: " + stats.getStatPoints() , width / 2 - 50, 180, 0x00FF00, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
