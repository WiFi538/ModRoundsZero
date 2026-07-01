package com.rounds.zero;

import com.rounds.zero.client.network.StatsClientPackets;
import com.rounds.zero.client.network.UpgradeClientPackets;
import com.rounds.zero.client.screen.PlayerStatsScreen;
import com.rounds.zero.network.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
    private static KeyBinding shieldKey;
    private static KeyBinding reloadKey;
    private static KeyBinding statsKey;

    @Override
    public void onInitializeClient() {
        shieldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rounds_zero.shield",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                "category.rounds_zero.combat"
        ));

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rounds_zero.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.rounds_zero.combat"
        ));

        statsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rounds_zero.stats",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.rounds_zero.combat"
        ));

        UpgradeClientPackets.init();
        StatsClientPackets.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (shieldKey.wasPressed()) {
                sendEmptyPacket(ModPackets.SHIELD_USE);
            }

            while (reloadKey.wasPressed()) {
                sendEmptyPacket(ModPackets.RELOAD_WEAPON);
            }

            while (statsKey.wasPressed()) {
                if (client.player == null) {
                    continue;
                }

                if (client.currentScreen instanceof PlayerStatsScreen statsScreen) {
                    statsScreen.close();
                } else {
                    StatsClientPackets.requestStats(client.currentScreen);
                }
            }
        });
    }

    private static void sendEmptyPacket(net.minecraft.util.Identifier packetId) {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(packetId, buf);
    }
}
