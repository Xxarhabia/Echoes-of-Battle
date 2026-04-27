package com.xarhabia;

import com.xarhabia.combat.CombatEvents;
import com.xarhabia.config.ModEvents;
import com.xarhabia.item.ModItems;
import com.xarhabia.progression.ProgressionService;
import com.xarhabia.progression.UpgradeStatPacket;
import com.xarhabia.stats.PlayerStatsManager;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.sprint.SprintInputPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatMod implements ModInitializer {
	public static final String MOD_ID = "combatmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.register();
		ModEvents.register();
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

		ServerPlayNetworking.registerGlobalReceiver(
				UpgradeStatPacket.ID,
				(server, player, handler, buf, responseSender) -> {
					String stat = buf.readString();
					server.execute(() -> {
						PlayerStats stats = PlayerStatsManager.getStats(player.getUuid());

						if (!stats.spendPoint()) return;

						switch (stat) {
							case "AGILITY" -> {
								stats.setAgility(stats.getAgility() + 1);
								ProgressionService.applyAgility(player);
								System.out.println("Agilidad aumentada");
							}
							case "STRENGTH" -> {
								stats.setStrength(stats.getStrength() + 1);
								System.out.println("Fuerza aumentada");
							}
						}
					});
				}
		);

	}
}