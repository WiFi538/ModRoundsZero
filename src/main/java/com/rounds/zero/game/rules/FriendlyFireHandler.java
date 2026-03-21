package com.rounds.zero.game.rules;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.team.TeamId;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class FriendlyFireHandler {
    private static boolean enabled = false;

    private FriendlyFireHandler() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        FriendlyFireHandler.enabled = enabled;
    }

    public static boolean shouldCancelDamage(ServerPlayerEntity victim, DamageSource source) {
        if (enabled) {
            return false;
        }

        Entity attackerEntity = source.getAttacker();
        if (!(attackerEntity instanceof ServerPlayerEntity attacker)) {
            return false;
        }

        if (attacker.getUuid().equals(victim.getUuid())) {
            return false;
        }

        TeamId attackerTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(attacker);
        TeamId victimTeam = RoundsZero.GAME_MANAGER.getPlayerTeam(victim);

        if (attackerTeam == TeamId.NONE || victimTeam == TeamId.NONE) {
            return false;
        }

        return attackerTeam == victimTeam;
    }
}