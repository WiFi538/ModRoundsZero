package com.rounds.zero.command;

import com.mojang.brigadier.CommandDispatcher;
import com.rounds.zero.game.rules.FriendlyFireHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class FriendlyFireCommand {
    private FriendlyFireCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("friendlyfire")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("on")
                        .executes(context -> {
                            FriendlyFireHandler.setEnabled(true);
                            context.getSource().sendFeedback(() -> Text.literal("Friendly fire включен."), true);
                            return 1;
                        }))
                .then(CommandManager.literal("off")
                        .executes(context -> {
                            FriendlyFireHandler.setEnabled(false);
                            context.getSource().sendFeedback(() -> Text.literal("Friendly fire выключен."), true);
                            return 1;
                        }))
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            boolean enabled = FriendlyFireHandler.isEnabled();
                            context.getSource().sendFeedback(() -> Text.literal("Friendly fire: " + (enabled ? "ON" : "OFF")), false);
                            return enabled ? 1 : 0;
                        }))
                .executes(context -> {
                    boolean enabled = FriendlyFireHandler.isEnabled();
                    context.getSource().sendFeedback(() -> Text.literal("Friendly fire: " + (enabled ? "ON" : "OFF")), false);
                    return enabled ? 1 : 0;
                }));
    }
}