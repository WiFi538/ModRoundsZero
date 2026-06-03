package com.rounds.zero.game;

import com.rounds.zero.game.arena.Arena;
import com.rounds.zero.game.event.RoundEventManager;
import com.rounds.zero.game.combat.CombatManager;
import com.rounds.zero.game.upgrade.UpgradeSynergyHelper;
import com.rounds.zero.game.combat.CombatStats;
import com.rounds.zero.game.team.TeamId;
import com.rounds.zero.game.team.TeamVisualManager;
import com.rounds.zero.game.upgrade.PlayerUpgradeData;
import com.rounds.zero.game.upgrade.UpgradeCard;
import com.rounds.zero.game.upgrade.UpgradeEffectResolver;
import com.rounds.zero.game.upgrade.UpgradeRegistry;
import com.rounds.zero.item.ModWeaponItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import com.rounds.zero.network.ModPackets;
import com.rounds.zero.game.scoreboard.MatchSidebarManager;
import net.minecraft.world.GameRules;

import java.util.*;

public class GameManager {
    public static final int UPGRADE_SELECTION_DELAY_TICKS = 40;

    private GameState gameState = GameState.WAITING;

    private final Map<UUID, TeamId> playerTeams = new HashMap<>();
    private final Map<TeamId, Integer> teamScores = new HashMap<>();
    private final Set<UUID> alivePlayers = new HashSet<>();

    private final Set<UUID> playersWaitingForUpgradeChoice = new HashSet<>();
    private final Map<UUID, List<UpgradeCard>> currentUpgradeOffers = new HashMap<>();
    private final Map<UUID, Long> upgradeChoiceUnlockTick = new HashMap<>();
    private final Map<UUID, PendingUpgradeScreen> pendingUpgradeScreens = new HashMap<>();
    private final Map<UUID, PlayerUpgradeData> playerUpgradeData = new HashMap<>();

    private record PendingUpgradeScreen(List<UpgradeCard> offer, long sendAtTick, long choiceUnlockTick) {
    }

    private TeamId pendingUpgradeLoserTeam = TeamId.NONE;
    private TeamId pendingUpgradeWinnerTeam = TeamId.NONE;

    private final List<Arena> arenas = new ArrayList<>();
    private final Random random = new Random();
    private final boolean debugMode = true;
    private final CombatManager combatManager = new CombatManager();
    private final RoundEventManager roundEventManager = new RoundEventManager();
    private int roundsToWin = 5;

    private Arena currentArena;
    private BlockPos lobbySpawn = new BlockPos(0, -34, 0);

    private Integer savedRandomTickSpeed = null;

