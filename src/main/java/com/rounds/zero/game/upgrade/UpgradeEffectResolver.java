package com.rounds.zero.game.upgrade;

import com.rounds.zero.game.combat.CombatStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UpgradeEffectResolver {
    /** Minimum reload / shield timings — 1 second (20 ticks at 20 TPS). */
    private static final long MIN_COMBAT_DURATION_TICKS = 20L;

    private UpgradeEffectResolver() {
    }

    public static CombatStats resolve(List<UpgradeCard> cards) {
        CombatStats stats = CombatStats.createDefault();

        if (cards == null || cards.isEmpty()) {
            return clamp(stats);
        }

        Map<String, Integer> counts = countCards(cards);

        for (UpgradeCard card : cards) {
            if (card != null) {
                applyNumericPart(stats, card);
            }
        }

        applySpecialEffects(stats, counts);

        if (counts.getOrDefault("sniper", 0) > 0) {
            if (stats.getMaxAmmo() <= 5) {
                stats.setMaxAmmo(1);
            } else {
                stats.setMaxAmmo(stats.getMaxAmmo() - 5);
            }
        }

        applySynergies(stats, cards, counts);

        return clamp(stats);
    }

    private static Map<String, Integer> countCards(List<UpgradeCard> cards) {
        Map<String, Integer> counts = new HashMap<>();
        for (UpgradeCard card : cards) {
            if (card != null) {
                counts.merge(card.getId(), 1, Integer::sum);
            }
        }
        return counts;
    }

    private static void applySpecialEffects(CombatStats stats, Map<String, Integer> counts) {
        int cursed = counts.getOrDefault("cursed_bullet", 0);
        if (cursed > 0) {
            stats.setCursedBullet(true);
            stats.setCursedGlowDurationTicks(100 + (cursed - 1) * 20);
            stats.setCursedExplosionPower(2.8f);
        }

        int parasite = counts.getOrDefault("parasite", 0);
        if (parasite > 0) {
            stats.setParasite(true);
            stats.setParasiteSpawnOnKill(parasite + 1);
        }

        int jackpot = counts.getOrDefault("jackpot", 0);
        if (jackpot > 0) {
            stats.setJackpot(true);
            stats.setJackpotChancePercent(55 + (jackpot - 1) * 2);
            stats.setJackpotDurationTicks(60 + (jackpot - 1) * 20);
        }

        int thor = counts.getOrDefault("thor", 0);
        if (thor > 0) {
            stats.setThor(true);
            stats.setThorChancePercent(25 + (thor - 1) * 5);
            stats.setThorDamage(4.0f);
        }

        int underSpeed = counts.getOrDefault("under_speed", 0);
        if (underSpeed > 0) {
            stats.setUnderSpeed(true);
            stats.setUnderSpeedDurationTicks(40 + (underSpeed - 1) * 20);
            stats.setUnderSpeedAmplifier(1);
        }

        int dep = counts.getOrDefault("dep", 0);
        if (dep > 0) {
            stats.setDep(true);
            stats.setDepChancePercent(50 + (dep - 1) * 5);
            stats.setDepTargetBonusMultiplier(0.5 + (dep - 1) * 0.02);
            stats.setDepSelfBonusMultiplier(0.35 + (dep - 1) * 0.02);
        }

        int timeJump = counts.getOrDefault("time_jump", 0);
        if (timeJump > 0) {
            stats.setTimeJump(true);
            stats.setTimeJumpChancePercent(30 + (timeJump - 1) * 3);
        }

        int summoner = counts.getOrDefault("summoner", 0);
        if (summoner > 0) {
            stats.setSummoner(true);
            stats.setSummonerLimitPerPlayer(6 + (summoner - 1));
            stats.setSummonerZombieDamage(6.0f);
            stats.setSummonerZombieMaxHealth(10.0);
        }

        int triple = counts.getOrDefault("triple_shot", 0);
        if (triple > 0) {
            stats.setProjectilesPerShot(2 + triple);
        }

        int fire = counts.getOrDefault("fire_shot", 0);
        if (fire > 0) {
            stats.setFireOnHitDurationTicks(100 + (fire - 1) * 20);
            stats.setFireOnHitExtraDamage(4.0f);
        }

        int ice = counts.getOrDefault("ice_bullets", 0);
        if (ice > 0) {
            stats.setIceBulletDurationTicks(20 * ice + 20);
        }

        int healing = counts.getOrDefault("healing_field", 0);
        if (healing > 0) {
            stats.setHealingFieldRadius(1);
            stats.setHealingFieldLifetimeTicks(25);
            stats.setHealingFieldEffectDurationTicks(50 + ((healing - 1) * 10));
            stats.setHealingFieldAmplifier(1);
            stats.setHealingFieldCooldownTicks(160L);
            stats.setHealingFieldSurgeHearts(healing + 1);
        }

        int poisonBullet = counts.getOrDefault("poison_bullet", 0);
        if (poisonBullet > 0) {
            stats.setPoisonBulletInstantHearts(2 + poisonBullet);
        }

        int poisonCloud = counts.getOrDefault("poison_cloud", 0);
        if (poisonCloud > 0) {
            stats.setPoisonCloudRadius(3);
            stats.setPoisonCloudLifetimeTicks(60 + (poisonCloud - 1) * 20);
            stats.setPoisonCloudEffectDurationTicks(72);
            stats.setPoisonCloudAmplifier(1);
        }

        int blindness = counts.getOrDefault("blindness_bullets", 0);
        if (blindness > 0) {
            stats.setBlindnessChancePercent(30);
            stats.setBlindnessDurationTicks(40 + (blindness - 1) * 20);
        }

        int bombShield = counts.getOrDefault("bomb_shield", 0);
        if (bombShield > 0) {
            stats.setBombShield(true);
            stats.setBombShieldDamage(10.0f + (bombShield - 1) * 2.0f);
            stats.setBombShieldRadius(1.5);
            stats.setBombShieldExtraCooldownTicks(100L);
        }

        int kaboom = counts.getOrDefault("kaboom", 0);
        if (kaboom > 0) {
            stats.setKaboom(true);
            stats.setKaboomDamage(kaboom * 2.0f);
            stats.setKaboomRadius(1.5);
        }
    }

    private static void applySynergies(
            CombatStats stats,
            List<UpgradeCard> cards,
            Map<String, Integer> counts
    ) {
        int poisonBulletCount = counts.getOrDefault("poison_bullet", 0);
        int poisonCloudCount = counts.getOrDefault("poison_cloud", 0);
        int healingFieldCount = counts.getOrDefault("healing_field", 0);

        if (poisonBulletCount > 0 && poisonCloudCount > 0) {
            if (stats.getPoisonCloudRadius() > 0) {
                stats.setPoisonCloudRadius(stats.getPoisonCloudRadius() + 2);
            }
            stats.setPoisonBulletInstantHearts(stats.getPoisonBulletInstantHearts() + 2.0f);
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
        stats.setShieldCooldownTicks(clampCombatDuration(stats.getShieldCooldownTicks() + card.getShieldCooldownTicksFlat()));
        stats.setReloadDurationTicks(applyPercentToDuration(stats.getReloadDurationTicks(), card.getReloadPercent()));
        stats.setReloadDurationTicks(clampCombatDuration(stats.getReloadDurationTicks() + card.getReloadTicksFlat()));
    }

    private static long clampCombatDuration(long ticks) {
        return Math.max(MIN_COMBAT_DURATION_TICKS, ticks);
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
        stats.setReloadDurationTicks(clampCombatDuration(stats.getReloadDurationTicks()));
        stats.setShieldCooldownTicks(clampCombatDuration(stats.getShieldCooldownTicks()));
        stats.setShieldDurationTicks(clampCombatDuration(stats.getShieldDurationTicks()));
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
        stats.setSummonerZombieMaxHealth(Math.max(1.0, stats.getSummonerZombieMaxHealth()));
        stats.setBombShieldDamage(Math.max(0.0f, stats.getBombShieldDamage()));
        stats.setBombShieldRadius(Math.max(0.0, stats.getBombShieldRadius()));
        stats.setBombShieldExtraCooldownTicks(Math.max(0L, stats.getBombShieldExtraCooldownTicks()));
        stats.setKaboomDamage(Math.max(0.0f, stats.getKaboomDamage()));
        stats.setKaboomRadius(Math.max(0.0, stats.getKaboomRadius()));
        stats.setParasiteSpawnOnKill(Math.max(0, stats.getParasiteSpawnOnKill()));
        stats.setPoisonBulletInstantHearts(Math.max(0.0f, stats.getPoisonBulletInstantHearts()));
        stats.setHealingFieldSurgeHearts(Math.max(0, stats.getHealingFieldSurgeHearts()));
        return stats;
    }
}
