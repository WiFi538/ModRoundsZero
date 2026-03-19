package com.rounds.zero.game.scoreboard;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.team.TeamId;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MatchSidebarManager {

    private static final String OBJECTIVE_NAME = "rounds_sidebar";

    private static final String RED_ENTRY = "§0";
    private static final String BLUE_ENTRY = "§1";

    private static final String RED_LINE_TEAM = "rounds_sidebar_red";
    private static final String BLUE_LINE_TEAM = "rounds_sidebar_blue";

    public static void setupSidebar(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();

        ScoreboardObjective oldObjective = scoreboard.getNullableObjective(OBJECTIVE_NAME);
        if (oldObjective != null) {
            scoreboard.removeObjective(oldObjective);
        }

        ScoreboardObjective objective = scoreboard.addObjective(
                OBJECTIVE_NAME,
                ScoreboardCriterion.DUMMY,
                Text.literal("Rounds до " + RoundsZero.GAME_MANAGER.getRoundsToWin()).formatted(Formatting.GOLD),
                ScoreboardCriterion.RenderType.INTEGER
        );

        scoreboard.setObjectiveSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID, objective);

        setupLineTeam(scoreboard, RED_LINE_TEAM, RED_ENTRY, "Красные - ", Formatting.RED);
        setupLineTeam(scoreboard, BLUE_LINE_TEAM, BLUE_ENTRY, "Синие - ", Formatting.BLUE);

        updateSidebar(server);
    }

    public static void updateSidebar(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(OBJECTIVE_NAME);

        if (objective == null) {
            return;
        }

        objective.setDisplayName(
                Text.literal("Rounds до " + RoundsZero.GAME_MANAGER.getRoundsToWin()).formatted(Formatting.GOLD)
        );

        scoreboard.getPlayerScore(RED_ENTRY, objective).setScore(RoundsZero.GAME_MANAGER.getTeamScore(TeamId.RED));
        scoreboard.getPlayerScore(BLUE_ENTRY, objective).setScore(RoundsZero.GAME_MANAGER.getTeamScore(TeamId.BLUE));

        scoreboard.setObjectiveSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID, objective);
    }

    public static void removeSidebar(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(OBJECTIVE_NAME);

        if (objective != null) {
            scoreboard.removeObjective(objective);
        }
    }

    private static void setupLineTeam(ServerScoreboard scoreboard, String teamName, String entry, String prefix, Formatting color) {
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.addTeam(teamName);
        }

        team.setColor(color);
        team.setPrefix(Text.literal(prefix).formatted(color));

        if (scoreboard.getPlayerTeam(entry) != team) {
            scoreboard.addPlayerToTeam(entry, team);
        }
    }
}