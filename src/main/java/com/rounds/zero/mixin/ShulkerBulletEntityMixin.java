package com.rounds.zero.mixin;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.entity.RoundsBulletEntityAccess;
import com.rounds.zero.game.combat.CombatManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBulletEntity.class)
public abstract class ShulkerBulletEntityMixin implements RoundsBulletEntityAccess {
    @Unique
    private float roundsZero$bulletDamage = 4.0f;

    @Unique
    private static boolean roundsZero$isModBullet(ShulkerBulletEntity bullet) {
        if (bullet.getCommandTags().contains(CombatManager.ROUNDS_BULLET_TAG)) {
            return true;
        }

        // Command tags are server-side; on client identify mod bullets by synced properties.
        return bullet.getOwner() instanceof PlayerEntity && bullet.hasNoGravity();
    }

    @Override
    public void roundsZero$setBulletDamage(float damage) {
        this.roundsZero$bulletDamage = damage;
    }

    @Override
    public float roundsZero$getBulletDamage() {
        return this.roundsZero$bulletDamage;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void roundsZero$customBulletTick(CallbackInfo ci) {
        ShulkerBulletEntity bullet = (ShulkerBulletEntity) (Object) this;
        if (!roundsZero$isModBullet(bullet)) {
            return;
        }

        bullet.baseTick();

        if (bullet.getWorld().isClient) {
            Vec3d velocity = bullet.getVelocity();
            bullet.setPosition(
                    bullet.getX() + velocity.x,
                    bullet.getY() + velocity.y,
                    bullet.getZ() + velocity.z
            );
            ci.cancel();
            return;
        }

        HitResult hitResult = ProjectileUtil.getCollision(
                bullet,
                hitTarget -> !hitTarget.isSpectator()
                        && hitTarget.isAlive()
                        && hitTarget.canHit()
                        && !(hitTarget instanceof ShulkerBulletEntity other && roundsZero$isModBullet(other))
        );

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
            Entity owner = bullet.getOwner();
            if (owner instanceof ServerPlayerEntity shooter && entityHitResult.getEntity() instanceof LivingEntity target) {
                target.damage(
                        shooter.getDamageSources().mobProjectile(bullet, shooter),
                        this.roundsZero$bulletDamage
                );
                RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitEntity(bullet, shooter, target, this.roundsZero$bulletDamage);
            }
            bullet.discard();
            ci.cancel();
            return;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
            Entity owner = bullet.getOwner();
            if (owner instanceof ServerPlayerEntity shooter) {
                RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitBlock(bullet, shooter, blockHitResult.getBlockPos());
            }
            bullet.discard();
            ci.cancel();
            return;
        }

        Vec3d velocity = bullet.getVelocity();
        bullet.setPosition(
                bullet.getX() + velocity.x,
                bullet.getY() + velocity.y,
                bullet.getZ() + velocity.z
        );

        if (!bullet.hasNoGravity()) {
            bullet.setVelocity(velocity.x, velocity.y - 0.05, velocity.z);
        }

        ci.cancel();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
            )
    )
    private void roundsZero$skipTrailParticle(
            World world,
            ParticleEffect parameters,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        ShulkerBulletEntity bullet = (ShulkerBulletEntity) (Object) this;
        if (roundsZero$isModBullet(bullet)) {
            return;
        }

        world.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void roundsZero$onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ShulkerBulletEntity bullet = (ShulkerBulletEntity) (Object) this;
        if (!roundsZero$isModBullet(bullet)) {
            return;
        }

        if (bullet.getWorld().isClient) {
            ci.cancel();
            return;
        }

        Entity owner = bullet.getOwner();
        if (!(owner instanceof ServerPlayerEntity shooter)) {
            bullet.discard();
            ci.cancel();
            return;
        }

        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            target.damage(
                    shooter.getDamageSources().mobProjectile(bullet, shooter),
                    this.roundsZero$bulletDamage
            );
            RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitEntity(bullet, shooter, target, this.roundsZero$bulletDamage);
        }

        bullet.discard();
        ci.cancel();
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    private void roundsZero$onBlockHit(net.minecraft.util.hit.BlockHitResult blockHitResult, CallbackInfo ci) {
        ShulkerBulletEntity bullet = (ShulkerBulletEntity) (Object) this;
        if (!roundsZero$isModBullet(bullet)) {
            return;
        }

        if (bullet.getWorld().isClient) {
            ci.cancel();
            return;
        }

        Entity owner = bullet.getOwner();
        if (!(owner instanceof ServerPlayerEntity shooter)) {
            bullet.discard();
            ci.cancel();
            return;
        }

        RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitBlock(bullet, shooter, blockHitResult.getBlockPos());
        bullet.discard();
        ci.cancel();
    }
}
