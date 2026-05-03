package com.xarhabia.entities.val.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ValHealthPacket {

    public static final Identifier ID = new Identifier("combatmod", "val_health");

    public static void send(ServerPlayerEntity player, float health, float maxHealth, boolean isAngry) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(health);
        buf.writeFloat(maxHealth);
        buf.writeBoolean(isAngry);
        ServerPlayNetworking.send(player, ID, buf);
    }
}
