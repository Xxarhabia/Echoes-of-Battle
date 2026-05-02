package com.xarhabia.client.val.render;

import com.xarhabia.CombatMod;
import com.xarhabia.entities.val.ValEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class ValEntityRenderer extends LivingEntityRenderer<ValEntity, PlayerEntityModel<ValEntity>> {

    // Ruta a la textura - assets/combatmod/textures/entity/val_entity.png
    private static final Identifier TEXTURE = new Identifier(CombatMod.MOD_ID, "textures/entity/val_entity.png");

    public ValEntityRenderer(EntityRendererFactory.Context context) {
        super(
                context,
                // PLAYER_MAIN_LAYER = modelo slim=false (ALEX=true para cuerpo delgado)
                new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false),
                0.5f
        );
    }

    @Override
    public Identifier getTexture(ValEntity entity) {
        return TEXTURE;
    }
}
