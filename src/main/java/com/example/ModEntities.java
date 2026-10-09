package com.example;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<CharaEntity> CHARA = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(ExampleMod.MOD_ID, "chara"),
        EntityType.Builder.of(CharaEntity::new, MobCategory.MONSTER)
            .sized(0.6f, 1.8f)
            .build("chara"));

    public static void register() {
        FabricDefaultAttributeRegistry.register(CHARA, CharaEntity.createAttributes());
    }
}
