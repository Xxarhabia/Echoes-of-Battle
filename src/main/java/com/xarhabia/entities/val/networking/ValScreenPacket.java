package com.xarhabia.entities.val.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ValScreenPacket {

    public static final Identifier ID = new Identifier("combatmod", "open_val_screen");

    // Servidor -> Cliente: le dice al cliente que abra la pantalla de Val
    public static void send(ServerPlayerEntity player, int valEntityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(valEntityId);
        ServerPlayNetworking.send(player, ID, buf);
    }
}