    public GameManager() {
        for (TeamId teamId : TeamId.values()) {
            if (teamId != TeamId.NONE) {
                teamScores.put(teamId, 0);
            }
        }
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public RoundEventManager getRoundEventManager() {
        return roundEventManager;
    }

    public boolean playerHasParasiteSummonerSynergy(ServerPlayerEntity player) {
        return UpgradeSynergyHelper.hasAllCards(getOwnedUpgrades(player), "parasite", "summoner");
    }

    public void applyPersistentUpgradeEffects(ServerPlayerEntity player) {
    }

    public boolean isPlayerShieldActive(ServerPlayerEntity player) {
        return combatManager.isShieldActive(player);
    }

    public boolean isPlayerRoundWinnerInvulnerable(ServerPlayerEntity player) {
        if (gameState != GameState.UPGRADE_SELECTION) {
            return false;
        }

        TeamId teamId = getPlayerTeam(player);
        return teamId != TeamId.NONE && teamId == pendingUpgradeWinnerTeam;
    }

    public void tickCombat(MinecraftServer server) {
        long now = server.getOverworld().getTime();

        if (gameState == GameState.UPGRADE_SELECTION) {
            processPendingUpgradeScreens(server, now);
        }

        if (now % 20L == 0L) {
            enforceGameModes(server);
            enforceSpectatorGlow(server);
        }

        if (gameState == GameState.ROUND_ACTIVE) {
            roundEventManager.tick(server, this, now);
        }
        combatManager.tick(server);
    }

    private void enforceSpectatorGlow(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.GLOWING,
                        60,
                        0,
                        true,
                        false,
                        false
                ));
            } else {
                StatusEffectInstance glowing = player.getStatusEffect(StatusEffects.GLOWING);
                if (glowing != null && glowing.isAmbient()) {
                    player.removeStatusEffect(StatusEffects.GLOWING);
                }
            }
        }
    }

    private void processPendingUpgradeScreens(MinecraftServer server, long now) {
        Iterator<Map.Entry<UUID, PendingUpgradeScreen>> iterator = pendingUpgradeScreens.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingUpgradeScreen> entry = iterator.next();
            PendingUpgradeScreen pending = entry.getValue();

            if (now < pending.sendAtTick()) {
                continue;
            }

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player != null && gameState == GameState.UPGRADE_SELECTION) {
                ModPackets.sendUpgradeScreen(player, pending.offer(), pending.choiceUnlockTick());
            }

            iterator.remove();
        }
    }

    private void clearUpgradeSelectionScheduling() {
        upgradeChoiceUnlockTick.clear();
        pendingUpgradeScreens.clear();
    }

    public boolean shouldBlockWorldInteraction(ServerPlayerEntity player) {
        return hasTeam(player);
    }

    private void enforceGameModes(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!hasTeam(player)) {
                if (gameState == GameState.WAITING && player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
                    player.changeGameMode(GameMode.ADVENTURE);
                }
                continue;
            }

            switch (gameState) {
                case WAITING -> {
                    if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
                        player.changeGameMode(GameMode.ADVENTURE);
                    }
                }
                case ROUND_ACTIVE -> {
                    if (!isAliveInRound(player)) {
                        if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                            player.changeGameMode(GameMode.SPECTATOR);
                        }
                    } else if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
                        player.changeGameMode(GameMode.ADVENTURE);
                    }
                }
                case UPGRADE_SELECTION -> {
                    if (!isAliveInRound(player)) {
                        if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                            player.changeGameMode(GameMode.SPECTATOR);
                        }
                    } else if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
                        player.changeGameMode(GameMode.ADVENTURE);
                    }
                }
                default -> {
                }
            }
        }
    }

    public void handleShieldRequest(ServerPlayerEntity player) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        if (!hasTeam(player) || !isAliveInRound(player) || player.isSpectator()) {
            return;
        }

        combatManager.handleShieldRequest(player);
    }

    public void handleReloadRequest(ServerPlayerEntity player) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        if (!hasTeam(player) || !isAliveInRound(player) || player.isSpectator()) {
            return;
        }

        combatManager.handleReloadRequest(player);
    }

    public void handleShootRequest(ServerPlayerEntity player) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        if (!hasTeam(player) || !isAliveInRound(player) || player.isSpectator()) {
            return;
        }

        combatManager.handleShootRequest(player);
    }

    public BlockPos getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(BlockPos lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setPlayerTeam(ServerPlayerEntity player, TeamId teamId) {
        playerTeams.put(player.getUuid(), teamId);
        playerUpgradeData.computeIfAbsent(player.getUuid(), uuid -> new PlayerUpgradeData());
    }

    public void clearPlayerTeam(ServerPlayerEntity player) {
        playerTeams.remove(player.getUuid());
        alivePlayers.remove(player.getUuid());
        playersWaitingForUpgradeChoice.remove(player.getUuid());
        currentUpgradeOffers.remove(player.getUuid());
        combatManager.removePlayer(player);
    }

    public TeamId getPlayerTeam(ServerPlayerEntity player) {
        return playerTeams.getOrDefault(player.getUuid(), TeamId.NONE);
    }

    public boolean hasTeam(ServerPlayerEntity player) {
        return getPlayerTeam(player) != TeamId.NONE;
    }

    public int getPlayerCountInTeam(TeamId teamId) {
        int count = 0;

        for (TeamId playerTeam : playerTeams.values()) {
            if (playerTeam == teamId) {
                count++;
            }
        }
        return count;
    }

    public List<ServerPlayerEntity> getPlayersInTeam(MinecraftServer server, TeamId teamId) {
        List<ServerPlayerEntity> players = new ArrayList<>();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (getPlayerTeam(player) == teamId) {
                players.add(player);
            }
        }

        return players;
    }

    public boolean isAliveInRound(ServerPlayerEntity player) {
        return alivePlayers.contains(player.getUuid());
    }

    public int getAliveCountInTeam(TeamId teamId) {
        int count = 0;

        for (UUID uuid : alivePlayers) {
            TeamId playerTeam = playerTeams.getOrDefault(uuid, TeamId.NONE);
            if (playerTeam == teamId) {
                count++;
            }
        }

        return count;
    }

    public boolean isWaitingForUpgradeChoice(ServerPlayerEntity player) {
        return playersWaitingForUpgradeChoice.contains(player.getUuid());
    }

    public int getWaitingForUpgradeChoiceCount() {
        return playersWaitingForUpgradeChoice.size();
    }

    public TeamId getPendingUpgradeLoserTeam() {
        return pendingUpgradeLoserTeam;
    }

    public TeamId getPendingUpgradeWinnerTeam() {
        return pendingUpgradeWinnerTeam;
    }

    public List<UpgradeCard> getCurrentUpgradeOffer(ServerPlayerEntity player) {
        return currentUpgradeOffers.getOrDefault(player.getUuid(), Collections.emptyList());
    }

    public List<UpgradeCard> getOwnedUpgrades(ServerPlayerEntity player) {
        PlayerUpgradeData data = playerUpgradeData.get(player.getUuid());
        if (data == null) {
            return Collections.emptyList();
        }

        return data.getOwnedUpgrades();
    }

    public boolean grantUpgrade(ServerPlayerEntity player, String cardId) {
        UpgradeCard card = UpgradeRegistry.findById(cardId);
        if (card == null) {
            return false;
        }

        playerUpgradeData.computeIfAbsent(player.getUuid(), uuid -> new PlayerUpgradeData()).addUpgrade(card);

        if (gameState == GameState.ROUND_ACTIVE && hasTeam(player) && isAliveInRound(player)) {
            CombatStats resolvedStats = UpgradeEffectResolver.resolve(getOwnedUpgrades(player));
            combatManager.preparePlayerForNewRound(player, resolvedStats);
            applyPersistentUpgradeEffects(player);
        }

        return true;
    }

    public void forceFinishUpgradeSelection(MinecraftServer server) {
        if (gameState != GameState.UPGRADE_SELECTION) {
            return;
        }

        playersWaitingForUpgradeChoice.clear();
        currentUpgradeOffers.clear();
        clearUpgradeSelectionScheduling();
        pendingUpgradeLoserTeam = TeamId.NONE;
        pendingUpgradeWinnerTeam = TeamId.NONE;

        startNextRound(server);
    }

    public boolean submitUpgradeChoice(MinecraftServer server, ServerPlayerEntity player, int optionIndex) {
        if (gameState != GameState.UPGRADE_SELECTION) {
            player.sendMessage(Text.literal("Сейчас нет фазы выбора улучшений.").formatted(Formatting.RED), false);
            return false;
        }

        if (!isWaitingForUpgradeChoice(player)) {
            player.sendMessage(Text.literal("Тебе сейчас не нужно выбирать улучшение.").formatted(Formatting.RED), false);
            return false;
        }

        List<UpgradeCard> offer = currentUpgradeOffers.get(player.getUuid());
        if (offer == null || offer.isEmpty()) {
            player.sendMessage(Text.literal("Для тебя не найдено предложение улучшений.").formatted(Formatting.RED), false);
            return false;
        }

        if (optionIndex < 1 || optionIndex > offer.size()) {
            player.sendMessage(Text.literal("Неверный номер карты.").formatted(Formatting.RED), false);
            return false;
        }

        long now = server.getOverworld().getTime();
        Long unlockTick = upgradeChoiceUnlockTick.get(player.getUuid());
        if (unlockTick != null && now < unlockTick) {
            double secondsLeft = (unlockTick - now) / 20.0;
            player.sendMessage(
                    Text.literal(String.format("Подожди %.1f сек. перед выбором карты.", secondsLeft))
                            .formatted(Formatting.YELLOW),
                    false
            );
            return false;
        }

        UpgradeCard selectedCard = offer.get(optionIndex - 1);

        playerUpgradeData
                .computeIfAbsent(player.getUuid(), uuid -> new PlayerUpgradeData())
                .addUpgrade(selectedCard);

        playersWaitingForUpgradeChoice.remove(player.getUuid());
        currentUpgradeOffers.remove(player.getUuid());
        upgradeChoiceUnlockTick.remove(player.getUuid());
        pendingUpgradeScreens.remove(player.getUuid());

        player.sendMessage(
                Text.literal("Ты выбрал карту: ")
                        .append(Text.literal(selectedCard.getTitle()).formatted(Formatting.GOLD)),
                false
        );

        applyPersistentUpgradeEffects(player);

        advanceIfUpgradeSelectionDone(server);

        return true;
    }

    private void advanceIfUpgradeSelectionDone(MinecraftServer server) {
        if (gameState != GameState.UPGRADE_SELECTION) {
            return;
        }

        if (!playersWaitingForUpgradeChoice.isEmpty()) {
            return;
        }

        broadcastMessage(server, Text.literal("Все проигравшие игроки выбрали улучшения. Следующий раунд начинается!").formatted(Formatting.GREEN));
        pendingUpgradeLoserTeam = TeamId.NONE;
        pendingUpgradeWinnerTeam = TeamId.NONE;
        currentUpgradeOffers.clear();
        clearUpgradeSelectionScheduling();
        startNextRound(server);
    }

    public void handlePlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        clearPlayerTeam(player);
        TeamVisualManager.clearPlayerTeam(server, player);
        sendPlayerToLobby(server, player);
    }

    public void handlePlayerDisconnect(MinecraftServer server, ServerPlayerEntity player) {
        if (!hasTeam(player)) {
            return;
        }

        boolean wasAliveInRound = alivePlayers.contains(player.getUuid());
        boolean wasWaitingForUpgrade = playersWaitingForUpgradeChoice.contains(player.getUuid());

        clearPlayerTeam(player);
        TeamVisualManager.clearPlayerTeam(server, player);

        if (wasAliveInRound) {
            checkRoundWinByElimination(server);
        }

        if (wasWaitingForUpgrade) {
            advanceIfUpgradeSelectionDone(server);
        }
    }

    public void sendPlayerToLobby(MinecraftServer server, ServerPlayerEntity player) {
        combatManager.resetPlayerToDefault(player);
        CombatManager.clearCursedState(player);

        player.changeGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevel(20.0f);
        player.clearStatusEffects();
        player.setFireTicks(0);

        player.teleport(
                server.getOverworld(),
                lobbySpawn.getX() + 0.5,
                lobbySpawn.getY(),
                lobbySpawn.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
    }

    public void handlePlayerDeath(MinecraftServer server, ServerPlayerEntity player) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        TeamId teamId = getPlayerTeam(player);
        if (teamId == TeamId.NONE) {
            return;
        }

        if (!alivePlayers.contains(player.getUuid())) {
            return;
        }

        alivePlayers.remove(player.getUuid());
        combatManager.clearTemporaryState(player);
        CombatManager.clearCursedState(player);
        checkRoundWinByElimination(server);
    }

    private BlockPos getSpawnForTeam(Arena arena, TeamId teamId) {
        return switch (teamId) {
            case RED -> arena.getRedSpawn();
            case BLUE -> arena.getBlueSpawn();
            case GREEN -> arena.getGreenSpawn();
            case YELLOW -> arena.getYellowSpawn();
            default -> null;
        };
    }

    public void handlePlayerRespawn(ServerPlayerEntity player) {
        if (gameState != GameState.ROUND_ACTIVE && gameState != GameState.UPGRADE_SELECTION) {
            return;
        }

        TeamId teamId = getPlayerTeam(player);
        if (teamId == TeamId.NONE) {
            return;
        }

        if (alivePlayers.contains(player.getUuid())) {
            return;
        }

        player.changeGameMode(GameMode.SPECTATOR);

        if (currentArena != null) {
            BlockPos targetPos = getSpawnForTeam(currentArena, teamId);

            if (targetPos != null) {
                player.teleport(
                        player.getServer().getOverworld(),
                        targetPos.getX() + 0.5,
                        targetPos.getY() + 1,
                        targetPos.getZ() + 0.5,
                        player.getYaw(),
                        player.getPitch()
                );
            }
        }

        if (gameState == GameState.UPGRADE_SELECTION && isWaitingForUpgradeChoice(player)) {
            player.sendMessage(
                    Text.literal("Твоя команда проиграла раунд. Открой список карт командой /rounds upgrades")
                            .formatted(Formatting.GOLD),
                    false
            );
            return;
        }

        player.sendMessage(
                Text.literal("Ты выбыл из раунда и теперь наблюдаешь за боем.")
                        .formatted(Formatting.GRAY),
                false
        );
    }

    // regeneration off
    private void setNaturalRegeneration(MinecraftServer server, boolean enabled) {
        server.getWorlds().forEach(world ->
                world.getGameRules().get(GameRules.NATURAL_REGENERATION).set(enabled, server)
        );
    }

    private void setPlantGrowthEnabled(MinecraftServer server, boolean enabled) {
        if (!enabled) {
            if (savedRandomTickSpeed == null) {
                savedRandomTickSpeed = server.getOverworld()
                        .getGameRules()
                        .get(net.minecraft.world.GameRules.RANDOM_TICK_SPEED)
                        .get();
            }

            server.getWorlds().forEach(world ->
                    world.getGameRules().get(net.minecraft.world.GameRules.RANDOM_TICK_SPEED).set(0, server)
            );
            return;
        }

        int valueToRestore = savedRandomTickSpeed != null ? savedRandomTickSpeed : 3;

        server.getWorlds().forEach(world ->
                world.getGameRules().get(net.minecraft.world.GameRules.RANDOM_TICK_SPEED).set(valueToRestore, server)
        );

        savedRandomTickSpeed = null;
    }

    private void checkRoundWinByElimination(MinecraftServer server) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        List<TeamId> aliveTeams = new ArrayList<>();
        for (TeamId teamId : TeamId.values()) {
            if (teamId == TeamId.NONE) continue;
            if (getAliveCountInTeam(teamId) > 0) {
                aliveTeams.add(teamId);
            }
        }

        if (aliveTeams.size() == 1) {
            endRound(server, aliveTeams.get(0));
        }
    }

    private void endRound(MinecraftServer server, TeamId winnerTeam) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        setGameState(GameState.ROUND_END);
        roundEventManager.onRoundEnd(server, this);
        combatManager.clearRoundEntities(server);
        combatManager.clearAllCursedEntities(server);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (hasTeam(player)) {
                combatManager.clearTemporaryState(player);
            }
        }

        addScore(winnerTeam, 1);
        MatchSidebarManager.updateSidebar(server);

        broadcastMessage(server,
                Text.literal("Раунд выиграла команда ")
                        .append(getTeamNameText(winnerTeam))
                        .append(Text.literal("!"))
        );

        if (hasTeamWonMatch(winnerTeam)) {
            endMatch(server, winnerTeam);
            return;
        }

        beginUpgradeSelection(server, winnerTeam);
    }

    private void beginUpgradeSelection(MinecraftServer server, TeamId winnerTeam) {
        pendingUpgradeWinnerTeam = winnerTeam;
        pendingUpgradeLoserTeam = TeamId.NONE;
        playersWaitingForUpgradeChoice.clear();
        currentUpgradeOffers.clear();
        clearUpgradeSelectionScheduling();

        long now = server.getOverworld().getTime();
        long sendAtTick = now + UPGRADE_SELECTION_DELAY_TICKS;
        long choiceUnlockTick = sendAtTick;

        List<TeamId> loserTeams = new ArrayList<>();

        for (TeamId teamId : TeamId.values()) {
            if (teamId == TeamId.NONE || teamId == winnerTeam) {
                continue;
            }

            if (getPlayerCountInTeam(teamId) > 0) {
                loserTeams.add(teamId);
            }
        }

        for (TeamId loserTeam : loserTeams) {
            for (ServerPlayerEntity player : getPlayersInTeam(server, loserTeam)) {
                playersWaitingForUpgradeChoice.add(player.getUuid());
                List<UpgradeCard> offer = generateUpgradeOffer(5);
                currentUpgradeOffers.put(player.getUuid(), offer);
                upgradeChoiceUnlockTick.put(player.getUuid(), choiceUnlockTick);
                pendingUpgradeScreens.put(player.getUuid(), new PendingUpgradeScreen(offer, sendAtTick, choiceUnlockTick));
                player.changeGameMode(GameMode.SPECTATOR);
            }
        }

        setGameState(GameState.UPGRADE_SELECTION);

        broadcastMessage(server,
                Text.literal("Фаза выбора улучшений для проигравших команд. Победитель раунда: ")
                        .append(getTeamNameText(winnerTeam))
        );
    }

    private List<UpgradeCard> generateUpgradeOffer(int count) {
        List<UpgradeCard> pool = new ArrayList<>(UpgradeRegistry.getAllCards());
        Collections.shuffle(pool, random);

        int resultSize = Math.min(count, pool.size());
        return new ArrayList<>(pool.subList(0, resultSize));
    }

    public int getRoundsToWin() {
        return roundsToWin;
    }

    public void setRoundsToWin(int roundsToWin) {
        if (roundsToWin < 1 || roundsToWin > 20) {
            throw new IllegalArgumentException("roundsToWin must be between 1 and 20");
        }

        this.roundsToWin = roundsToWin;
    }

    public boolean hasTeamWonMatch(TeamId teamId) {
        return getTeamScore(teamId) >= roundsToWin;
    }

    public int getTeamScore(TeamId teamId) {
        return teamScores.getOrDefault(teamId, 0);
    }

    public void addScore(TeamId teamId, int amount) {
        int currentScore = teamScores.getOrDefault(teamId, 0);
        teamScores.put(teamId, currentScore + amount);
    }

    public void resetScores() {
        for (TeamId teamId : TeamId.values()) {
            if (teamId != TeamId.NONE) {
                teamScores.put(teamId, 0);
            }
        }
    }

    public Arena getCurrentArena() {
        return currentArena;
    }

    public void setCurrentArena(Arena currentArena) {
        this.currentArena = currentArena;
    }

    public void addArena(Arena arena) {
        arenas.add(arena);
    }

    public Arena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(arenas.size());
        return arenas.get(randomIndex);
    }

    public boolean canStartGame() {
        int activeTeams = 0;

        for (TeamId teamId : TeamId.values()) {
            if (teamId == TeamId.NONE) {
                continue;
            }

            if (getPlayerCountInTeam(teamId) > 0) {
                activeTeams++;
            }
        }

        if (debugMode) {
            return activeTeams >= 1;
        }

        return activeTeams >= 2;
    }

    public void startMatch(MinecraftServer server) {
        resetScores();
        pendingUpgradeLoserTeam = TeamId.NONE;
        pendingUpgradeWinnerTeam = TeamId.NONE;
        playersWaitingForUpgradeChoice.clear();
        currentUpgradeOffers.clear();
        setNaturalRegeneration(server, false);
        setPlantGrowthEnabled(server, false);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            playerUpgradeData.put(player.getUuid(), new PlayerUpgradeData());
        }

        startNextRound(server);
    }

    public void debugWinRound(MinecraftServer server, TeamId winnerTeam) {
        if (gameState != GameState.ROUND_ACTIVE) {
            return;
        }

        endRound(server, winnerTeam);
    }

    public void endMatch(MinecraftServer server, TeamId winnerTeam) {
        setGameState(GameState.MATCH_END);

        broadcastMessage(server,
                Text.literal("Матч выиграла команда ")
                        .append(getTeamNameText(winnerTeam))
                        .append(Text.literal("!"))
        );

        alivePlayers.clear();
        playersWaitingForUpgradeChoice.clear();
        currentUpgradeOffers.clear();
        clearUpgradeSelectionScheduling();
        playerUpgradeData.clear();
        pendingUpgradeLoserTeam = TeamId.NONE;
        pendingUpgradeWinnerTeam = TeamId.NONE;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendPlayerToLobby(server, player);
        }

        MatchSidebarManager.removeSidebar(server);
        resetScores();
        setCurrentArena(null);
        setGameState(GameState.WAITING);
        setNaturalRegeneration(server, true);
    }

    private void broadcastMessage(MinecraftServer server, Text message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(message, false);
        }
    }

    private MutableText getTeamNameText(TeamId teamId) {
        return switch (teamId) {
            case RED -> Text.literal("КРАСНЫЕ").formatted(Formatting.RED);
            case BLUE -> Text.literal("СИНИЕ").formatted(Formatting.BLUE);
            case GREEN -> Text.literal("ЗЕЛЕНЫЕ").formatted(Formatting.GREEN);
            case YELLOW -> Text.literal("ЖЕЛТЫЕ").formatted(Formatting.YELLOW);
            default -> Text.literal("NONE").formatted(Formatting.GRAY);
        };
    }

    public void startNextRound(MinecraftServer server) {
        Arena randomArena = getRandomArena();
        if (randomArena == null) {
            return;
        }

        setCurrentArena(randomArena);
        setGameState(GameState.ROUND_ACTIVE);
        combatManager.clearRoundEntities(server);
        roundEventManager.onRoundStart(server, this);

        alivePlayers.clear();
        playersWaitingForUpgradeChoice.clear();
        currentUpgradeOffers.clear();
        clearUpgradeSelectionScheduling();
        pendingUpgradeLoserTeam = TeamId.NONE;
        pendingUpgradeWinnerTeam = TeamId.NONE;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            TeamId teamId = getPlayerTeam(player);

            if (teamId == TeamId.NONE) {
                continue;
            }

            alivePlayers.add(player.getUuid());

            CombatStats resolvedStats = UpgradeEffectResolver.resolve(getOwnedUpgrades(player));
            combatManager.preparePlayerForNewRound(player, resolvedStats);

            player.changeGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getMaxHealth());
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(20.0f);
            player.clearStatusEffects();
            roundEventManager.applyEventEffects(player);
            applyPersistentUpgradeEffects(player);
            player.setFireTicks(0);
            player.getInventory().clear();
            player.getInventory().insertStack(ModWeaponItems.createPistol());

            BlockPos spawn = getSpawnForTeam(randomArena, teamId);
            if (spawn != null) {
                player.teleport(
                        server.getOverworld(),
                        spawn.getX() + 0.5,
                        spawn.getY(),
                        spawn.getZ() + 0.5,
                        player.getYaw(),
                        player.getPitch()
                );
            }
        }
    }
}