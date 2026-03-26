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

        // сколько помещается в описание "Пуля создаёт облако яда, -20% скорости стрельбы, +0.5с" 54 символа

        register(UpgradeCard.builder(
                "ahui_plan",
                "АХУЕННЫЙ ПЛАН",
                "+100% DMG, -100% S_Attack, +0.5с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/ahui_plan.png")
                .damageMultiplier(2.0)
                .fireRatePercent(-100.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "combine",
                "КОМБАЙН",
                "+100% DMG, +0.25с Reload, -2 Ammo"
        ).texturePath("rounds_zero:textures/gui/cards/combine.png")
                .damageMultiplier(2.0)
                .reloadTicksFlat(5L)
                .flatAmmoBonus(-2)
                .build());

        register(UpgradeCard.builder(
                "bogatyr",
                "БОГАТЫРЬ",
                "-25% Reload Shield, +50% HP"
        ).texturePath("rounds_zero:textures/gui/cards/bogatyr.png")
                .shieldCooldownPercent(-25.0)
                .maxHealthMultiplier(1.5)
                .build());

        register(UpgradeCard.builder(
                "skorostrel",
                "СКОРОСТРЕЛ",
                "+250% S_Bullet, -30% S_Attack, +2с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/skorostrel.png")
                .bulletSpeedMultiplier(3.5)
                .fireRatePercent(-30.0)
                .reloadTicksFlat(40L)
                .build());

        register(UpgradeCard.builder(
                "glass_cannon",
                "СТЕКЛЯННАЯ ПУШКА",
                "+100% DMG, -60% HP, -0.5с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/glass_cannon.png")
                .damageMultiplier(2.0)
                .maxHealthMultiplier(0.4)
                .reloadTicksFlat(-10L)
                .build());

        register(UpgradeCard.builder(
                "your_mama",
                "ТВОЯ МАМА",
                "+80% HP, -1с Reload Bullet and Shield"
        ).texturePath("rounds_zero:textures/gui/cards/your_mama.png")
                .maxHealthMultiplier(1.8)
                .reloadTicksFlat(-20L)
                .shieldCooldownTicksFlat(-20)
                .build());

        register(UpgradeCard.builder(
                "fast_gonzales",
                "БЫСТРЫЙ ГОНЗАЛЕС",
                "-50% Reload"
        ).texturePath("rounds_zero:textures/gui/cards/fast_gonzales.png")
                .reloadPercent(-50.0)
                .build());

        register(UpgradeCard.builder(
                "obossivatel",
                "ОБОССЫВАТЕЛЬ",
                "+1000% S_Bullet, +17 Ammo, -50% DMG, +3с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/obossivatel.png")
                .fireRatePercent(1000.0)
                .flatAmmoBonus(17)
                .damageMultiplier(0.5)
                .reloadTicksFlat(60L)
                .build());

        register(UpgradeCard.builder(
                "tank",
                "ТАНК",
                "+100% HP, +20% DMG, -25% S_Attack, +0.5с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/tank.png")
                .maxHealthMultiplier(2.0)
                .damageMultiplier(1.2)
                .fireRatePercent(-25.0)
                .reloadTicksFlat(10L)
                .build());

        register(UpgradeCard.builder(
                "raskrutka",
                "РАСКРУТКА",
                "+100% S_Bullet, +50% S_Attack, +1с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/raskrutka.png")
                .bulletSpeedMultiplier(2.0)
                .fireRatePercent(50.0)
                .reloadTicksFlat(20L)
                .build());

        register(UpgradeCard.builder(
                "tochniy_vystrel",
                "ТОЧНЫЙ ВЫСТРЕЛ",
                "+30% HP, +150% S_Bullet, +0.25с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/tochniy_vystrel.png")
                .maxHealthMultiplier(1.3)
                .bulletSpeedMultiplier(2.5)
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "ice_bullets",
                "ЛЕДЯНЫЕ ПУЛИ",
                "Пули замедляют на 2с. +0.25с Reload"
        ).texturePath("rounds_zero:textures/gui/cards/ice_bullets.png")
                .reloadTicksFlat(5L)
                .build());

        register(UpgradeCard.builder(
                "healing_field",
                "ЛЕЧАЩЕЕ ПОЛЕ",
                "Щит дает Regen, +30% HP, +1с Reload Shield"
        ).texturePath("rounds_zero:textures/gui/cards/healing_field.png")
                .maxHealthMultiplier(1.3)
                .shieldCooldownTicksFlat(20)
                .build());

        register(UpgradeCard.builder(
                "poison_bullet",
                "ОТРАВА",
                "Пуля накладывает отравление, +10% DMG, -20% Reload"
        ).texturePath("rounds_zero:textures/gui/cards/poison_bullet.png")
                .damageMultiplier(1.1)
                .reloadPercent(-20.0)
                .build());

        register(UpgradeCard.builder(
                "poison_cloud",
                "ЯДОВИТОЕ ОБЛАКО",
                "Пуля создаёт облако яда, -20% S_Attack, +0.5с Reload"
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
