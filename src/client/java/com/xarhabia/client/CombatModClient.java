package com.xarhabia.client;

import com.xarhabia.client.hud.StaminaHud;
import com.xarhabia.client.network.ClientStaminaData;
import com.xarhabia.network.SprintInputPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import com.xarhabia.network.StaminaSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class CombatModClient implements ClientModInitializer {

	private static boolean lastState = false;

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

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			boolean current = client.player.isSprinting();

			if (client.player.age % 20 == 0) {
				var vel = client.player.getVelocity();
				double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

				System.out.println(
						"CLIENT SPEED: " + speed +
								" | X: " + vel.x +
								" | Z: " + vel.z +
								" | SPRINTING: " + current
				);
			}

			if (current != lastState) {
				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeBoolean(current); //empaquetado
				ClientPlayNetworking.send(SprintInputPacket.ID, buf); //envio al servidor
				lastState = current;
			}
		});

		StaminaHud.register();
	}
}