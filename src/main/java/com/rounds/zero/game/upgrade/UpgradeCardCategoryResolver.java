package com.rounds.zero.game.upgrade;

public final class UpgradeCardCategoryResolver {
    private UpgradeCardCategoryResolver() {
    }

    public static UpgradeCardCategory resolve(UpgradeCard card) {
        if (card == null) {
            return UpgradeCardCategory.WEAPON;
        }

        return switch (card.getId()) {
            case "ice_bullets", "poison_bullet", "poison_cloud", "blindness_bullets", "fire_shot", "triple_shot" ->
                    UpgradeCardCategory.BULLET_EFFECT;
            case "healing_field" -> UpgradeCardCategory.SHIELD_EFFECT;
            case "bogatyr", "your_mama", "tank" -> UpgradeCardCategory.HEALTH;
            default -> UpgradeCardCategory.WEAPON;
        };
    }
}
