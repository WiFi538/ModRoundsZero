package com.rounds.zero.game.team;

import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamVisualManager {

    private static final String RED_TEAM_NAME = "rounds_red";
    private static final String BLUE_TEAM_NAME = "rounds_blue";
    private static final String GREEN_TEAM_NAME = "rounds_green";
    private static final String YELLOW_TEAM_NAME = "rounds_yellow";

    public static void setupTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        createOrUpdateTeam(scoreboard, RED_TEAM_NAME, "Красные", Formatting.RED);
        createOrUpdateTeam(scoreboard, BLUE_TEAM_NAME, "Синие", Formatting.BLUE);
        createOrUpdateTeam(scoreboard, GREEN_TEAM_NAME, "Зеленые", Formatting.GREEN);
        createOrUpdateTeam(scoreboard, YELLOW_TEAM_NAME, "Желтые", Formatting.YELLOW);
    }

    private static void createOrUpdateTeam(Scoreboard scoreboard, String name, String displayName, Formatting color) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.addTeam(name);
        }

        team.setDisplayName(Text.literal(displayName));
        team.setColor(color);
        team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS);
    }

    public static void setPlayerTeam(MinecraftServer server, ServerPlayerEntity player, TeamId teamId) {
        Scoreboard scoreboard = server.getScoreboard();

        setupTeams(server);
        clearPlayerTeam(server, player);

        Team targetTeam = switch (teamId) {
            case RED -> scoreboard.getTeam(RED_TEAM_NAME);
            case BLUE -> scoreboard.getTeam(BLUE_TEAM_NAME);
            case GREEN -> scoreboard.getTeam(GREEN_TEAM_NAME);
            case YELLOW -> scoreboard.getTeam(YELLOW_TEAM_NAME);
            default -> null;
        };

        if (targetTeam != null) {
            scoreboard.addPlayerToTeam(player.getEntityName(), targetTeam);
        }
    }

    public static void clearPlayerTeam(MinecraftServer server, ServerPlayerEntity player) {
        Scoreboard scoreboard = server.getScoreboard();
        String playerName = player.getEntityName();

        Team currentTeam = scoreboard.getPlayerTeam(playerName);
        if (currentTeam != null) {
            scoreboard.removePlayerFromTeam(playerName, currentTeam);
        }
    }
}