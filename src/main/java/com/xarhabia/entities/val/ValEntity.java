package com.xarhabia.entities.val;

import com.xarhabia.entities.ModEntities;
import com.xarhabia.entities.val.networking.ValHealthPacket;
import com.xarhabia.entities.val.networking.ValScreenPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ValEntity extends PathAwareEntity {

    private static final int DESPAWN_TICKS = 5 * 60 * 20;

    private final List<ItemStack> storedInventory = new ArrayList<>();
    private int ticksAlive = 0;
    private boolean invetoryClaimed = false;

    private static final int HID_THRESHOLD = 7;
    private int hitCount = 0;
    private boolean isAngry = false;

    private boolean pendingAngryActivation = false;
    private boolean pendingReset = false;
    private PlayerEntity angryTarget = null;

    public ValEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0);
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
        invetoryClaimed = true;
    }

    public void storeInventoryFromList(List<ItemStack> items) {
        storedInventory.clear();
        storedInventory.addAll(items);
    }

    // --- Click derecho: abrir pantalla --------
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (!storedInventory.isEmpty() && !invetoryClaimed && !isAngry) {
                ValScreenPacket.send(serverPlayer, this.getId());
            }
        }
        return ActionResult.SUCCESS;
    }

    // --- Timer de despawn ----------
    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            if (pendingAngryActivation) {
                pendingAngryActivation = false;
                activateAngryMode(angryTarget);
            }
            if (pendingReset) {
                pendingReset = false;
                applyPassiveMode();
            }
        }

        super.tick();

        if (!this.getWorld().isClient()) {
            ticksAlive++;
            if (!isAngry && ticksAlive >= DESPAWN_TICKS) {
                this.discard(); // Despawnea si dropear nada
                return;
            }

            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.getPlayers().forEach(player -> {
                if (player.squaredDistanceTo(this) < 64 * 64) {
                    ValHealthPacket.send(
                            player,
                            this.getHealth(),
                            this.getMaxHealth(),
                            isAngry
                    );
                }
            });
        }
    }

    // ---Persistencia en NBT (para que sobreviva reloads del mundo) -------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TicksAlive", ticksAlive);
        nbt.putBoolean("InvetoryClaimed", invetoryClaimed);
        nbt.putInt("HitCount", hitCount);
        nbt.putBoolean("IsAngry", isAngry);

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
        hitCount = nbt.getInt("HitCount");
        isAngry = nbt.getBoolean("IsAngry");

        if (isAngry) {
            this.goalSelector.clear(goal -> true);
            this.targetSelector.clear(goal -> true);
            this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, true));
            this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
            this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
            this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
            this.setInvulnerable(false);
        }

        NbtList itemList = nbt.getList("StoredInventory", 10);
        storedInventory.clear();
        for (int i = 0; i < itemList.size(); i++) {
            storedInventory.add(ItemStack.fromNbt(itemList.getCompound(i)));
        }
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof PlayerEntity player)) {
            return false;
        }

        if (isAngry) {
            return super.damage(source, amount);
        }

        hitCount++;

        // Solo efecto visual sin daño real
        this.timeUntilRegen = 0;
        this.hurtTime = this.maxHurtTime;

//        this.setInvulnerable(false);
//        boolean result = super.damage(source, amount);
//        this.setInvulnerable(true);

        if (hitCount >= HID_THRESHOLD) {
            pendingAngryActivation = true;
            angryTarget = player;
        }

        return true;
    }

    private void applyPassiveMode() {
        isAngry = false;
        hitCount = 0;
        this.setInvulnerable(true);
        this.setTarget(null);

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        this.setHealth(20.0f);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);

        this.goalSelector.clear(goal -> true);
        this.targetSelector.clear(goal -> true);
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    private void activateAngryMode(PlayerEntity player) {
        isAngry = true;
        this.setInvulnerable(false);

        // aplicar atributos divinos
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .setBaseValue(200.0); //100 corazones
        this.setHealth(200.0f);

        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.45);

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(15.0);

        // activar IA de combate
        this.goalSelector.clear(goal -> true);
        this.targetSelector.clear(goal -> true);

        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));

        // RevengeGoal hace que mantenga el target de quien la golpeó
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

        // Apuntar al jugador que la golpeo para señalar el cambio
        this.setTarget(player);

        // Efecto de particulas para señalar el cambio
        if (!this.getWorld().isClient()) {
            ((ServerWorld) this.getWorld()).spawnParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    this.getX(), this.getY() + 1, this.getZ(),
                    50, 0.5, 1.0, 0.5, 0.3
            );
        }
    }

    public void resetToPassive() {
        pendingReset = true;
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
