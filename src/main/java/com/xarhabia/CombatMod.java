package com.xarhabia;

import com.xarhabia.combat.CombatEvents;
import com.xarhabia.config.ModEvents;
import com.xarhabia.entities.ModEntities;
import com.xarhabia.entities.val.ValEntity;
import com.xarhabia.entities.val.ValSpawnEvents;
import com.xarhabia.item.ModItems;
import com.xarhabia.progression.ProgressionService;
import com.xarhabia.progression.UpgradeStatPacket;
import com.xarhabia.stats.PlayerStatsManager;
import com.xarhabia.stats.PlayerStats;
import com.xarhabia.sprint.SprintInputPacket;
import com.xarhabia.stats.StatConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
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
		ModEntities.register();
		ModEntities.registerAttributes();
		ValSpawnEvents.register();
		registerServerPackets();

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

						switch (stat) {
							case "AGILITY" -> {
								if (stats.getAgility() >= StatConfig.MAX_AGILITY) return;
								if (!stats.spendPoint()) return;

								stats.setAgility(stats.getAgility() + 1);
								ProgressionService.applyAgility(player);
							}
							case "STRENGTH" -> {
								if (stats.getStrength() >= StatConfig.MAX_STRENGTH) return;
								if (!stats.spendPoint()) return;

								stats.setStrength(stats.getStrength() + 1);
								System.out.println("Fuerza aumentada");
							}
							case "VITALITY" -> {
								if (stats.getVitality() >= StatConfig.MAX_VITALITY) return;
								if (!stats.spendPoint()) return;

								stats.setVitality(stats.getVitality() + 1);
								ProgressionService.applyVitality(player);
							}
						}

					});
				}
		);

	}

	private static void registerServerPackets() {
		// Jugador eligio recuperar
		ServerPlayNetworking.registerGlobalReceiver(
				new Identifier("combatmod", "val_recover"),
				(server, player, handler, buf, responseSender) -> {
					int valEntityId = buf.readInt();
					server.execute(() -> {
						ValEntity val = (ValEntity) player.getWorld().getEntityById(valEntityId);
						if (val != null) {
							val.returnInventory(player);
							val.discard();
						}
					});
				}
		);

		ServerPlayNetworking.registerGlobalReceiver(
				new Identifier("combatmod", "val_decline"),
				(server, player, handler, buf, responseSender) -> {
					int valEntityId = buf.readInt();
					server.execute(() -> {
						ValEntity val = (ValEntity) player.getWorld().getEntityById(valEntityId);
						if (val != null) {
							val.discard();
						}
					});
				}
		);
	}
}