package com.rounds.zero.game.upgrade;

import com.rounds.zero.game.combat.CombatStats;

import java.util.List;

public final class UpgradeEffectResolver {
    private UpgradeEffectResolver() {
    }

    public static CombatStats resolve(List<UpgradeCard> cards) {
        CombatStats stats = CombatStats.createDefault();

        if (cards == null || cards.isEmpty()) {
            return clamp(stats);
        }

        int iceBulletsCount = 0;
        int healingFieldCount = 0;
        int poisonBulletCount = 0;
        int poisonCloudCount = 0;
        int blindnessCount = 0;

        for (UpgradeCard card : cards) {
            if (card == null) {
                continue;
            }

            applyNumericPart(stats, card);

            switch (card.getId()) {
                case "ice_bullets" -> iceBulletsCount++;
                case "healing_field" -> healingFieldCount++;
                case "poison_bullet" -> poisonBulletCount++;
                case "poison_cloud" -> poisonCloudCount++;
                case "blindness_bullets" -> blindnessCount++;
                default -> {
                }
            }
        }

        if (iceBulletsCount > 0) {
            stats.setIceBulletDurationTicks(20 * iceBulletsCount);
        }

        if (healingFieldCount > 0) {
            stats.setHealingFieldRadius(2);
            stats.setHealingFieldLifetimeTicks(40);
            stats.setHealingFieldEffectDurationTicks(100 + ((healingFieldCount - 1) * 20));
            stats.setHealingFieldAmplifier(2);
        }

        if (poisonBulletCount > 0) {
            stats.setPoisonBulletDurationTicks(72 + ((poisonBulletCount - 1) * 24));
            stats.setPoisonBulletAmplifier(2);
        }

        if (poisonCloudCount > 0) {
            stats.setPoisonCloudRadius(3);
            stats.setPoisonCloudLifetimeTicks(60 + ((poisonCloudCount - 1) * 20));
            stats.setPoisonCloudEffectDurationTicks(72);
            stats.setPoisonCloudAmplifier(1);
        }

        if (blindnessCount > 0) {
            stats.setBlindnessChancePercent(30);
            stats.setBlindnessDurationTicks(40 + ((blindnessCount - 1) * 20));
        }

        return clamp(stats);
    }

    private static void applyNumericPart(CombatStats stats, UpgradeCard card) {
        stats.setMaxAmmo(stats.getMaxAmmo() + card.getFlatAmmoBonus());

        stats.setBulletDamage(stats.getBulletDamage() * card.getDamageMultiplier());
        stats.setBulletSpeed(stats.getBulletSpeed() * card.getBulletSpeedMultiplier());
        stats.setBulletSize(stats.getBulletSize() * card.getBulletSizeMultiplier());
        stats.setMaxHealth(stats.getMaxHealth() * card.getMaxHealthMultiplier());

        stats.setShotCooldownTicks(applyFireRatePercent(stats.getShotCooldownTicks(), card.getFireRatePercent()));
        stats.setShieldCooldownTicks(applyPercentToDuration(stats.getShieldCooldownTicks(), card.getShieldCooldownPercent()));
        stats.setShieldCooldownTicks(stats.getShieldCooldownTicks() + card.getShieldCooldownTicksFlat());
        stats.setReloadDurationTicks(applyPercentToDuration(stats.getReloadDurationTicks(), card.getReloadPercent()));
        stats.setReloadDurationTicks(stats.getReloadDurationTicks() + card.getReloadTicksFlat());
    }

    private static long applyFireRatePercent(long currentCooldownTicks, double fireRatePercent) {
        double result;

        if (fireRatePercent >= 0.0) {
            result = currentCooldownTicks / (1.0 + fireRatePercent / 100.0);
        } else {
            result = currentCooldownTicks * (1.0 + Math.abs(fireRatePercent) / 100.0);
        }

        return Math.max(1L, Math.round(result));
    }

    private static long applyPercentToDuration(long currentTicks, double percent) {
        double result = currentTicks * (1.0 + percent / 100.0);
        return Math.max(1L, Math.round(result));
    }

    private static CombatStats clamp(CombatStats stats) {
        stats.setMaxAmmo(Math.max(1, stats.getMaxAmmo()));
        stats.setShotCooldownTicks(Math.max(1L, stats.getShotCooldownTicks()));
        stats.setReloadDurationTicks(Math.max(1L, stats.getReloadDurationTicks()));
        stats.setShieldCooldownTicks(Math.max(1L, stats.getShieldCooldownTicks()));
        stats.setShieldDurationTicks(Math.max(1L, stats.getShieldDurationTicks()));
        stats.setBulletDamage(Math.max(0.1, stats.getBulletDamage()));
        stats.setBulletSpeed(Math.max(0.1, stats.getBulletSpeed()));
        stats.setBulletSize(Math.max(0.25, stats.getBulletSize()));
        stats.setMaxHealth(Math.max(2.0, stats.getMaxHealth()));
        return stats;
    }
}
