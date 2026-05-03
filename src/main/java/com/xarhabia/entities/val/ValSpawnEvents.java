package com.xarhabia.entities.val;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValSpawnEvents {

    public static void register() {
        registerValReset();
        registerInventoryRetention();
        registerDeathEvent();
        registerCommand();
    }

    // Map temporal para pasar el inventario de la muerte al spawn de Val
    private static final java.util.Map<java.util.UUID, List<ItemStack>> pendingInventories =
            new java.util.HashMap<>();

    private static void registerValReset() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            ServerWorld world = (ServerWorld) player.getWorld();

            // Diferir al siguiente tick para evitar ConcurrentModificationException
            world.getServer().execute(() -> {
                List<ValEntity> vals = world.getEntitiesByClass(
                        ValEntity.class,
                        player.getBoundingBox().expand(64),
                        val -> true
                );

                for (ValEntity val : vals) {
                    val.resetToPassive();
                }
            });

            return true;
        });
    }

    private static void registerInventoryRetention() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            // Capturar inventario antes de que minecraft lo dropee
            List<ItemStack> captured = captureInventory(player);
            pendingInventories.put(player.getUuid(), captured);

            // Limpiar el inventario para que Minecraft no dropee nada
            player.getInventory().clear();
            return true; // Permitir la muerte
        });
    }

    private static List<ItemStack> captureInventory(ServerPlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
            }
        }
        return items;
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
                    player.getBoundingBox().expand(200),
                    v -> true
            ).size() > 0;

            if (valAlreadyNearby) return;

            spawnVal(serverWorld, player);
        });
    }

    private static void spawnVal(ServerWorld world, ServerPlayerEntity player) {
        BlockPos spawnPos = resolveSpawnPos(player, world);

        ValEntity val = ValEntity.create(world);
        val.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                0f,
                0f
        );

        // Pasamos el inventario capturado antes de la muerte
        List<ItemStack> pending = pendingInventories.remove(player.getUuid());
        if (pending != null) {
            val.storeInventoryFromList(pending);
        }

        world.spawnEntity(val);
    }

    private static BlockPos resolveSpawnPos(ServerPlayerEntity player, ServerWorld currentWorld) {
        // Intentar punto de cama/anchor del jugador
        BlockPos bedPos = player.getSpawnPointPosition();
        RegistryKey<World> spawnDimension = player.getSpawnPointDimension();

        if (bedPos != null) {
            // Verificar que la dimension del spawn sea la misma donde murio
            if (spawnDimension.equals(currentWorld.getRegistryKey())) {
                // Verificar que la cama/anchor siga existiendo
                Optional<Vec3d> validSpawn = PlayerEntity.findRespawnPosition(
                        currentWorld, bedPos, player.getSpawnAngle(), false, true
                );
                if (validSpawn.isPresent()) {
                    return BlockPos.ofFloored(validSpawn.get());
                }
            }
        }

        // Fallback: spawn global del mundo
        return currentWorld.getSpawnPos();
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

