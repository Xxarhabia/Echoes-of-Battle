package com.xarhabia.client.gui;

import com.xarhabia.client.gui.hud.HintHud;
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
import net.minecraft.util.Identifier;

public class StatsScreen extends Screen {

    public StatsScreen() {
        super(Text.literal("Stats"));
    }

    @Override
    protected void init() {
        super.init();

        int leftX = width / 2 - 120;
        int baseY = 80;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("+ Agilidad"),
                button -> increaseAgility()
        ).dimensions(leftX, baseY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("+ Fuerza"),
                button -> increaseStrength()
        ).dimensions(leftX,  baseY + 30, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("+ Vitalidad"),
                button -> increaseVitality()
        ).dimensions(leftX,  baseY + 60, 100, 20).build());
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

    private void increaseVitality() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("VITALITY");
        ClientPlayNetworking.send(UpgradeStatPacket.ID, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        this.renderBackground(context);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            int size = 16;
            int x = 10;
            int y = 10;

            // icono escape
            context.drawTexture(
                    new Identifier("combatmod", "textures/gui/key_esc.png"),
                    x, y,
                    0, 0,
                    size, size,
                    size, size
            );

            // Texto salir
            context.drawText(
                    textRenderer,
                    "Salir",
                    x + size + 5,
                    y + 4,
                    0xFFFFFF,
                    true
            );

            int rightX = width / 2 + 20;
            int baseY = 80;

            PlayerStats stats = PlayerStatsManager.getStats(client.player.getUuid());
            context.drawText(textRenderer, "STATS DEL JUGADOR", width / 2 - 50, 20, 0xFFFFFF, false);
            context.drawText(textRenderer, "Agilidad: " + stats.getAgility() , rightX, baseY + 5, 0x00FF00, false);
            context.drawText(textRenderer, "Fuerza: " + stats.getStrength() , rightX, baseY + 35, 0x00FFFF, false);
            context.drawText(textRenderer, "Vitalidad: " + stats.getVitality() , rightX, baseY + 65, 0xFF5555, false);
            context.drawText(textRenderer, "Puntos disponibles: " + stats.getStatPoints() , rightX, baseY + 95, 0x00FF00, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
