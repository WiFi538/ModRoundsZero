package com.rounds.zero.mixin;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.combat.CombatManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void roundsZero$onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        if (projectile.getWorld().isClient()) {
            return;
        }

        if (!projectile.getCommandTags().contains(CombatManager.ROUNDS_BULLET_TAG)) {
            return;
        }

        Entity owner = projectile.getOwner();
        if (!(owner instanceof ServerPlayerEntity shooter)) {
            return;
        }

        if (entityHitResult.getEntity() instanceof ServerPlayerEntity target) {
            RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitEntity(projectile, shooter, target);
        }
    }

    @Inject(method = "onBlockHit", at = @At("TAIL"))
    private void roundsZero$onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        if (projectile.getWorld().isClient()) {
            return;
        }

        if (!projectile.getCommandTags().contains(CombatManager.ROUNDS_BULLET_TAG)) {
            return;
        }

        Entity owner = projectile.getOwner();
        if (!(owner instanceof ServerPlayerEntity shooter)) {
            return;
        }

        RoundsZero.GAME_MANAGER.getCombatManager().handleProjectileHitBlock(projectile, shooter, blockHitResult.getBlockPos());
    }
}
