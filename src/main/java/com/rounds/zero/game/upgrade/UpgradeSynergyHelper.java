package com.rounds.zero.game.upgrade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UpgradeSynergyHelper {
    private UpgradeSynergyHelper() {
    }

    public static boolean hasCard(List<UpgradeCard> cards, String cardId) {
        if (cards == null || cardId == null) {
            return false;
        }

        for (UpgradeCard card : cards) {
            if (card != null && cardId.equals(card.getId())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasAllCards(List<UpgradeCard> cards, String... cardIds) {
        if (cardIds == null || cardIds.length == 0) {
            return false;
        }

        Set<String> owned = new HashSet<>();
        if (cards != null) {
            for (UpgradeCard card : cards) {
                if (card != null) {
                    owned.add(card.getId());
                }
            }
        }

        for (String cardId : cardIds) {
            if (!owned.contains(cardId)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasAnyHealthTankCard(List<UpgradeCard> cards) {
        return hasCard(cards, "bogatyr") || hasCard(cards, "your_mama") || hasCard(cards, "tank");
    }
}
