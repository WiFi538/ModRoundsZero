package com.rounds.zero.game.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeRegistry {
    private static final List<UpgradeCard> ALL_CARDS = new ArrayList<>();

    static {
        register(UpgradeCard.builder(
                "big_bullet",
                "БОЛЬШАЯ ПУЛЯ",
                "Размер пули x2, перезарядка +0.25с"
        ).texturePath("rounds_zero:textures/gui/cards/big_bullet.png")
                .bulletSizeMultiplier(2.0)
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "ahui_plan",
                "АХУЕННЫЙ ПЛАН",
                "+100% урона, -150% скорости стрельбы, перезарядка +0.5с"
        ).texturePath("rounds_zero:textures/gui/cards/ahui_plan.png")
                .damageMultiplier(2.0)
                .fireRatePercent(-150.0)
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
                "-30% перезарядки блока, +30% HP"
        ).texturePath("rounds_zero:textures/gui/cards/bogatyr.png")
                .shieldCooldownPercent(-30.0)
                .maxHealthMultiplier(1.3)
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "+250% скорости пуль, -50% скорости стрельбы, +0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .bulletSpeedMultiplier(3.5)
                .fireRatePercent(-50.0)
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "glass_cannon",
                "СТЕКЛЯННАЯ ПУШКА",
                "+100% урона, -100% HP, -0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .damageMultiplier(2.0)
                .maxHealthMultiplier(0.01)
                .reloadTicksFlat(-5L)
                .build());

        register(UpgradeCard.builder(
                "your_mama",
                "ТВОЯ МАМА",
                "+80% HP"
        ).texturePath("rounds_zero:textures/gui/cards/your_mama.png")
                .maxHealthMultiplier(1.8)
                .build());

        register(UpgradeCard.builder(
                "fast_gonzales",
                "БЫСТРЫЙ ГОНЗАЛЕС",
                "-70% перезарядки патронов"
        ).texturePath("rounds_zero:textures/gui/cards/fast_gonzales.png")
                .reloadPercent(-70.0)
                .build());

        register(UpgradeCard.builder(
                "obossivatel",
                "ОБОССЫВАТЕЛЬ",
                "+1000% скорости стрельбы, +10 патронов, -75% урона, +0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/obossivatel.png")
                .fireRatePercent(1000.0)
                .flatAmmoBonus(10)
                .damageMultiplier(0.25)
                .reloadTicksFlat(5L)
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
                "+100% скорости пули, +60% урона, -100% скорости стрельбы, +0.5с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .bulletSpeedMultiplier(2.0)
                .damageMultiplier(1.6)
                .fireRatePercent(-100.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "tochniy_vystrel",
                "ТОЧНЫЙ ВЫСТРЕЛ",
                "+40% HP, +100% скорости пули, +0.25с перезарядки"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .maxHealthMultiplier(1.4)
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
                "При активации щита создаёт поле регенерации, +30% HP, +0.5с кд щита"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.3)
                .shieldCooldownPercent(50.0)
                .build());

        register(UpgradeCard.builder(
                "poison_bullet",
                "ОТРАВА",
                "Пуля накладывает отравление I на 2с, +70% урона, -30% перезарядки, -1 патрон"
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .damageMultiplier(1.7)
                .reloadPercent(-30.0)
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
