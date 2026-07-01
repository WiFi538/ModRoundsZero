package com.rounds.zero.network;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.upgrade.UpgradeCard;
import com.rounds.zero.game.upgrade.UpgradeCardCategory;
import com.rounds.zero.game.upgrade.UpgradeCardCategoryResolver;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ModPackets {
    public static final Identifier SHIELD_USE = new Identifier(RoundsZero.MOD_ID, "shield_use");
    public static final Identifier RELOAD_WEAPON = new Identifier(RoundsZero.MOD_ID, "reload_weapon");
    public static final Identifier OPEN_UPGRADE_SCREEN = new Identifier(RoundsZero.MOD_ID, "open_upgrade_screen");
    public static final Identifier SELECT_UPGRADE = new Identifier(RoundsZero.MOD_ID, "select_upgrade");
    public static final Identifier REQUEST_PLAYER_STATS = new Identifier(RoundsZero.MOD_ID, "request_player_stats");
    public static final Identifier SYNC_PLAYER_STATS = new Identifier(RoundsZero.MOD_ID, "sync_player_stats");

    private ModPackets() {
    }

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SELECT_UPGRADE, (server, player, handler, buf, responseSender) -> {
            int index = buf.readInt();

            server.execute(() -> {
                RoundsZero.GAME_MANAGER.submitUpgradeChoice(server, player, index);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PLAYER_STATS, (server, player, handler, buf, responseSender) ->
                server.execute(() -> sendPlayerStats(player))
        );
    }

    public static void sendUpgradeScreen(ServerPlayerEntity player, List<UpgradeCard> cards, long choiceUnlockTick) {
        var buf = PacketByteBufs.create();

        buf.writeVarLong(choiceUnlockTick);
        buf.writeInt(cards.size());

        for (UpgradeCard card : cards) {
            buf.writeString(card.getId());
            buf.writeString(card.getTitle());
            buf.writeString(card.getDescription());
            buf.writeString(toWireCategoryId(UpgradeCardCategoryResolver.resolve(card)));
        }

        ServerPlayNetworking.send(player, OPEN_UPGRADE_SCREEN, buf);
    }

    public static void sendPlayerStats(ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        PlayerStatsSnapshot.create(player).write(buf);
        ServerPlayNetworking.send(player, SYNC_PLAYER_STATS, buf);
    }

    private static String toWireCategoryId(UpgradeCardCategory category) {
        return switch (category) {
            case HEALTH -> "health";
            case BULLET_EFFECT -> "bullet";
            case SHIELD_EFFECT -> "shield";
            default -> "weapon";
        };
    }
}