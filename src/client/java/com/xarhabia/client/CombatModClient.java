package com.xarhabia.client;

import com.xarhabia.client.hud.StaminaHud;
import com.xarhabia.client.network.ClientStaminaData;
import com.xarhabia.network.StaminaSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CombatModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				StaminaSyncPacket.ID,
				(client, handler, buf, responseSender) -> {
					double stamina = buf.readDouble();
					double max = buf.readDouble();

					client.execute(() -> {
						ClientStaminaData.stamina = stamina;
						ClientStaminaData.maxStamina = max;
					});
				}
		);

		StaminaHud.register();
	}
}