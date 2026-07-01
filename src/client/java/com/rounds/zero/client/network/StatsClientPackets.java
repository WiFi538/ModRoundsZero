package com.rounds.zero.client.network;

import com.rounds.zero.client.screen.PlayerStatsScreen;
import com.rounds.zero.network.ModPackets;
import com.rounds.zero.network.PlayerStatsSnapshot;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;

public final class StatsClientPackets {
    private static Screen pendingReturnScreen;

    private StatsClientPackets() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_PLAYER_STATS, (client, handler, buf, responseSender) -> {
            PlayerStatsSnapshot snapshot = PlayerStatsSnapshot.read(buf);
            Screen returnTo = pendingReturnScreen;
            pendingReturnScreen = null;

            client.execute(() -> client.setScreen(new PlayerStatsScreen(snapshot, returnTo)));
        });
    }

    public static void requestStats() {
        requestStats(null);
    }

    public static void requestStats(Screen returnTo) {
        pendingReturnScreen = returnTo;
        ClientPlayNetworking.send(ModPackets.REQUEST_PLAYER_STATS, PacketByteBufs.create());
    }
}
