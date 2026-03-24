package com.rounds.zero;

import com.rounds.zero.command.RoundsCommand;
import com.rounds.zero.game.GameManager;
import com.rounds.zero.game.arena.Arena;
import com.rounds.zero.game.combat.CombatManager;
import com.rounds.zero.item.ModWeaponItems;
import com.rounds.zero.network.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundsZero implements ModInitializer {
    public static final String MOD_ID = "rounds_zero";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameManager GAME_MANAGER = new GameManager();

    @Override
    public void onInitialize() {
        ModPackets.registerC2SPackets();
        LOGGER.info("ROUNDS ZERO MOD LOADED!!!");

        GAME_MANAGER.addArena(new Arena(
                "TEST_green",
                new BlockPos(84, -34, 49),   // red
                new BlockPos(57, -34, 76),   // blue
                new BlockPos(84, -34, 76),   // green
                new BlockPos(57, -34, 49)    // yellow
        ));

        GAME_MANAGER.addArena(new Arena(
                "TEST_pink",
                new BlockPos(85, -34, 86),
                new BlockPos(58, -34, 113),
                new BlockPos(85, -34, 113),
                new BlockPos(58, -34, 86)
        ));

        GAME_MANAGER.addArena(new Arena(
                "Cave",
                new BlockPos(153, -34, 87),
                new BlockPos(128, -34, 112),
                new BlockPos(153, -34, 112),
                new BlockPos(128, -34, 87)
        ));


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RoundsCommand.register(dispatcher));

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player && player.getServer() != null) {
                GAME_MANAGER.handlePlayerDeath(player.getServer(), player);
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) {
                return true;
            }

            if (!GAME_MANAGER.isPlayerShieldActive(player)) {
                return true;
            }

            boolean blocked = GAME_MANAGER.getCombatManager().blockDamageWithShield(player);
            if (blocked) {
                markProjectileAsShieldBlocked(source);
                return false;
            }

            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> GAME_MANAGER.handlePlayerRespawn(newPlayer));

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.SHIELD_USE, (server, player, handler, buf, responseSender) ->
                server.execute(() -> GAME_MANAGER.handleShieldRequest(player))
        );

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.RELOAD_WEAPON, (server, player, handler, buf, responseSender) ->
                server.execute(() -> GAME_MANAGER.handleReloadRequest(player))
        );

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (ModWeaponItems.isPistol(player.getStackInHand(hand))) {
                GAME_MANAGER.handleShootRequest((ServerPlayerEntity) player);
                return TypedActionResult.success(player.getStackInHand(hand));
            }

            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        ServerTickEvents.END_SERVER_TICK.register(GAME_MANAGER::tickCombat);
    }

    private static void markProjectileAsShieldBlocked(DamageSource source) {
        if (source.getSource() instanceof PersistentProjectileEntity projectile) {
            projectile.addCommandTag(CombatManager.SHIELD_BLOCKED_TAG);
        }
    }
}