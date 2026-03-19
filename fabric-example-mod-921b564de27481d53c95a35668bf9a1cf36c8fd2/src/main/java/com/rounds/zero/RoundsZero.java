package com.rounds.zero;

import com.rounds.zero.command.RoundsCommand;
import com.rounds.zero.game.GameManager;
import com.rounds.zero.game.arena.Arena;
import com.rounds.zero.network.ModPackets;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundsZero implements ModInitializer {
    public static final String MOD_ID = "rounds_zero";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameManager GAME_MANAGER = new GameManager();

    @Override
    public void onInitialize() {
        LOGGER.info("ROUNDS ZERO MOD LOADED!!!");

        GAME_MANAGER.addArena(new Arena(
                "TEST_green",
                new BlockPos(84, -34, 49),
                new BlockPos(57, -34, 76)
        ));

        GAME_MANAGER.addArena(new Arena(
                "TEST_pink",
                new BlockPos(85, -34, 86),
                new BlockPos(58, -34, 113)
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RoundsCommand.register(dispatcher);
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                return !GAME_MANAGER.isPlayerShielded(player);
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player && player.getServer() != null) {
                GAME_MANAGER.handlePlayerDeath(player.getServer(), player);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            GAME_MANAGER.handlePlayerRespawn(newPlayer);
        });

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.SHIELD_USE, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> GAME_MANAGER.handleShieldRequest(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.RELOAD_WEAPON, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> GAME_MANAGER.handleReloadRequest(player));
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> GAME_MANAGER.tickCombat(server));
    }
}