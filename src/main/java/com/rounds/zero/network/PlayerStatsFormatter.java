package com.rounds.zero.network;

import com.rounds.zero.game.combat.CombatStats;
import com.rounds.zero.game.combat.PlayerCombatData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class PlayerStatsFormatter {
    private PlayerStatsFormatter() {
    }

    static List<String> format(ServerPlayerEntity player, CombatStats stats, PlayerCombatData combatData) {
        List<String> lines = new ArrayList<>();

        lines.add(section("Основное"));
        lines.add(String.format(
                Locale.ROOT,
                "HP: %.1f / %.1f сердец",
                player.getHealth() / 2.0,
                stats.getMaxHealth() / 2.0
        ));
        lines.add(String.format(Locale.ROOT, "Урон пули: %.1f сердец", stats.getBulletDamage() / 2.0));
        lines.add(String.format(Locale.ROOT, "Скорость пули: %.2f", stats.getBulletSpeed()));
        lines.add(String.format(Locale.ROOT, "Размер пули: %.2f", stats.getBulletSize()));
        lines.add(String.format(
                Locale.ROOT,
                "Патроны: %d / %d",
                combatData.getCurrentAmmo(),
                stats.getMaxAmmo()
        ));
        lines.add(String.format(Locale.ROOT, "Пуль за выстрел: %d", stats.getProjectilesPerShot()));
        lines.add(String.format(
                Locale.ROOT,
                "Скорость атаки: %.2f выстр/с (%.1f с)",
                20.0 / stats.getShotCooldownTicks(),
                ticksToSeconds(stats.getShotCooldownTicks())
        ));
        lines.add(String.format(Locale.ROOT, "Перезарядка: %.1f с", ticksToSeconds(stats.getReloadDurationTicks())));

        lines.add("");
        lines.add(section("Щит"));
        lines.add(String.format(Locale.ROOT, "Длительность: %.1f с", ticksToSeconds(stats.getShieldDurationTicks())));
        lines.add(String.format(Locale.ROOT, "Кулдаун: %.1f с", ticksToSeconds(stats.getShieldCooldownTicks())));

        appendEffects(lines, stats);
        appendSynergies(lines, stats);

        return lines;
    }

    private static void appendEffects(List<String> lines, CombatStats stats) {
        List<String> effects = new ArrayList<>();

        if (stats.getIceBulletDurationTicks() > 0) {
            effects.add(String.format(Locale.ROOT, "Лёд: %.1f с", ticksToSeconds(stats.getIceBulletDurationTicks())));
        }

        if (stats.getPoisonBulletDurationTicks() > 0 || stats.getPoisonBulletInstantHearts() > 0.0f) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Яд: %.1f с, мгновенно %.1f серд.",
                    ticksToSeconds(stats.getPoisonBulletDurationTicks()),
                    stats.getPoisonBulletInstantHearts()
            ));
        }

        if (stats.getPoisonCloudLifetimeTicks() > 0) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Ядовитое облако: радиус %d, %.1f с",
                    stats.getPoisonCloudRadius(),
                    ticksToSeconds(stats.getPoisonCloudLifetimeTicks())
            ));
        }

        if (stats.getBlindnessChancePercent() > 0) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Слепота: %d%%, %.1f с",
                    stats.getBlindnessChancePercent(),
                    ticksToSeconds(stats.getBlindnessDurationTicks())
            ));
        }

        if (stats.getHealingFieldLifetimeTicks() > 0) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Поле лечения: радиус %d, %.1f с, кулдаун %.1f с",
                    stats.getHealingFieldRadius(),
                    ticksToSeconds(stats.getHealingFieldLifetimeTicks()),
                    ticksToSeconds(stats.getHealingFieldCooldownTicks())
            ));
        }

        if (stats.getFireOnHitDurationTicks() > 0) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Поджог: %.1f с, +%.1f урона",
                    ticksToSeconds(stats.getFireOnHitDurationTicks()),
                    stats.getFireOnHitExtraDamage()
            ));
        }

        if (stats.isCursedBullet()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Проклятая пуля: свечение %.1f с, взрыв %.1f",
                    ticksToSeconds(stats.getCursedGlowDurationTicks()),
                    stats.getCursedExplosionPower()
            ));
        }

        if (stats.isParasite()) {
            effects.add(String.format(Locale.ROOT, "Паразит: %d чешуйниц за убийство", stats.getParasiteSpawnOnKill()));
        }

        if (stats.isJackpot()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Джекпот: %d%%, %.1f с",
                    stats.getJackpotChancePercent(),
                    ticksToSeconds(stats.getJackpotDurationTicks())
            ));
        }

        if (stats.isThor()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Тор: %d%%, урон %.1f",
                    stats.getThorChancePercent(),
                    stats.getThorDamage()
            ));
        }

        if (stats.isUnderSpeed()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Замедление: %d%%, %.1f с",
                    stats.getUnderSpeedAmplifier() + 1,
                    ticksToSeconds(stats.getUnderSpeedDurationTicks())
            ));
        }

        if (stats.isDep()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Деп: %d%%, цель x%.2f, себе x%.2f",
                    stats.getDepChancePercent(),
                    stats.getDepTargetBonusMultiplier(),
                    stats.getDepSelfBonusMultiplier()
            ));
        }

        if (stats.isTimeJump()) {
            effects.add(String.format(Locale.ROOT, "Прыжок во времени: %d%%", stats.getTimeJumpChancePercent()));
        }

        if (stats.isSummoner()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Призыватель: до %d зомби, %.1f HP, урон %.1f",
                    stats.getSummonerLimitPerPlayer(),
                    stats.getSummonerZombieMaxHealth() / 2.0,
                    stats.getSummonerZombieDamage() / 2.0
            ));
        }

        if (stats.isGhostRider()) {
            effects.add("Призрачный наездник: активен");
        }

        if (stats.isBombShield()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "Бомбический щит: %.1f серд., радиус %.1f, +%.1f с кулдаун",
                    stats.getBombShieldDamage() / 2.0,
                    stats.getBombShieldRadius(),
                    ticksToSeconds(stats.getBombShieldExtraCooldownTicks())
            ));
        }

        if (stats.isKaboom()) {
            effects.add(String.format(
                    Locale.ROOT,
                    "БАБАХ: %.1f серд., радиус %.1f",
                    stats.getKaboomDamage() / 2.0,
                    stats.getKaboomRadius()
            ));
        }

        if (effects.isEmpty()) {
            return;
        }

        lines.add("");
        lines.add(section("Эффекты"));
        lines.addAll(effects);
    }

    private static void appendSynergies(List<String> lines, CombatStats stats) {
        List<String> synergies = new ArrayList<>();

        if (stats.isParasiteSummonerSynergy()) {
            synergies.add("Паразит + Призыватель");
        }

        if (stats.isDepJackpotSynergy()) {
            synergies.add("Деп + Джекпот");
        }

        if (stats.isHealingFieldSurge()) {
            synergies.add("Поле лечения + танк");
        }

        if (stats.isFireGhostSynergy()) {
            synergies.add("Огненный выстрел + Тор");
        }

        if (synergies.isEmpty()) {
            return;
        }

        lines.add("");
        lines.add(section("Синергии"));
        lines.addAll(synergies);
    }

    private static String section(String title) {
        return "== " + title + " ==";
    }

    private static double ticksToSeconds(long ticks) {
        return ticks / 20.0;
    }
}
