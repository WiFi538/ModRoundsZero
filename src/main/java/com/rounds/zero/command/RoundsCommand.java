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

    private static int joinPlayersToTeam(ServerCommandSource source, Collection<ServerPlayerEntity> targets, TeamId teamId, String successMessage) {
        for (ServerPlayerEntity player : targets) {
            RoundsZero.GAME_MANAGER.setPlayerTeam(player, teamId);
            TeamVisualManager.setPlayerTeam(source.getServer(), player, teamId);
            player.sendMessage(buildJoinTeamMessage(teamId), false);
        }

        source.sendFeedback(() -> Text.literal(successMessage), true);
        return targets.size();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("rounds")
                        .requires(source -> source.hasPermissionLevel(2))

                        .then(CommandManager.literal("join")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.literal("red")
                                                .executes(context -> joinPlayersToTeam(
                                                        context.getSource(),
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        TeamId.RED,
                                                        "Игроки добавлены в красную команду."
                                                )))
                                        .then(CommandManager.literal("blue")
                                                .executes(context -> joinPlayersToTeam(
                                                        context.getSource(),
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        TeamId.BLUE,
                                                        "Игроки добавлены в синюю команду."
                                                )))
                                        .then(CommandManager.literal("green")
                                                .executes(context -> joinPlayersToTeam(
                                                        context.getSource(),
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        TeamId.GREEN,
                                                        "Игроки добавлены в зеленую команду."
                                                )))
                                        .then(CommandManager.literal("yellow")
                                                .executes(context -> joinPlayersToTeam(
                                                        context.getSource(),
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        TeamId.YELLOW,
                                                        "Игроки добавлены в желтую команду."
                                                )))))

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

                        .then(CommandManager.literal("config")
                                .then(CommandManager.literal("rounds_to_win")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, 20))
                                                .executes(context -> {
                                                    int value = IntegerArgumentType.getInteger(context, "value");

                                                    RoundsZero.GAME_MANAGER.setRoundsToWin(value);
                                                    MatchSidebarManager.updateSidebar(context.getSource().getServer());

                                                    context.getSource().sendFeedback(
                                                            () -> Text.literal("Количество раундов для победы установлено: ")
                                                                    .append(Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)),
                                                            true
                                                    );

                                                    return 1;
                                                }))))
        );
    }

    private static MutableText buildJoinTeamMessage(TeamId teamId) {
        return switch (teamId) {
            case RED -> Text.literal("Ты присоединился к ")
                    .append(Text.literal("КРАСНОЙ").formatted(Formatting.RED))
                    .append(Text.literal(" команде!"));
            case BLUE -> Text.literal("Ты присоединился к ")
                    .append(Text.literal("СИНЕЙ").formatted(Formatting.BLUE))
                    .append(Text.literal(" команде!"));
            case GREEN -> Text.literal("Ты присоединился к ")
                    .append(Text.literal("ЗЕЛЕНОЙ").formatted(Formatting.GREEN))
                    .append(Text.literal(" команде!"));
            case YELLOW -> Text.literal("Ты присоединился к ")
                    .append(Text.literal("ЖЕЛТОЙ").formatted(Formatting.YELLOW))
                    .append(Text.literal(" команде!"));
            default -> Text.literal("Ты без команды.").formatted(Formatting.GRAY);
        };
    }
}