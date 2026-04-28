package com.xarhabia.client;

import com.xarhabia.client.gui.StatsScreen;
import com.xarhabia.client.gui.hud.HintHud;
import com.xarhabia.client.item.CustomSwordItem;
import com.xarhabia.client.stamina.StaminaHud;
import com.xarhabia.client.stamina.ClientStaminaData;
import com.xarhabia.sprint.SprintInputPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import com.xarhabia.stamina.StaminaSyncPacket;
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

			if (current != lastState) {
				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeBoolean(current); //empaquetado
				ClientPlayNetworking.send(SprintInputPacket.ID, buf); //envio al servidor
				lastState = current;
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			if (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_K) == GLFW.GLFW_PRESS) {
				client.setScreen(new StatsScreen()

				);
			}
		});

		CustomSwordItem.register();
		StaminaHud.register();
		HintHud.drawStatsHint();
	}
}