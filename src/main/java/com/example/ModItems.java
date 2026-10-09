package com.example;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class ModItems {
    public static final Item TOY_KNIFE = Registry.register(
        BuiltInRegistries.ITEM,
        new ResourceLocation(ExampleMod.MOD_ID, "toy_knife"),
        new SwordItem(Tiers.IRON, 4, -2.0f, new Item.Properties()));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
            .register(entries -> entries.accept(TOY_KNIFE));
    }
}
