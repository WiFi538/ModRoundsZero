package com.rounds.zero.network;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.upgrade.UpgradeCard;
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

    private ModPackets() {
    }

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SELECT_UPGRADE, (server, player, handler, buf, responseSender) -> {
            int index = buf.readInt();

            server.execute(() -> {
                RoundsZero.GAME_MANAGER.submitUpgradeChoice(server, player, index);
            });
        });
    }

    public static void sendUpgradeScreen(ServerPlayerEntity player, List<UpgradeCard> cards) {
        var buf = PacketByteBufs.create();

        buf.writeInt(cards.size());

        for (UpgradeCard card : cards) {
            buf.writeString(card.getId());
            buf.writeString(card.getTitle());
            buf.writeString(card.getDescription());
            buf.writeString(card.getTexturePath());
        }

        ServerPlayNetworking.send(player, OPEN_UPGRADE_SCREEN, buf);
    }
}