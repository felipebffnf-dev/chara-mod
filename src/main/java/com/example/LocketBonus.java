package com.example;

import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class LocketBonus {
    private static final UUID BONUS_ID = UUID.fromString("7d3c1a52-9b4e-4f8a-a1c6-2e5b8d0f4c91");
    private static final AttributeModifier BONUS = new AttributeModifier(
        BONUS_ID, "chara_locket", 8.0, AttributeModifier.Operation.ADDITION);

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                update(player);
            }
        });
    }

    private static void update(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        boolean hasLocket = player.getInventory().contains(new ItemStack(ModItems.LOCKET));
        boolean applied = maxHealth.getModifier(BONUS_ID) != null;

        if (hasLocket && !applied) {
            maxHealth.addPermanentModifier(BONUS);
        } else if (!hasLocket && applied) {
            maxHealth.removeModifier(BONUS_ID);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}
