package com.rounds.zero.client.network;

import com.rounds.zero.client.screen.UpgradeSelectScreen;
import com.rounds.zero.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.ArrayList;
import java.util.List;

public class UpgradeClientPackets {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_UPGRADE_SCREEN, (client, handler, buf, responseSender) -> {

            int size = buf.readInt();

            List<UpgradeSelectScreen.Card> cards = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                cards.add(new UpgradeSelectScreen.Card(
                        buf.readString(),
                        buf.readString(),
                        buf.readString(),
                        buf.readString()
                ));
            }

            client.execute(() -> {
                client.setScreen(new UpgradeSelectScreen(cards));
            });
        });
    }

    public static void sendSelect(int index) {
        var buf = new net.minecraft.network.PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeInt(index);

        ClientPlayNetworking.send(ModPackets.SELECT_UPGRADE, buf);
    }
}