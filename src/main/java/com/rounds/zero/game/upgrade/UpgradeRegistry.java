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
                "Попадание заставляет цель светиться. При смерти цель взрывается (блоки не ломаются)."
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .bulletSpeedMultiplier(1.1)
                .flatAmmoBonus(2)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "parasite",
                "ПАРАЗИТ",
                "Убийство пулей: из цели появляются 2 чешуйницы. Убийство чешуйницей: появляется ещё 1."
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .damageMultiplier(1.2)
                .fireRatePercent(-10.0)
                .flatAmmoBonus(1)
                .maxHealthMultiplier(1.3)
                .build());

        register(UpgradeCard.builder(
                "jackpot",
                "ДЖЕКПОТ",
                "50% шанс: при попадании случайный эффект на 2 секунды — либо тебе, либо цели."
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
                .flatAmmoBonus(5)
                .fireRatePercent(20.0)
                .bulletSpeedMultiplier(0.9)
                .damageMultiplier(1.1)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "thor",
                "ТОР",
                "15% шанс вызвать молнию при попадании (без огня и без ломания блоков)."
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .maxHealthMultiplier(1.2)
                .flatAmmoBonus(1)
                .fireRatePercent(-10.0)
                .shieldCooldownTicksFlat(-20)
                .build());

        register(UpgradeCard.builder(
                "under_speed",
                "ПОД СПИДАМИ",
                "При попадании во врага ты получаешь Скорость II на 2 секунды."
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .bulletSpeedMultiplier(1.2)
                .fireRatePercent(10.0)
                .maxHealthMultiplier(0.8)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "dep",
                "ДЕП",
                "50% шанс: +50% урона по цели ИЛИ +20% урона по тебе (с учётом модификаторов)."
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .maxHealthMultiplier(0.9)
                .fireRatePercent(20.0)
                .bulletSpeedMultiplier(1.1)
                .flatAmmoBonus(3)
                .build());

        register(UpgradeCard.builder(
                "time_jump",
                "ВРЕМЕННОЙ СКАЧОК",
                "15% шанс: цель телепортируется на 2 блока в случайную сторону (вбок или вверх)."
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .bulletSpeedMultiplier(1.1)
                .fireRatePercent(-10.0)
                .flatAmmoBonus(1)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "summoner",
                "ПРИЗЫВАТЕЛЬ",
                "При использовании щита призывает зомби. Он не горит и не атакует призывателя. Лимит: 6 на игрока."
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .shieldCooldownTicksFlat(40)
                .maxHealthMultiplier(0.9)
                .build());

        register(UpgradeCard.builder(
                "triple_shot",
                "ТРОЙНОЙ ВЫСТРЕЛ",
                "Стреляет 3 пулями за 1 патрон (три рядом). +5% скорость пули, -5% скорости стрельбы, -1 пуля, +2с перезарядка, +5% здоровья."
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .bulletSpeedMultiplier(1.05)
                .fireRatePercent(-5.0)
                .flatAmmoBonus(-1)
                .reloadTicksFlat(40L)
                .maxHealthMultiplier(1.05)
                .build());

        register(UpgradeCard.builder(
                "sniper",
                "СНАЙПЕР",
                "Полная замена: -5 пуль (минимум 1), +100% урон, +100% скорость пули, -50% скорости стрельбы, +2с перезарядка, -30% здоровья, -2с перезарядка щита."
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .damageMultiplier(2.0)
                .bulletSpeedMultiplier(2.0)
                .fireRatePercent(-50.0)
                .maxHealthMultiplier(0.7)
                .reloadTicksFlat(40L)
                .shieldCooldownTicksFlat(-40)
                .build());

        register(UpgradeCard.builder(
                "fire_shot",
                "ОГНЕННЫЙ ВЫСТРЕЛ",
                "При попадании цель сильно загорается и получает дополнительный огненный урон."
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .reloadTicksFlat(20L)
                .bulletSpeedMultiplier(1.1)
                .fireRatePercent(-10.0)
                .maxHealthMultiplier(1.2)
                .shieldCooldownTicksFlat(20)
                .build());

        register(UpgradeCard.builder(
                "ahui_plan",
                "НАДЕЖНЫЙ ПЛАН",
                "Урон +100%. Скорость стрельбы в 2 раза ниже. Перезарядка +0.5с."
        ).texturePath("rounds_zero:textures/gui/cards/ahui_plan.png")
                .damageMultiplier(2.0)
                .fireRatePercent(-100.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "combine",
                "КОМБАЙН",
                "Урон +100%. Перезарядка +0.25с. Патроны в магазине -2."
        ).texturePath("rounds_zero:textures/gui/cards/combine.png")
                .damageMultiplier(2.0)
                .reloadTicksFlat(5L)
                .flatAmmoBonus(-2)
                .build());

        register(UpgradeCard.builder(
                "bogatyr",
                "БОГАТЫРЬ",
                "Перезарядка щита -25%. Здоровье +50%."
        ).texturePath("rounds_zero:textures/gui/cards/bogatyr.png")
                .shieldCooldownPercent(-25.0)
                .maxHealthMultiplier(1.5)
                .fireRatePercent(-5.0)
                .damageMultiplier(1.05)
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "Скорость пуль +175%. Скорость стрельбы -30%. Перезарядка +2с."
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .bulletSpeedMultiplier(2.75)
                .fireRatePercent(30.0)
                .maxHealthMultiplier(0.8)
                .reloadTicksFlat(40L)
                .shieldCooldownTicksFlat(20)
                .build());

        register(UpgradeCard.builder(
                "glass_cannon",
                "СТЕКЛЯННАЯ ПУШКА",
                "Урон +100%. Здоровье -60%. Перезарядка -0.5с."
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .damageMultiplier(2.0)
                .maxHealthMultiplier(0.4)
                .reloadTicksFlat(-10L)
                .build());

        register(UpgradeCard.builder(
                "your_mama",
                "ТВОЯ МАМА",
                "Здоровье +80%. Перезарядка оружия -1с. Кулдаун щита -1с."
        ).texturePath("rounds_zero:textures/gui/cards/your_mama.png")
                .maxHealthMultiplier(1.8)
                .reloadTicksFlat(-20L)
                .shieldCooldownTicksFlat(-20)
                .build());

        register(UpgradeCard.builder(
                "fast_gonzales",
                "БЫСТРЫЙ ГОНЗАЛЕС",
                "Перезарядка -50%."
        ).texturePath("rounds_zero:textures/gui/cards/fast_gonzales.png")
                .reloadPercent(-50.0)
                .shieldCooldownPercent(-10.0)
                .build());

        register(UpgradeCard.builder(
                "obossivatel",
                "ОБОССЫВАТЕЛЬ",
                "Скорость стрельбы в 12 раз выше. Патронов в магазине +17. Урон -50%. Перезарядка +3с."
        ).texturePath("rounds_zero:textures/gui/cards/obossivatel.png")
                .fireRatePercent(1100.0)
                .flatAmmoBonus(17)
                .damageMultiplier(0.5)
                .reloadTicksFlat(60L)
                .build());

        register(UpgradeCard.builder(
                "tank",
                "ТАНК",
                "Здоровье +100%. Урон +20%. Скорость стрельбы -25%. Перезарядка +0.5с."
        ).texturePath("rounds_zero:textures/gui/cards/tank.png")
                .maxHealthMultiplier(2.0)
                .damageMultiplier(1.2)
                .fireRatePercent(-25.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "tochniy_vystrel",
                "ТОЧНЫЙ ВЫСТРЕЛ",
                "Здоровье +30%. Скорость пуль +150%. Перезарядка +0.25с."
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .maxHealthMultiplier(1.3)
                .bulletSpeedMultiplier(2.5)
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "ice_bullets",
                "ЛЕДЯНЫЕ ПУЛИ",
                "Пули замедляют цель на 2 секунды. Перезарядка +0.25с."
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "healing_field",
                "ЛЕЧАЩЕЕ ПОЛЕ",
                "Когда активируешь щит, он даёт лечение союзникам. Здоровье +30%. Кулдаун щита +1с. Лечение в кулдауне 8с."
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.3)
                .shieldCooldownTicksFlat(20)
                .build());

        register(UpgradeCard.builder(
                "poison_bullet",
                "ОТРАВА",
                "Пуля накладывает отравление. Урон +10%. Перезарядка -20%."
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .damageMultiplier(1.1)
                .reloadPercent(-20.0)
                .build());

        register(UpgradeCard.builder(
                "poison_cloud",
                "ЯДОВИТОЕ ОБЛАКО",
                "При попадании создаётся облако яда. Скорость стрельбы -20%. Перезарядка +0.5с."
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .fireRatePercent(-20.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "blindness_bullets",
                "ОСЛЕПЛЕНИЕ",
                "30% шанс ослепить цель на 2 секунды."
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
                .fireRatePercent(10.0)
                .build());

        register(UpgradeCard.builder(
                "bomb_shield",
                "БОМБИЧЕСКИЙ НАСТРОЙ",
                "При активации щита — взрыв радиусом 3 блока на 5 сердец. Союзников не задевает. Кулдаун щита +5 сек."
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.2)
                .build());

        register(UpgradeCard.builder(
                "kaboom",
                "БАБАХ",
                "Пуля взрывается при попадании (3 блока, 1 сердце). Бьёт всех, включая тебя и союзников. -10% скорость стрельбы, -10% скорость пуль."
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .fireRatePercent(-10.0)
                .bulletSpeedMultiplier(0.9)
                .damageMultiplier(1.1)
                .maxHealthMultiplier(0.9)
                .reloadTicksFlat(20L)
                .shieldCooldownTicksFlat(20)
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
