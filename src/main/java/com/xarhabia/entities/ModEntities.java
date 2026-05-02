package com.xarhabia.entities;

import com.xarhabia.CombatMod;
import com.xarhabia.entities.val.ValEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<ValEntity> VAL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(CombatMod.MOD_ID, "val"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, ValEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static void register() {
        CombatMod.LOGGER.info("Registrando entidades de " + CombatMod.MOD_ID);
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(VAL, ValEntity.createAttributes());
    }
}
