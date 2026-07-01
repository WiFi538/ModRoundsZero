package com.rounds.zero.network;

import com.rounds.zero.RoundsZero;
import com.rounds.zero.game.combat.CombatStats;
import com.rounds.zero.game.combat.PlayerCombatData;
import com.rounds.zero.game.upgrade.UpgradeCard;
import com.rounds.zero.game.upgrade.UpgradeEffectResolver;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlayerStatsSnapshot {
    public record CardEntry(String title, int count) {
    }

    private final List<CardEntry> cards;
    private final List<String> statLines;

    public PlayerStatsSnapshot(List<CardEntry> cards, List<String> statLines) {
        this.cards = List.copyOf(cards);
        this.statLines = List.copyOf(statLines);
    }

    public static PlayerStatsSnapshot create(ServerPlayerEntity player) {
        List<UpgradeCard> owned = RoundsZero.GAME_MANAGER.getOwnedUpgrades(player);
        CombatStats stats = UpgradeEffectResolver.resolve(owned);
        PlayerCombatData combatData = RoundsZero.GAME_MANAGER.getCombatManager().getOrCreate(player);

        return new PlayerStatsSnapshot(
                groupCards(owned),
                PlayerStatsFormatter.format(player, stats, combatData)
        );
    }

    private static List<CardEntry> groupCards(List<UpgradeCard> owned) {
        Map<String, CardEntry> grouped = new LinkedHashMap<>();

        for (UpgradeCard card : owned) {
            grouped.compute(card.getId(), (id, entry) -> {
                if (entry == null) {
                    return new CardEntry(card.getTitle(), 1);
                }

                return new CardEntry(entry.title(), entry.count() + 1);
            });
        }

        return new ArrayList<>(grouped.values());
    }

    public List<CardEntry> cards() {
        return cards;
    }

    public List<String> statLines() {
        return statLines;
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(cards.size());
        for (CardEntry card : cards) {
            buf.writeString(card.title());
            buf.writeVarInt(card.count());
        }

        buf.writeVarInt(statLines.size());
        for (String line : statLines) {
            buf.writeString(line);
        }
    }

    public static PlayerStatsSnapshot read(PacketByteBuf buf) {
        int cardCount = buf.readVarInt();
        List<CardEntry> cards = new ArrayList<>(cardCount);

        for (int index = 0; index < cardCount; index++) {
            cards.add(new CardEntry(buf.readString(), buf.readVarInt()));
        }

        int lineCount = buf.readVarInt();
        List<String> statLines = new ArrayList<>(lineCount);

        for (int index = 0; index < lineCount; index++) {
            statLines.add(buf.readString());
        }

        return new PlayerStatsSnapshot(cards, statLines);
    }
}
