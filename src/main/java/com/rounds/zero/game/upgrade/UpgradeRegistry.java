package com.rounds.zero.game.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeRegistry {
    private static final List<UpgradeCard> ALL_CARDS = new ArrayList<>();

    static {
//        register(UpgradeCard.builder(
//                "big_bullet",
//                "БОЛЬШАЯ ПУЛЯ",
//                "Размер пули x2, перезарядка +0.25с"
//        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
//                .bulletSizeMultiplier(2.0)
//                .reloadTicksFlat(5L)
//                .build());

        register(UpgradeCard.builder(
                "ahui_plan",
                "АХУЕННЫЙ ПЛАН",
                "+100% урона, -100% скорости стрельбы, перезарядка +0.5с"
        ).texturePath("rounds_zero:textures/gui/cards/ahui_plan.png")
                .damageMultiplier(2.0)
                .fireRatePercent(-100.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "combine",
                "КОМБАЙН",
                "+100% урона, перезарядка +0.25с, -2 к боезапасу"
        ).texturePath("rounds_zero:textures/gui/cards/combine.png")
                .damageMultiplier(2.0)
                .reloadTicksFlat(5L)
                .flatAmmoBonus(-2)
                .build());

        register(UpgradeCard.builder(
                "bogatyr",
                "БОГАТЫРЬ",
                "-25% перезарядки блока, +50% HP"
        ).texturePath("rounds_zero:textures/gui/cards/bogatyr.png")
                .shieldCooldownPercent(-25.0)
                .maxHealthMultiplier(1.5)
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "+250% скорости пуль, -50% скорости стрельбы, +2с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .bulletSpeedMultiplier(3.5)
                .fireRatePercent(-50.0)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "glass_cannon",
                "СТЕКЛЯННАЯ ПУШКА",
                "+100% урона, -60% HP, -0.5с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .damageMultiplier(2.0)
                .maxHealthMultiplier(0.4)
                .reloadTicksFlat(-10L)
                .build());

        register(UpgradeCard.builder(
                "your_mama",
                "ТВОЯ МАМА",
                "+80% HP, -1 к боезапасу, -1с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/your_mama.png")
                .maxHealthMultiplier(1.8)
                .flatAmmoBonus(-1)
                .reloadTicksFlat(-20L)
                .build());

        register(UpgradeCard.builder(
                "fast_gonzales",
                "БЫСТРЫЙ ГОНЗАЛЕС",
                "-50% перезарядки патронов"
        ).texturePath("rounds_zero:textures/gui/cards/fast_gonzales.png")
                .reloadPercent(-50.0)
                .build());

        register(UpgradeCard.builder(
                "obossivatel",
                "ОБОССЫВАТЕЛЬ",
                "+1000% скорости стрельбы, +5 патронов, -50% урона, +1с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/obossivatel.png")
                .fireRatePercent(1000.0)
                .flatAmmoBonus(5)
                .damageMultiplier(0.5)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "tank",
                "ТАНК",
                "+100% HP, -25% скорости стрельбы, +0.5с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/tank.png")
                .maxHealthMultiplier(2.0)
                .fireRatePercent(-25.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "raskrutka",
                "РАСКРУТКА",
                "+100% скорости пули, +50% скорости стрельбы, +0.5с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .bulletSpeedMultiplier(2.0)
                .fireRatePercent(50.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "tochniy_vystrel",
                "ТОЧНЫЙ ВЫСТРЕЛ",
                "+30% HP, +100% скорости пули, +0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .maxHealthMultiplier(1.3)
                .bulletSpeedMultiplier(2.0)
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "ice_bullets",
                "ЛЕДЯНЫЕ ПУЛИ",
                "Пуля накладывает замедление II на 1с, +0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "healing_field",
                "ЛЕЧАЩЕЕ ПОЛЕ",
                "При активации щита создаёт поле регенерации, +30% HP, +1с кд щита"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.3)
                .shieldCooldownTicksFlat(20)
                .build());

        register(UpgradeCard.builder(
                "poison_bullet",
                "ОТРАВА",
                "Пуля накладывает отравление III, +10% урона, -15% перезарядки, -1 патрон"
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .damageMultiplier(1.1)
                .reloadPercent(-15.0)
                .flatAmmoBonus(-1)
                .build());

        register(UpgradeCard.builder(
                "poison_cloud",
                "ЯДОВИТОЕ ОБЛАКО",
                "Пуля создаёт облако яда, -20% скорости стрельбы, +0.5с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/poison_cloud.png")
                .fireRatePercent(-20.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "blindness_bullets",
                "ОСЛЕПЛЕНИЕ",
                "30% шанс наложить слепоту на 2с"
        ).texturePath("rounds_zero:textures/gui/cards/blindness_bullets.png")
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
}
