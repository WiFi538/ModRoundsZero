package com.rounds.zero.game.combat;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.team.TeamId;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3f;

public final class CombatExplosionHelper {
    public enum AllyDamageMode {
        DAMAGE_ALL,
        SKIP_ALLIES
    }

    private static final Vector3f EXPLOSION_RING_COLOR = new Vector3f(1.0f, 0.45f, 0.05f);

    private CombatExplosionHelper() {
    }

    public static void explode(
            ServerWorld world,
            ServerPlayerEntity source,
            Vec3d center,
            double radius,
            float damage,
            AllyDamageMode allyDamageMode
    ) {
        spawnExplosionVisuals(world, center, radius);

        world.createExplosion(
                source,
                source.getDamageSources().explosion(source, source),
                createNoBlockDamageBehavior(),
                center.x,
                center.y,
                center.z,
                (float) radius,
                false,
                World.ExplosionSourceType.MOB
        );

        if (damage <= 0.0f) {
            return;
        }

        double radiusSquared = radius * radius;
        Box area = new Box(
                center.x - radius,
                center.y - radius,
                center.z - radius,
                center.x + radius,
                center.y + radius,
                center.z + radius
        );

        DamageSource damageSource = source.getDamageSources().playerAttack(source);
        TeamId sourceTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(source);

        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (entity.squaredDistanceTo(center) > radiusSquared) {
                continue;
            }

            if (allyDamageMode == AllyDamageMode.SKIP_ALLIES && entity instanceof ServerPlayerEntity targetPlayer) {
                TeamId targetTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(targetPlayer);
                if (sourceTeam != TeamId.NONE && targetTeam == sourceTeam) {
                    continue;
                }
            }

            entity.damage(damageSource, damage);
        }
    }

    public static void spawnExplosionVisuals(ServerWorld world, Vec3d center, double radius) {
        world.spawnParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                center.x,
                center.y + 0.5,
                center.z,
                1,
                0.0,
                0.0,
                0.0,
                0.0
        );
        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                center.x,
                center.y + 0.25,
                center.z,
                16,
                radius * 0.35,
                0.25,
                radius * 0.35,
                0.02
        );
        world.spawnParticles(
                ParticleTypes.SMOKE,
                center.x,
                center.y + 0.4,
                center.z,
                24,
                radius * 0.45,
                0.35,
                radius * 0.45,
                0.015
        );
        world.spawnParticles(
                ParticleTypes.FLAME,
                center.x,
                center.y + 0.2,
                center.z,
                18,
                radius * 0.4,
                0.2,
                radius * 0.4,
                0.01
        );

        DustParticleEffect ringParticle = new DustParticleEffect(EXPLOSION_RING_COLOR, 1.6f);
        int ringPoints = 36;
        for (int index = 0; index < ringPoints; index++) {
            double angle = (Math.PI * 2.0 * index) / ringPoints;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            world.spawnParticles(ringParticle, x, center.y + 0.12, z, 2, 0.02, 0.04, 0.02, 0.0);
        }
    }

    private static ExplosionBehavior createNoBlockDamageBehavior() {
        return new ExplosionBehavior() {
            @Override
            public boolean canDestroyBlock(
                    net.minecraft.world.explosion.Explosion explosion,
                    net.minecraft.world.BlockView blockView,
                    net.minecraft.util.math.BlockPos pos,
                    net.minecraft.block.BlockState state,
                    float power
            ) {
                return false;
            }
        };
    }
}
