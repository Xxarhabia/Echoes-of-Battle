package com.xarhabia;

import com.xarhabia.config.PlayerTickHandler;
import com.xarhabia.event.CombatEvents;
import com.xarhabia.manager.PlayerStatsManager;
import com.xarhabia.model.PlayerStats;
import com.xarhabia.network.SprintInputPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatMod implements ModInitializer {
	public static final String MOD_ID = "combatmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		CombatEvents.register();
		PlayerTickHandler.register();

		// Recibimos llamadas del
		ServerPlayNetworking.registerGlobalReceiver(
				SprintInputPacket.ID,
				(server, player, handler, buf, responseSender) -> {
					boolean wantsToSprint = buf.readBoolean(); //Leemos el contenido empaquetado del cliente

					server.execute(() -> {
						PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());
						stats.setWantsToSprint(wantsToSprint); //Lo implementamos
					});
				}
		);
	}
}