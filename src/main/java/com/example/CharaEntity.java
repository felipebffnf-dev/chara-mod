package com.example;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CharaEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.literal("Chara"),
        BossEvent.BossBarColor.RED,
        BossEvent.BossBarOverlay.PROGRESS);

    private int phase = 1;

    public CharaEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.MOVEMENT_SPEED, 0.32)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16f));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
            entity -> !(entity instanceof CharaEntity)));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        float ratio = getHealth() / getMaxHealth();
        bossEvent.setProgress(ratio);

        int newPhase = ratio > 0.6f ? 1 : (ratio > 0.3f ? 2 : 3);
        if (newPhase != phase) {
            phase = newPhase;
            applyPhase();
        }

        LivingEntity target = getTarget();
        if (target != null && phase >= 2 && tickCount % 60 == 0) {
            shootFan(target);
        }
        if (phase == 3 && tickCount % 100 == 0) {
            areaAttack();
        }
    }

    private void applyPhase() {
        if (phase == 2) {
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36);
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0);
        } else if (phase == 3) {
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.42);
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(13.0);
        }
    }

    private void shootFan(LivingEntity target) {
        Vec3 dir = target.getEyePosition().subtract(getEyePosition()).normalize();
        for (int i = -1; i <= 1; i++) {
            double angle = i * 0.25;
            double c = Math.cos(angle);
            double s = Math.sin(angle);
            double dx = dir.x * c - dir.z * s;
            double dz = dir.x * s + dir.z * c;
            Arrow arrow = new Arrow(level(), this);
            arrow.setPos(getX(), getEyeY() - 0.1, getZ());
            arrow.shoot(dx, dir.y, dz, 1.6f, 0.0f);
            arrow.setBaseDamage(4.0);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            level().addFreshEntity(arrow);
        }
    }

    private void areaAttack() {
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.0, getZ(),
                40, 2.0, 0.5, 2.0, 0.1);
        }
        for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(4.0))) {
            p.hurt(damageSources().mobAttack(this), 10.0f);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }
}
