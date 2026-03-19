package com.rounds.zero;

import com.rounds.zero.network.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
    private static KeyBinding shieldKey;
    private static KeyBinding reloadKey;

    @Override
    public void onInitializeClient() {
        shieldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rounds_zero.shield",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                "category.rounds_zero.CombatStats.java"
        ));

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rounds_zero.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.rounds_zero.CombatStats.java"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (shieldKey.wasPressed()) {
                sendEmptyPacket(ModPackets.SHIELD_USE);
            }

            while (reloadKey.wasPressed()) {
                sendEmptyPacket(ModPackets.RELOAD_WEAPON);
            }
        });
    }

    private static void sendEmptyPacket(net.minecraft.util.Identifier packetId) {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(packetId, buf);
    }
}