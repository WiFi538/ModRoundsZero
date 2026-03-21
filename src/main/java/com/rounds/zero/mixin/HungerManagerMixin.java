package com.rounds.zero.mixin;

import com.rounds.zero.game.rules.NaturalRegenerationHandler;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Redirect(
            method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;canFoodHeal()Z")
    )
    private boolean roundsZero$blockNaturalRegeneration(ServerPlayerEntity player) {
        return NaturalRegenerationHandler.canFoodHeal(player);
    }
}