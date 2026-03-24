package com.rounds.zero.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.GameState;
import com.rounds.zero.game.arena.Arena;
import com.rounds.zero.game.scoreboard.MatchSidebarManager;
import com.rounds.zero.game.team.TeamId;
import com.rounds.zero.game.team.TeamVisualManager;
import com.rounds.zero.game.upgrade.UpgradeCard;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;

public class RoundsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("rounds")
                        .requires(source -> source.hasPermissionLevel(2))

                        .then(CommandManager.literal("join")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.literal("red")
                                                .executes(context -> {
                                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");

                                                    for (ServerPlayerEntity player : targets) {
                                                        RoundsZero.GAME_MANAGER.setPlayerTeam(player, TeamId.RED);
                                                        TeamVisualManager.setPlayerTeam(context.getSource().getServer(), player, TeamId.RED);
                                                        player.sendMessage(buildJoinTeamMessage(TeamId.RED), false);
                                                    }

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Игроки добавлены в красную команду."),
                                                            true
                                                    );
                                                    return targets.size();
                                                }))
                                        .then(CommandManager.literal("blue")
                                                .executes(context -> {
                                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");

                                                    for (ServerPlayerEntity player : targets) {
                                                        RoundsZero.GAME_MANAGER.setPlayerTeam(player, TeamId.BLUE);
                                                        TeamVisualManager.setPlayerTeam(context.getSource().getServer(), player, TeamId.BLUE);
                                                        player.sendMessage(buildJoinTeamMessage(TeamId.BLUE), false);
                                                    }

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Игроки добавлены в синюю команду."),
                                                            true
                                                    );
                                                    return targets.size();
                                                }))))
                                        .then(CommandManager.literal("green")
                                                .executes(context -> {
                                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");

                                                    for (ServerPlayerEntity player : targets) {
                                                        RoundsZero.GAME_MANAGER.setPlayerTeam(player, TeamId.GREEN);
                                                        TeamVisualManager.setPlayerTeam(context.getSource().getServer(), player, TeamId.GREEN);
                                                        player.sendMessage(buildJoinTeamMessage(TeamId.GREEN), false);
                                                    }

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Игроки добавлены в зеленую команду."),
                                                            true
                                                    );
                                                    return targets.size();
                                                }))
                                        .then(CommandManager.literal("yellow")
                                                .executes(context -> {
                                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");

                                                    for (ServerPlayerEntity player : targets) {
                                                        RoundsZero.GAME_MANAGER.setPlayerTeam(player, TeamId.YELLOW);
                                                        TeamVisualManager.setPlayerTeam(context.getSource().getServer(), player, TeamId.YELLOW);
                                                        player.sendMessage(buildJoinTeamMessage(TeamId.YELLOW), false);
                                                    }

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Игроки добавлены в желтую команду."),
                                                            true
                                                    );
                                                    return targets.size();
                                                }))

                        .then(CommandManager.literal("leave")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(context -> {
                                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");

                                            for (ServerPlayerEntity player : targets) {
                                                RoundsZero.GAME_MANAGER.clearPlayerTeam(player);
                                                TeamVisualManager.clearPlayerTeam(context.getSource().getServer(), player);
                                                player.sendMessage(Text.literal("Ты вышел из команды.").formatted(Formatting.GRAY), false);
                                            }

                                            context.getSource().sendFeedback(
                                                    () -> Text.literal("Игроки убраны из команд."),
                                                    true
                                            );
                                            return targets.size();
                                        })))


                        .then(CommandManager.literal("start")
                                .executes(context -> {
                                    if (!RoundsZero.GAME_MANAGER.canStartGame()) {
                                        context.getSource().sendError(Text.literal("Нельзя начать игру."));
                                        return 0;
                                    }

                                    MatchSidebarManager.setupSidebar(context.getSource().getServer());
                                    RoundsZero.GAME_MANAGER.startMatch(context.getSource().getServer());
                                    MatchSidebarManager.updateSidebar(context.getSource().getServer());

                                    Arena currentArena = RoundsZero.GAME_MANAGER.getCurrentArena();
                                    if (currentArena != null) {
                                        context.getSource().sendFeedback(
                                                () -> Text.literal("Матч запущен! Арена: ")
                                                        .append(Text.literal(currentArena.getName()).formatted(Formatting.GOLD)),
                                                true
                                        );
                                    } else {
                                        context.getSource().sendError(Text.literal("Не удалось выбрать арену."));
                                        return 0;
                                    }

                                    return 1;
                                }))


                        .then(CommandManager.literal("upgrades")
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");

                                            if (RoundsZero.GAME_MANAGER.getGameState() != GameState.UPGRADE_SELECTION) {
                                                context.getSource().sendError(Text.literal("Сейчас нет фазы выбора улучшений."));
                                                return 0;
                                            }

                                            if (!RoundsZero.GAME_MANAGER.isWaitingForUpgradeChoice(player)) {
                                                context.getSource().sendError(Text.literal("Этому игроку сейчас не нужно выбирать улучшение."));
                                                return 0;
                                            }

                                            List<UpgradeCard> offer = RoundsZero.GAME_MANAGER.getCurrentUpgradeOffer(player);

                                            player.sendMessage(Text.literal("Твои 5 карт:").formatted(Formatting.GOLD), false);

                                            for (int i = 0; i < offer.size(); i++) {
                                                UpgradeCard card = offer.get(i);

                                                player.sendMessage(
                                                        Text.literal((i + 1) + ". ").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(card.getTitle()).formatted(Formatting.AQUA))
                                                                .append(Text.literal(" — " + card.getDescription()).formatted(Formatting.GRAY)),
                                                        false
                                                );
                                            }

                                            player.sendMessage(
                                                    Text.literal("Выбери карту командой /rounds pick <player> <1-5>").formatted(Formatting.GREEN),
                                                    false
                                            );

                                            context.getSource().sendFeedback(
                                                    () -> Text.literal("Список улучшений отправлен игроку " + player.getName().getString() + "."),
                                                    true
                                            );

                                            return 1;
                                        })
                                        .then(CommandManager.literal("owned")
                                                .executes(context -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                                    List<UpgradeCard> owned = RoundsZero.GAME_MANAGER.getOwnedUpgrades(player);

                                                    if (owned.isEmpty()) {
                                                        player.sendMessage(Text.literal("У тебя пока нет выбранных улучшений.").formatted(Formatting.GRAY), false);
                                                        return 1;
                                                    }

                                                    player.sendMessage(Text.literal("Твои выбранные улучшения:").formatted(Formatting.GOLD), false);

                                                    for (UpgradeCard card : owned) {
                                                        player.sendMessage(
                                                                Text.literal("- ").formatted(Formatting.YELLOW)
                                                                        .append(Text.literal(card.getTitle()).formatted(Formatting.AQUA))
                                                                        .append(Text.literal(" — " + card.getDescription()).formatted(Formatting.GRAY)),
                                                                false
                                                        );
                                                    }

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Список выбранных улучшений отправлен игроку " + player.getName().getString() + "."),
                                                            true
                                                    );

                                                    return 1;
                                                }))))

                        // починил надеюсь
                        .then(CommandManager.literal("pick")
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 5))
                                                .executes(context -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
                                                    int slot = IntegerArgumentType.getInteger(context, "slot");

                                                    boolean success = RoundsZero.GAME_MANAGER.submitUpgradeChoice(
                                                            context.getSource().getServer(),
                                                            player,
                                                            slot
                                                    );

                                                    if (success) {
                                                        MatchSidebarManager.updateSidebar(context.getSource().getServer());
                                                        context.getSource().sendFeedback(
                                                                () -> Text.literal("Игрок " + player.getName().getString() + " выбрал карту #" + slot + "."),
                                                                true
                                                        );
                                                        return 1;
                                                    }

                                                    context.getSource().sendError(Text.literal("Не удалось выбрать карту."));
                                                    return 0;
                                                }))))


                        .then(CommandManager.literal("debug")
                                .then(CommandManager.literal("win")
                                        .then(CommandManager.literal("red")
                                                .executes(context -> {
                                                    if (RoundsZero.GAME_MANAGER.getGameState() != GameState.ROUND_ACTIVE) {
                                                        context.getSource().sendError(Text.literal("Сейчас нет активного раунда."));
                                                        return 0;
                                                    }

                                                    RoundsZero.GAME_MANAGER.debugWinRound(context.getSource().getServer(), TeamId.RED);
                                                    MatchSidebarManager.updateSidebar(context.getSource().getServer());
                                                    return 1;
                                                }))
                                        .then(CommandManager.literal("blue")
                                                .executes(context -> {
                                                    if (RoundsZero.GAME_MANAGER.getGameState() != GameState.ROUND_ACTIVE) {
                                                        context.getSource().sendError(Text.literal("Сейчас нет активного раунда."));
                                                        return 0;
                                                    }

                                                    RoundsZero.GAME_MANAGER.debugWinRound(context.getSource().getServer(), TeamId.BLUE);
                                                    MatchSidebarManager.updateSidebar(context.getSource().getServer());
                                                    return 1;
                                                })))
                                .then(CommandManager.literal("upgrades")
                                        .then(CommandManager.literal("done")
                                                .executes(context -> {
                                                    if (RoundsZero.GAME_MANAGER.getGameState() != GameState.UPGRADE_SELECTION) {
                                                        context.getSource().sendError(Text.literal("Сейчас нет фазы выбора улучшений."));
                                                        return 0;
                                                    }

                                                    RoundsZero.GAME_MANAGER.forceFinishUpgradeSelection(context.getSource().getServer());
                                                    MatchSidebarManager.updateSidebar(context.getSource().getServer());

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Фаза улучшений принудительно завершена. Новый раунд начат."),
                                                            true
                                                    );

                                                    return 1;
                                                }))))
        );
    }

    private static MutableText buildJoinTeamMessage(TeamId teamId) {
        if (teamId == TeamId.RED) {
            return Text.literal("Ты присоединился к ")
                    .append(Text.literal("КРАСНОЙ").formatted(Formatting.RED))
                    .append(Text.literal(" команде!"));
        }

        if (teamId == TeamId.BLUE) {
            return Text.literal("Ты присоединился к ")
                    .append(Text.literal("СИНЕЙ").formatted(Formatting.BLUE))
                    .append(Text.literal(" команде!"));
        }

        if (teamId == TeamId.GREEN) {
            return Text.literal("Ты присоединился к ")
                    .append(Text.literal("ЗЕЛЕНОЙ").formatted(Formatting.GREEN))
                    .append(Text.literal(" команде!"));
        }

        if (teamId == TeamId.YELLOW) {
            return Text.literal("Ты присоединился к ")
                    .append(Text.literal("ЖЕЛТОЙ").formatted(Formatting.YELLOW))
                    .append(Text.literal(" команде!"));
        }

        return Text.literal("Команда не выбрана.").formatted(Formatting.GRAY);
    }
}