package com.xarhabia.entities.val;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ValSpawnEvents {

    public static void register() {
        registerDeathEvent();
        registerCommand();
    }

    private static void registerDeathEvent() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {

            // Solo nos interesa cuando muere el jugador
            if (!(entity instanceof ServerPlayerEntity player)) return;

            World world = player.getWorld();

            // Validacion: solo en servidor (nunca en cliente)
            if (world.isClient()) return;

            ServerWorld serverWorld = (ServerWorld) world;

            // Evitar multipes instancias: buscar si ya existe una val cerca
            boolean valAlreadyNearby = serverWorld.getEntitiesByClass(
                    ValEntity.class,
                    player.getBoundingBox().expand(20),
                    v -> true
            ).size() > 0;

            if (valAlreadyNearby) return;

            spawnVal(serverWorld, player);
        });
    }

    private static void spawnVal(ServerWorld world, PlayerEntity player) {
        ValEntity val = ValEntity.create(world);

        // Spawnear 2 bloques al frente del jugador, en su misma posicion de Y
        double offSetX = -Math.sin(Math.toRadians(player.getYaw())) * 2;
        double offSetZ = -Math.cos(Math.toRadians(player.getYaw())) * 2;

        val.refreshPositionAndAngles(
                player.getX() + offSetX,
                player.getY(),
                player.getZ() + offSetZ,
                player.getYaw(),
                0f
        );

        world.spawnEntity(val);
    }

    // ------ Comando: /spawnval -------
    private static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("valspawn")
                            .requires(source -> source.hasPermissionLevel(2)) //solo ops
                            .executes(ValSpawnEvents::executeSpawnVal)
            );
        });
    }

    private static int executeSpawnVal(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!(source.getWorld() instanceof ServerWorld serverWorld)) {
            source.sendError(Text.literal("Este comando solo puede usarse en el servidor."));
            return 0;
        }

        ServerPlayerEntity player;
        try {
            player = source.getPlayerOrThrow();
        } catch (Exception e) {
            source.sendError(Text.literal("Error" + e.getMessage()));
            e.printStackTrace(); //Imprimimos en consola
            return 0;
        }

        try {
            spawnVal(serverWorld, player);
            source.sendFeedback(() -> Text.literal("Val ha sido convocada."), false);
        } catch (Exception e) {
            source.sendError(Text.literal("Error al spawnear: " + e.getMessage()));
            return 0;
        }

        return 1;
    }
}

