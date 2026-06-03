package com.rounds.zero.game.rules;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.GameState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerMeleeHandler {
    private static final double KNOCKBACK_STRENGTH = 0.4;

    private PlayerMeleeHandler() {
    }

    /**
     * During an active round, player melee (empty hand or pistol left-click) must not damage other players,
     * but should still knock them back. Non-player targets keep normal damage.
     */
    public static boolean shouldCancelPlayerMeleeDamage(ServerPlayerEntity victim, DamageSource source) {
        if (RoundsZero.GAME_MANAGER.getGameState() != GameState.ROUND_ACTIVE) {
            return false;
        }

        if (!source.isOf(DamageTypes.PLAYER_ATTACK)) {
            return false;
        }

        if (!(source.getAttacker() instanceof ServerPlayerEntity attacker)) {
            return false;
        }

        if (!RoundsZero.GAME_MANAGER.hasTeam(attacker) || !RoundsZero.GAME_MANAGER.isAliveInRound(attacker)) {
            return false;
        }

        if (attacker.isSpectator()) {
            return false;
        }

        return true;
    }

    public static void applyMeleeKnockback(ServerPlayerEntity attacker, ServerPlayerEntity victim) {
        double deltaX = attacker.getX() - victim.getX();
        double deltaZ = attacker.getZ() - victim.getZ();

        if (deltaX * deltaX + deltaZ * deltaZ < 1.0E-4) {
            double yawRadians = Math.toRadians(attacker.getYaw());
            deltaX = -Math.sin(yawRadians);
            deltaZ = Math.cos(yawRadians);
        }

        victim.takeKnockback(KNOCKBACK_STRENGTH, deltaX, deltaZ);
        victim.velocityModified = true;
    }
}
