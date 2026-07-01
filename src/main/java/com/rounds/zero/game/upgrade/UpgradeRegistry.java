package com.rounds.zero.game.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeRegistry {
    private static final List<UpgradeCard> ALL_CARDS = new ArrayList<>();

    static {
        register(UpgradeCard.builder(
                "cursed_bullet",
                "ПРОКЛЯТАЯ ПУЛЯ",
                "При попадании по цели, она начинает светиться и после смерти взрывается.\n§a+2 пули, +20% скорость пули§r\n§c+2с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .flatAmmoBonus(2)
                .bulletSpeedMultiplier(1.2)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "parasite",
                "ПАРАЗИТ",
                "При убийстве спавнятся чешуйницы.\n§a+1 пуля, +10% DMG§r\n§c-2ХП§r"
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .flatAmmoBonus(1)
                .damageMultiplier(1.1)
                .maxHealthMultiplier(0.8)
                .build());

        register(UpgradeCard.builder(
                "jackpot",
                "ДЖЕКПОТ",
                "55% шанс случайного эффекта на 3 с — тебе или цели.\n§a+5 пуль, +10% скорость пули, +10% скорость атаки§r\n§c-2ХП, -10% DMG, +2с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
                .flatAmmoBonus(5)
                .damageMultiplier(0.9)
                .bulletSpeedMultiplier(1.1)
                .maxHealthMultiplier(0.8)
                .fireRatePercent(10)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "thor",
                "ТОР",
                "25% шанс молнии при попадании.\n§a+5ХП, +1 пуля, +10% скорость пули§r\n§c-10% скорость атаки§r"
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .flatAmmoBonus(1)
                .bulletSpeedMultiplier(1.1)
                .maxHealthMultiplier(1.5)
                .fireRatePercent(-10)
                .build());

        register(UpgradeCard.builder(
                "under_speed",
                "ПОД СПИДАМИ",
                "При попадании по врагу — Скорость II на 2 с.\n§a+25% скорость пули, +20% скорость атаки, +1 пуля§r\n§c-20% урон, +1с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .flatAmmoBonus(1)
                .damageMultiplier(0.8)
                .bulletSpeedMultiplier(1.25)
                .fireRatePercent(20)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "dep",
                "ДЕП",
                "50%: +50% урона по цели ИЛИ +35% урона по себе.\n§a+2 пули, +20% скорости пуль, +10% скорости атаки§r\n§c-15% урона, -2ХП, +1с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .flatAmmoBonus(2)
                .damageMultiplier(0.85)
                .bulletSpeedMultiplier(1.2)
                .maxHealthMultiplier(0.8)
                .fireRatePercent(10)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "time_jump",
                "ВРЕМЕННОЙ СКАЧОК",
                "30%: телепорт цели на 2 блока.\n§a+20% скорости пуль, +10% скорости атаки, +1ХП§r\n§c-10% урона, +1с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .damageMultiplier(0.9)
                .bulletSpeedMultiplier(1.2)
                .maxHealthMultiplier(1.1)
                .fireRatePercent(10)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "summoner",
                "ПРИЗЫВАТЕЛЬ",
                "Призыв зомби при блоке.\n§a+10% скорость пули§r\n§c-1 пуля, -2ХП, +2с щит, -10% скорость атаки, -20% DMG§r"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .flatAmmoBonus(-1)
                .damageMultiplier(0.8)
                .bulletSpeedMultiplier(1.1)
                .maxHealthMultiplier(0.8)
                .fireRatePercent(-10)
                .shieldCooldownTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "triple_shot",
                "ТРОЙНОЙ ВЫСТРЕЛ",
                "3 пули за 1 патрон.\n§a+10% скорость пули, щит -1с§r\n§c-10% скорость атаки, -20% DMG, -1 пуля, reload +3с§r"
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .flatAmmoBonus(-1)
                .damageMultiplier(0.8)
                .bulletSpeedMultiplier(1.1)
                .fireRatePercent(-10)
                .reloadTicksFlat(60L)
                .shieldCooldownTicksFlat(-20L)
                .build());

        register(UpgradeCard.builder(
                "sniper",
                "СНАЙПЕР",
                "Мощный одиночный выстрел на огромную дистанцию.\n§a+50% DMG, +50% скорость пули, щит -2с§r\n§c-5 пуль, -50% скорость стрельбы, -5 ХП, reload +3с§r"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .damageMultiplier(1.5)
                .bulletSpeedMultiplier(1.5)
                .maxHealthMultiplier(0.5)
                .fireRatePercent(-50)
                .reloadTicksFlat(60L)
                .shieldCooldownTicksFlat(-40L)
                .build());

        register(UpgradeCard.builder(
                "fire_shot",
                "ОГНЕННЫЙ ВЫСТРЕЛ",
                "Поджог при попадании по цели.\n§a+15% скорости пуль, +10% скорости атаки, reload -3с§r\n§c-20% DMG, -2 пули§r"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .flatAmmoBonus(-2)
                .damageMultiplier(0.8)
                .bulletSpeedMultiplier(1.15)
                .fireRatePercent(10)
                .reloadTicksFlat(-60L)
                .build());

        register(UpgradeCard.builder(
                "ahui_plan",
                "НАДЕЖНЫЙ ПЛАН",
                "Каждый выстрел становится медленным, но сокрушительным.\n§aDMG в 2 раза больше§r\n§cСкорость стрельбы в 2 раза ниже, reload +2с, -3 ХП§r"
        ).texturePath("rounds_zero:textures/gui/cards/ahui_plan.png")
                .damageMultiplier(2)
                .maxHealthMultiplier(0.7)
                .fireRatePercent(-100)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "combine",
                "КОМБАЙН",
                "Увеличивает убойную силу за счёт уменьшения боезапаса.\n§a+50% DMG§r\n§c-3 пули, +1с reload, -2 ХП§r"
        ).texturePath("rounds_zero:textures/gui/cards/combine.png")
                .flatAmmoBonus(-3)
                .damageMultiplier(1.5)
                .maxHealthMultiplier(0.8)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "bogatyr",
                "БОГАТЫРЬ",
                "Значительно увеличивает выживаемость, ослабляя атаку.\n§a+5 ХП, щит -3с§r\n§c-25% DMG§r"
        ).texturePath("rounds_zero:textures/gui/cards/bogatyr.png")
                .damageMultiplier(0.75)
                .maxHealthMultiplier(1.5)
                .shieldCooldownTicksFlat(-60L)
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "Скорость пули и стрельбы в 2 раза выше.\n§aСкорость пули ×2, скорость стрельбы ×2, reload -2с, щит -1с§r\n§c-20% DMG§r"
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .damageMultiplier(0.8)
                .bulletSpeedMultiplier(2)
                .fireRatePercent(100)
                .reloadTicksFlat(-40L)
                .shieldCooldownTicksFlat(-20L)
                .build());

        register(UpgradeCard.builder(
                "glass_cannon",
                "СТЕКЛЯННАЯ ПУШКА",
                "Максимальный урон при критически низком здоровье.\n§aDMG в 2 раза больше§r\n§c-6 ХП§r"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .damageMultiplier(2)
                .maxHealthMultiplier(0.4)
                .build());

        register(UpgradeCard.builder(
                "your_mama",
                "ТВОЯ МАМА",
                "Огромный запас здоровья.\n§a+8 ХП§r\n§c-40% DMG, +2с щит, +2с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/your_mama.png")
                .damageMultiplier(0.6)
                .maxHealthMultiplier(1.8)
                .reloadTicksFlat(40L)
                .shieldCooldownTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "fast_gonzales",
                "БЫСТРЫЙ ГОНЗАЛЕС",
                "Максимально быстрое восстановление оружия и защиты.\n§areload в 2 раза быстрее, щит -1с§r"
        ).texturePath("rounds_zero:textures/gui/cards/fast_gonzales.png")
                .reloadPercent(-50)
                .shieldCooldownTicksFlat(-20L)
                .build());

        register(UpgradeCard.builder(
                "obossivatel",
                "ОБОССЫВАТЕЛЬ",
                "Невероятный темп стрельбы с огромным боекомплектом.\n§aСкорость стрельбы ×10, +17 пуль§r\n§c-50% DMG, +4с reload, -1 ХП§r"
        ).texturePath("rounds_zero:textures/gui/cards/obossivatel.png")
                .flatAmmoBonus(17)
                .damageMultiplier(0.5)
                .maxHealthMultiplier(0.9)
                .fireRatePercent(900)
                .reloadTicksFlat(80L)
                .build());

        register(UpgradeCard.builder(
                "tank",
                "ТАНК",
                "Несокрушимая огневая точка с медленной атакой.\n§a+10 ХП, +20% DMG, щит -1с§r\n§c-50% скорость стрельбы, -2 пули, reload +3с§r"
        ).texturePath("rounds_zero:textures/gui/cards/tank.png")
                .flatAmmoBonus(-2)
                .damageMultiplier(1.2)
                .maxHealthMultiplier(2)
                .fireRatePercent(-50)
                .reloadTicksFlat(60L)
                .shieldCooldownTicksFlat(-20L)
                .build());

        register(UpgradeCard.builder(
                "tochniy_vystrel",
                "ТОЧНЫЙ ВЫСТРЕЛ",
                "Точный выстрел с высокой скоростью полёта пули.\n§aПули в 2 раза быстрее, +10% DMG§r\n§c-20% скорость атаки, +1с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .damageMultiplier(1.1)
                .bulletSpeedMultiplier(2)
                .fireRatePercent(-20)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "ice_bullets",
                "ЛЕДЯНЫЕ ПУЛИ",
                "Замедление цели при попадании.\n§a+3 ХП§r\n§cСкорость стрельбы -10%, скорость пули -10%, reload +1с§r"
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .bulletSpeedMultiplier(0.9)
                .maxHealthMultiplier(1.3)
                .fireRatePercent(-10)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "healing_field",
                "ЛЕЧАЩЕЕ ПОЛЕ",
                "Активируй зону лечения при блоке.\n§a+3 ХП§r\n§c+2с щит, -15% DMG§r"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .damageMultiplier(0.85)
                .maxHealthMultiplier(1.3)
                .shieldCooldownTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "poison_bullet",
                "ОТРАВА",
                "Отрави цель при попадании.\n§a+10% DMG от пули§r\n§cСкорость пули -10%, скорость атаки -10%, -1 пуля§r"
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .flatAmmoBonus(-1)
                .damageMultiplier(1.1)
                .bulletSpeedMultiplier(0.9)
                .fireRatePercent(-10)
                .build());

        register(UpgradeCard.builder(
                "poison_cloud",
                "ЯДОВИТОЕ ОБЛАКО",
                "Ядовитая зона в месте попадания пули.\n§cСкорость стрельбы -20%, reload +2с, DMG -20%§r"
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .damageMultiplier(0.8)
                .fireRatePercent(-20)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "blindness_bullets",
                "ОСЛЕПЛЕНИЕ",
                "Ослепи цель при попадании с шансом 30%.\n§a+10% скорость атаки, +10% скорость пуль§r\n§c-1 пуля§r"
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
                .flatAmmoBonus(-1)
                .bulletSpeedMultiplier(1.1)
                .fireRatePercent(10)
                .build());

        register(UpgradeCard.builder(
                "bomb_shield",
                "БОМБИЧЕСКИЙ НАСТРОЙ",
                "Взорвись при использовании щита (союзникам урон не наносится).\n§a+3 ХП§r\n§c+3с щит§r"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.3)
                .shieldCooldownTicksFlat(60L)
                .build());

        register(UpgradeCard.builder(
                "kaboom",
                "БАБАХ",
                "Пуля взрывается при попадании!\n§a+10% скорость пуль, +10% DMG§r\n§c-20% скорость атаки, +1с reload§r"
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .damageMultiplier(1.1)
                .bulletSpeedMultiplier(1.1)
                .fireRatePercent(-20)
                .reloadTicksFlat(20L)
                .build());

    }

    private UpgradeRegistry() {
    }

    private static void register(UpgradeCard card) {
        ALL_CARDS.add(card);
    }

    public static List<UpgradeCard> getAllCards() {
        return Collections.unmodifiableList(ALL_CARDS);
    }

    public static UpgradeCard findById(String id) {
        if (id == null) {
            return null;
        }

        for (UpgradeCard card : ALL_CARDS) {
            if (card.getId().equalsIgnoreCase(id)) {
                return card;
            }
        }

        return null;
    }
}
