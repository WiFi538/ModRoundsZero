package com.rounds.zero.game.rules;

import net.minecraft.server.network.ServerPlayerEntity;

public final class NaturalRegenerationHandler {
    private static boolean disabled = true;

    private NaturalRegenerationHandler() {
    }

    public static boolean isDisabled() {
        return disabled;
    }

    public static void setDisabled(boolean disabled) {
        NaturalRegenerationHandler.disabled = disabled;
    }

    public static boolean canFoodHeal(ServerPlayerEntity player) {
        if (disabled) {
            return false;
        }

        return player.canFoodHeal();
    }
}