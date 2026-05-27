package com.rounds.zero.game.upgrade;

public final class UpgradeCardCategoryResolver {
    private UpgradeCardCategoryResolver() {
    }

    public static UpgradeCardCategory resolve(UpgradeCard card) {
        if (card == null) {
            return UpgradeCardCategory.WEAPON;
        }

        return switch (card.getId()) {
            case "ice_bullets", "poison_bullet", "poison_cloud", "blindness_bullets", "fire_shot", "triple_shot",
                 "cursed_bullet", "parasite", "jackpot", "thor", "under_speed", "dep", "time_jump", "kaboom" ->
                    UpgradeCardCategory.BULLET_EFFECT;
            case "healing_field", "summoner", "bomb_shield" -> UpgradeCardCategory.SHIELD_EFFECT;
            case "bogatyr", "your_mama", "tank", "ghost_rider" -> UpgradeCardCategory.HEALTH;
            default -> UpgradeCardCategory.WEAPON;
        };
    }
}
