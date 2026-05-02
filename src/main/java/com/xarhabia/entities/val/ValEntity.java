package com.xarhabia.entities.val;

import com.xarhabia.entities.ModEntities;
import com.xarhabia.entities.val.networking.ValScreenPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ValEntity extends MobEntity {

    private static final int DESPAWN_TICKS = 5 * 60 * 20;

    private final List<ItemStack> storedInventory = new ArrayList<>();
    private int ticksAlive = 0;
    private boolean invetoryClaimed = false;

    public ValEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        // Val no puede ser empujada ni recibe knockback intencional
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void initGoals() {
        // Mira al jugador mas cercano en un radio de 8 bloques
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        // Si nadie esta cerca, mira al frente aleatoriamente
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    // --- Guardar inventario del jugador --------
    public void storeInventory(PlayerInventory inventory) {
        storedInventory.clear();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                storedInventory.add(stack.copy());
            }
        }
    }

    // --- Devolver inventario al jugador --------
    public void returnInventory(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack stack : storedInventory) {
            // Intentar agregar al inventario, si no cabe, tropear al suelo
            if (!inventory.insertStack(stack)) {
                player.dropItem(stack, false);
            }
        }
        storedInventory.clear();
        invetoryClaimed = false;
    }

    public void storeInventoryFromList(List<ItemStack> items) {
        storedInventory.clear();
        storedInventory.addAll(items);
    }

    // --- Click derecho: abrir pantalla --------
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (!storedInventory.isEmpty() && !invetoryClaimed) {
                ValScreenPacket.send(serverPlayer, this.getId());
            }
        }
        return ActionResult.SUCCESS;
    }

    // --- Timer de despawn ----------
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            ticksAlive++;
            if (ticksAlive == DESPAWN_TICKS) {
                this.discard(); // Despawnea si dropear nada
            }
        }
    }

    // ---Persistencia en NBT (para que sobreviva reloads del mundo) -------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TicksAlive", ticksAlive);
        nbt.putBoolean("InvetoryClaimed", invetoryClaimed);

        NbtList itemList = new NbtList();
        for (ItemStack stack : storedInventory) {
            NbtCompound itemNbt = new NbtCompound();
            stack.writeNbt(itemNbt);
            itemList.add(itemNbt);
        }
        nbt.put("StoredInventory", itemList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ticksAlive = nbt.getInt("TicksAlive");
        invetoryClaimed = nbt.getBoolean("InvetoryClaimed");

        storedInventory.clear();
        NbtList itemList = nbt.getList("StoredInventory", 10);
        for (int i = 0; i < itemList.size(); i++) {
            storedInventory.add(ItemStack.fromNbt(itemList.getCompound(i)));
        }
    }

    // Val no puede ser agredida de vuelta
    @Override
    public boolean isAttackable() {
        return false;
    }

    // Evita que despawne naturalmente
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    // Necesario para que el pathfinding funcione aunque no camine
    @Override
    protected void mobTick() {
        super.mobTick();
    }

    // Fabrica estatica - usada internamente por Fabric
    public static ValEntity create(World world) {
        return new ValEntity(ModEntities.VAL, world);
    }
}
