package com.rounds.zero.game.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerUpgradeData {
    private final List<UpgradeCard> ownedUpgrades = new ArrayList<>();

    public void addUpgrade(UpgradeCard card) {
        ownedUpgrades.add(card);
    }

    public List<UpgradeCard> getOwnedUpgrades() {
        return Collections.unmodifiableList(ownedUpgrades);
    }

    public void clear() {
        ownedUpgrades.clear();
    }
}