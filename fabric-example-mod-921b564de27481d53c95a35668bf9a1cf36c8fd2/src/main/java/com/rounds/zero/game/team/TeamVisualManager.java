package com.rounds.zero.game.team;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class TeamVisualManager {

    private static final String RED_TEAM_NAME = "rounds_red";
    private static final String BLUE_TEAM_NAME = "rounds_blue";

    public static void setupTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        Team redTeam = scoreboard.getTeam(RED_TEAM_NAME);
        if (redTeam == null) {
            redTeam = scoreboard.addTeam(RED_TEAM_NAME);
            redTeam.setDisplayName(net.minecraft.text.Text.literal("Красные"));
            redTeam.setColor(Formatting.RED);
        }

        Team blueTeam = scoreboard.getTeam(BLUE_TEAM_NAME);
        if (blueTeam == null) {
            blueTeam = scoreboard.addTeam(BLUE_TEAM_NAME);
            blueTeam.setDisplayName(net.minecraft.text.Text.literal("Синие"));
            blueTeam.setColor(Formatting.BLUE);
        }
    }

    public static void setPlayerTeam(MinecraftServer server, ServerPlayerEntity player, TeamId teamId) {
        Scoreboard scoreboard = server.getScoreboard();

        setupTeams(server);
        clearPlayerTeam(server, player);

        if (teamId == TeamId.RED) {
            Team redTeam = scoreboard.getTeam(RED_TEAM_NAME);
            if (redTeam != null) {
                scoreboard.addPlayerToTeam(player.getEntityName(), redTeam);
            }
        } else if (teamId == TeamId.BLUE) {
            Team blueTeam = scoreboard.getTeam(BLUE_TEAM_NAME);
            if (blueTeam != null) {
                scoreboard.addPlayerToTeam(player.getEntityName(), blueTeam);
            }
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