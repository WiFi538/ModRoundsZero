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
        boolean hasSniper = false;

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
                case "triple_shot" -> stats.setProjectilesPerShot(Math.max(stats.getProjectilesPerShot(), 3));
                case "fire_shot" -> {
                    stats.setFireOnHitDurationTicks(Math.max(stats.getFireOnHitDurationTicks(), 100));
                    // Extra fire-hit damage so fire shot consistently feels stronger than vanilla burn ticks.
                    stats.setFireOnHitExtraDamage(Math.max(stats.getFireOnHitExtraDamage(), 4.0f));
                }
                case "sniper" -> hasSniper = true;
                case "cursed_bullet" -> {
                    stats.setCursedBullet(true);
                    stats.setCursedGlowDurationTicks(Math.max(stats.getCursedGlowDurationTicks(), 200));
                    stats.setCursedExplosionPower(Math.max(stats.getCursedExplosionPower(), 2.8f));
                }
                case "parasite" -> stats.setParasite(true);
                case "jackpot" -> {
                    stats.setJackpot(true);
                    stats.setJackpotChancePercent(Math.max(stats.getJackpotChancePercent(), 50));
                    stats.setJackpotDurationTicks(Math.max(stats.getJackpotDurationTicks(), 40));
                }
                case "thor" -> {
                    stats.setThor(true);
                    stats.setThorChancePercent(Math.max(stats.getThorChancePercent(), 15));
                    stats.setThorDamage(Math.max(stats.getThorDamage(), 6.0f));
                }
                case "under_speed" -> {
                    stats.setUnderSpeed(true);
                    stats.setUnderSpeedDurationTicks(Math.max(stats.getUnderSpeedDurationTicks(), 40));
                    stats.setUnderSpeedAmplifier(Math.max(stats.getUnderSpeedAmplifier(), 1));
                }
                case "dep" -> {
                    stats.setDep(true);
                    stats.setDepChancePercent(Math.max(stats.getDepChancePercent(), 50));
                    stats.setDepTargetBonusMultiplier(Math.max(stats.getDepTargetBonusMultiplier(), 0.5));
                    stats.setDepSelfBonusMultiplier(Math.max(stats.getDepSelfBonusMultiplier(), 0.2));
                }
                case "time_jump" -> {
                    stats.setTimeJump(true);
                    stats.setTimeJumpChancePercent(Math.max(stats.getTimeJumpChancePercent(), 15));
                }
                case "summoner" -> {
                    stats.setSummoner(true);
                    stats.setSummonerLimitPerPlayer(Math.max(stats.getSummonerLimitPerPlayer(), 6));
                    stats.setSummonerZombieDamage(Math.max(stats.getSummonerZombieDamage(), 6.0f));
                }
                case "bomb_shield" -> {
                    stats.setBombShield(true);
                    stats.setBombShieldDamage(Math.max(stats.getBombShieldDamage(), 10.0f));
                    stats.setBombShieldRadius(Math.max(stats.getBombShieldRadius(), 1.5));
                    stats.setBombShieldExtraCooldownTicks(Math.max(stats.getBombShieldExtraCooldownTicks(), 100L));
                }
                case "kaboom" -> {
                    stats.setKaboom(true);
                    stats.setKaboomDamage(Math.max(stats.getKaboomDamage(), 2.0f));
                    stats.setKaboomRadius(Math.max(stats.getKaboomRadius(), 1.5));
                }
                default -> {
                }
            }
        }

        if (hasSniper) {
            if (stats.getMaxAmmo() <= 5) {
                stats.setMaxAmmo(1);
            } else {
                stats.setMaxAmmo(stats.getMaxAmmo() - 5);
            }
        }

        if (iceBulletsCount > 0) {
            stats.setIceBulletDurationTicks(20 * iceBulletsCount + 20);
        }

        if (healingFieldCount > 0) {
            stats.setHealingFieldRadius(1);
            stats.setHealingFieldLifetimeTicks(25);
            stats.setHealingFieldEffectDurationTicks(50 + ((healingFieldCount - 1) * 10));
            stats.setHealingFieldAmplifier(1);
            stats.setHealingFieldCooldownTicks(160L);
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

        applySynergies(stats, cards, poisonBulletCount, poisonCloudCount, healingFieldCount);

        return clamp(stats);
    }

    private static void applySynergies(
            CombatStats stats,
            List<UpgradeCard> cards,
            int poisonBulletCount,
            int poisonCloudCount,
            int healingFieldCount
    ) {
        if (poisonBulletCount > 0 && poisonCloudCount > 0) {
            if (stats.getPoisonCloudRadius() > 0) {
                stats.setPoisonCloudRadius(stats.getPoisonCloudRadius() + 2);
            }
            if (stats.getPoisonBulletDurationTicks() > 0) {
                stats.setPoisonBulletDurationTicks(stats.getPoisonBulletDurationTicks() + 40);
            }
        }

        if (UpgradeSynergyHelper.hasAllCards(cards, "parasite", "summoner")) {
            stats.setParasiteSummonerSynergy(true);
        }

        if (UpgradeSynergyHelper.hasAllCards(cards, "dep", "jackpot")) {
            stats.setDepJackpotSynergy(true);
        }

        if (healingFieldCount > 0 && UpgradeSynergyHelper.hasAnyHealthTankCard(cards)) {
            if (stats.getHealingFieldLifetimeTicks() > 0) {
                stats.setHealingFieldLifetimeTicks(stats.getHealingFieldLifetimeTicks() + 40);
            }
            stats.setHealingFieldSurge(true);
        }

        if (UpgradeSynergyHelper.hasAllCards(cards, "fire_shot", "thor")) {
            stats.setFireGhostSynergy(true);
        }
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
        stats.setProjectilesPerShot(Math.max(1, stats.getProjectilesPerShot()));
        stats.setMaxAmmo(Math.max(1, stats.getMaxAmmo()));
        stats.setShotCooldownTicks(Math.max(1L, stats.getShotCooldownTicks()));
        stats.setReloadDurationTicks(Math.max(1L, stats.getReloadDurationTicks()));
        stats.setShieldCooldownTicks(Math.max(1L, stats.getShieldCooldownTicks()));
        stats.setShieldDurationTicks(Math.max(1L, stats.getShieldDurationTicks()));
        stats.setBulletDamage(Math.max(0.1, stats.getBulletDamage()));
        stats.setBulletSpeed(Math.max(0.1, stats.getBulletSpeed()));
        stats.setBulletSize(Math.max(0.25, stats.getBulletSize()));
        stats.setMaxHealth(Math.max(2.0, stats.getMaxHealth()));
        stats.setJackpotChancePercent(Math.max(0, Math.min(100, stats.getJackpotChancePercent())));
        stats.setThorChancePercent(Math.max(0, Math.min(100, stats.getThorChancePercent())));
        stats.setDepChancePercent(Math.max(0, Math.min(100, stats.getDepChancePercent())));
        stats.setTimeJumpChancePercent(Math.max(0, Math.min(100, stats.getTimeJumpChancePercent())));
        stats.setJackpotDurationTicks(Math.max(0, stats.getJackpotDurationTicks()));
        stats.setUnderSpeedDurationTicks(Math.max(0, stats.getUnderSpeedDurationTicks()));
        stats.setCursedGlowDurationTicks(Math.max(0, stats.getCursedGlowDurationTicks()));
        stats.setThorDamage(Math.max(0.0f, stats.getThorDamage()));
        stats.setCursedExplosionPower(Math.max(0.0f, stats.getCursedExplosionPower()));
        stats.setFireOnHitExtraDamage(Math.max(0.0f, stats.getFireOnHitExtraDamage()));
        stats.setSummonerLimitPerPlayer(Math.max(0, stats.getSummonerLimitPerPlayer()));
        stats.setSummonerZombieDamage(Math.max(0.0f, stats.getSummonerZombieDamage()));
        stats.setBombShieldDamage(Math.max(0.0f, stats.getBombShieldDamage()));
        stats.setBombShieldRadius(Math.max(0.0, stats.getBombShieldRadius()));
        stats.setBombShieldExtraCooldownTicks(Math.max(0L, stats.getBombShieldExtraCooldownTicks()));
        stats.setKaboomDamage(Math.max(0.0f, stats.getKaboomDamage()));
        stats.setKaboomRadius(Math.max(0.0, stats.getKaboomRadius()));
        return stats;
    }
}
