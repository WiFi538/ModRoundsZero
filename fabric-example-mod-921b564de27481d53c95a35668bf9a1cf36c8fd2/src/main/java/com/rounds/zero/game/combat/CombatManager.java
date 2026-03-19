package com.rounds.zero.game.combat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {
    private final Map<UUID, PlayerCombatData> playerCombatData = new HashMap<>();

    public PlayerCombatData getOrCreate(ServerPlayerEntity player) {
        return playerCombatData.computeIfAbsent(player.getUuid(), uuid -> new PlayerCombatData());
    }

    public PlayerCombatData get(ServerPlayerEntity player) {
        return playerCombatData.get(player.getUuid());
    }

    public void remove(ServerPlayerEntity player) {
        playerCombatData.remove(player.getUuid());
    }

    public void preparePlayerForRound(ServerPlayerEntity player, CombatStats computedStats) {
        PlayerCombatData data = getOrCreate(player);
        data.setCombatStats(computedStats);
        data.resetForRound();
        sendAmmoStatus(player, data);
    }

    public void resetPlayerState(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        data.setReloading(false);
        data.setReloadFinishTick(0L);
        data.setShieldActive(false);
        data.setShieldEndTick(0L);
        data.setShieldCooldownEndTick(0L);
    }

    public boolean tryActivateShield(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        long now = getNow(player.getServer());

        if (data.isShieldActive()) {
            player.sendMessage(Text.literal("Щит уже активен.").formatted(Formatting.AQUA), true);
            return false;
        }

        if (now < data.getShieldCooldownEndTick()) {
            long remainingTicks = data.getShieldCooldownEndTick() - now;
            double seconds = remainingTicks / 20.0d;
            player.sendMessage(Text.literal(String.format("Щит в кулдауне: %.1f сек.", seconds)).formatted(Formatting.RED), true);
            return false;
        }

        CombatStats stats = data.getCombatStats();
        data.setShieldActive(true);
        data.setShieldEndTick(now + stats.getShieldDurationTicks());
        data.setShieldCooldownEndTick(now + stats.getShieldCooldownTicks());

        player.sendMessage(Text.literal("Щит активирован.").formatted(Formatting.AQUA), true);
        return true;
    }

    public boolean tryStartReload(ServerPlayerEntity player) {
        PlayerCombatData data = getOrCreate(player);
        CombatStats stats = data.getCombatStats();

        if (data.isReloading()) {
            player.sendMessage(Text.literal("Перезарядка уже идёт.").formatted(Formatting.YELLOW), true);
            return false;
        }

        if (data.getCurrentAmmo() >= stats.getMaxAmmo()) {
            player.sendMessage(Text.literal("Магазин уже полный.").formatted(Formatting.GRAY), true);
            return false;
        }

        long now = getNow(player.getServer());
        data.setReloading(true);
        data.setReloadFinishTick(now + stats.getReloadDurationTicks());
        player.sendMessage(Text.literal("Перезарядка...").formatted(Formatting.YELLOW), true);
        return true;
    }

    public void tick(MinecraftServer server) {
        long now = getNow(server);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerCombatData data = playerCombatData.get(player.getUuid());
            if (data == null) {
                continue;
            }

            if (data.isShieldActive() && now >= data.getShieldEndTick()) {
                data.setShieldActive(false);
                data.setShieldEndTick(0L);
                player.sendMessage(Text.literal("Щит закончился.").formatted(Formatting.GRAY), true);
            }

            if (data.isReloading() && now >= data.getReloadFinishTick()) {
                data.setReloading(false);
                data.setReloadFinishTick(0L);
                data.setCurrentAmmo(data.getCombatStats().getMaxAmmo());
                player.sendMessage(Text.literal("Перезарядка завершена.").formatted(Formatting.GREEN), true);
                sendAmmoStatus(player, data);
            }
        }
    }

    public boolean isShieldActive(ServerPlayerEntity player) {
        PlayerCombatData data = get(player);
        return data != null && data.isShieldActive();
    }

    public long getNow(MinecraftServer server) {
        return server == null ? 0L : server.getOverworld().getTime();
    }

    public void sendAmmoStatus(ServerPlayerEntity player, PlayerCombatData data) {
        player.sendMessage(
                Text.literal("Патроны: " + data.getCurrentAmmo() + "/" + data.getCombatStats().getMaxAmmo())
                        .formatted(Formatting.GOLD),
                true
        );
    }
}
