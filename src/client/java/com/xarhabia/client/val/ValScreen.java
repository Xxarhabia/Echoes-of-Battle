package com.xarhabia.client.val;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ValScreen extends Screen {

    private final int valEntityId;

    public ValScreen(int valEntityId) {
        super(Text.of("Val, la Diosa de la Misericordia"));
        this.valEntityId = valEntityId;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // boton de recuperar
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Recuperar"),
                button -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(valEntityId);
                    ClientPlayNetworking.send(
                            new Identifier("combatmod", "val_recover"), buf
                    );
                    this.close();
                })
                .dimensions(centerX - 105, centerY + 20, 100, 20)
                .build()

        );

        // Boton no recuperar
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("No recuperar"),
                button -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(valEntityId);
                    ClientPlayNetworking.send(
                            new Identifier("combatmod", "val_decline"), buf
                    );
                    this.close();
                })
                .dimensions(centerX + 5, centerY + 20, 100, 20)
                .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo semitransparente
        this.renderBackground(context);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Titulo
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Val, la Diosa de la Misericorida"),
                centerX, centerY - 30, 0xFFD700
        );

        // Mensaje
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Deseas recuperar tu inventario?"),
                centerX, centerY - 10, 0xFFFFFF
        );

        super.render(context, mouseX, mouseY, delta);
    }

    // No se puede cerrar con Escape - el jugador debe elegir
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
