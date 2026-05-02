package com.xarhabia.entities.val;

import com.xarhabia.entities.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ValEntity extends MobEntity {

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
