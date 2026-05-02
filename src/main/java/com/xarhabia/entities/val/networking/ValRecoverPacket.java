package com.xarhabia.entities.val.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ValRecoverPacket {

    public static final Identifier RECOVER_ID = new Identifier("combatmod", "val_recover");
    public static final Identifier DECLINE_ID = new Identifier("combatmod", "val_decline");

    public static void sendRecover(int valEntityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(valEntityId);
        // Se envia desde el cliente, ver ValScreen
    }
}
