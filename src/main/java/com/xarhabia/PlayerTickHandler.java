package com.xarhabia;

import com.xarhabia.stamina.StaminaHandler;
import com.xarhabia.stats.PlayerStatsManager;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.stamina.StaminaSyncPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerTickHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator()) continue;
                PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

                StaminaHandler.handleStamina(player, stats);

                //Sincronizacion stamina -> cliente
                if (player.age % 5 == 0) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(stats.getStamina());
                    buf.writeDouble(stats.getMaxStamina());

                    ServerPlayNetworking.send(player, StaminaSyncPacket.ID, buf);
                }
            }
        });
    }
}
