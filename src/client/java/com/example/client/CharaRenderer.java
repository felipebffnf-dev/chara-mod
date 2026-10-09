package com.example.client;

import com.example.CharaEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CharaRenderer extends HumanoidMobRenderer<CharaEntity, HumanoidModel<CharaEntity>> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("modid", "textures/entity/chara.png");

    public CharaRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CharaEntity entity) {
        return TEXTURE;
    }
}
