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
                .build());

        register(UpgradeCard.builder(
                "parasite",
                "ПАРАЗИТ",
                "Убийство пулей: из цели появляются 2 чешуйницы. Убийство чешуйницей: появляется ещё 1."
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .build());

        register(UpgradeCard.builder(
                "jackpot",
                "ДЖЕКПОТ",
                "50% шанс: при попадании случайный эффект на 2 секунды — либо тебе, либо цели."
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
                .build());

        register(UpgradeCard.builder(
                "thor",
                "ТОР",
                "15% шанс вызвать молнию при попадании (без огня и без ломания блоков)."
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .build());

        register(UpgradeCard.builder(
                "under_speed",
                "ПОД СПИДАМИ",
                "При попадании во врага ты получаешь Скорость II на 2 секунды."
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .build());

        register(UpgradeCard.builder(
                "dep",
                "ДЕП",
                "50% шанс: +50% урона по цели ИЛИ +20% урона по тебе (с учётом модификаторов)."
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .build());

        register(UpgradeCard.builder(
                "time_jump",
                "ВРЕМЕННОЙ СКАЧОК",
                "15% шанс: цель телепортируется на 2 блока в случайную сторону (вбок или вверх)."
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .build());

        register(UpgradeCard.builder(
                "summoner",
                "ПРИЗЫВАТЕЛЬ",
                "При использовании щита призывает зомби. Он не горит и не атакует призывателя. Лимит: 6 на игрока."
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .build());

        register(UpgradeCard.builder(
                "triple_shot",
                "ТРОЙНОЙ ВЫСТРЕЛ",
                "Стреляет 3 пулями за 1 патрон (три рядом). -25% урон, +11% скорость пуль, -11% скорость стрельбы, +1с перезарядка"
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .damageMultiplier(0.75)
                .bulletSpeedMultiplier(1.11)
                .fireRatePercent(-11.0)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "sniper",
                "СНАЙПЕР",
                "Урон +150%, скорость пуль +500%, скорость стрельбы в 2 раза ниже, здоровье -30%. +2.5с перезарядка. В магазине 1 патрон."
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .damageMultiplier(2.5)
                .bulletSpeedMultiplier(6.0)
                .fireRatePercent(-100.0)
                .maxHealthMultiplier(0.7)
                .reloadTicksFlat(50L)
                .build());

        register(UpgradeCard.builder(
                "fire_shot",
                "ОГНЕННЫЙ ВЫСТРЕЛ",
                "При попадании цель поджигается на 3 секунды."
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .build());

        register(UpgradeCard.builder(
                "ahui_plan",
                "АХУЕННЫЙ ПЛАН",
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
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "Скорость пуль +175%. Скорость стрельбы -30%. Перезарядка +2с."
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .bulletSpeedMultiplier(2.75)
                .fireRatePercent(-30.0)
                .reloadTicksFlat(40L)
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
                "raskrutka",
                "РАСКРУТКА",
                "Скорость пуль +60%. Скорость стрельбы +50%. Перезарядка +1с."
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .bulletSpeedMultiplier(1.6)
                .fireRatePercent(50.0)
                .reloadTicksFlat(20L)
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
                .build());

        register(UpgradeCard.builder(
                "ghost_rider",
                "ПРИЗРАЧНЫЙ ГОНЩИК",
                "За тобой остаётся огненный след: блоки под ногами поджигаются."
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .build());

        register(UpgradeCard.builder(
                "bomb_shield",
                "БОМБИЧЕСКИЙ НАСТРОЙ",
                "При активации щита — взрыв радиусом 3 блока на 5 сердец. Союзников не задевает. Кулдаун щита +5 сек."
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .build());

        register(UpgradeCard.builder(
                "kaboom",
                "БАБАХ",
                "Пуля взрывается при попадании (3 блока, 2 сердца). Бьёт всех, включая тебя и союзников. -10% скорость стрельбы, -10% скорость пуль."
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .fireRatePercent(-10.0)
                .bulletSpeedMultiplier(0.9)
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
