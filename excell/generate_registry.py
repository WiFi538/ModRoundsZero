# -*- coding: utf-8 -*-
import json
from pathlib import Path


def java_string(value: str) -> str:
    return json.dumps(value, ensure_ascii=False)

# Manual numeric mapping from Pravki.xlsx (heart = 2 HP on base 20)
CARDS = {
    "cursed_bullet": {
        "title": "ПРОКЛЯТАЯ ПУЛЯ",
        "texture": "rounds_zero:textures/gui/cards/poison_bullet.png",
        "desc": "При попадании по цели, она начинает светиться и после смерти взрывается.\n§a+2 пули, +20% скорость пули§r\n§c+2с reload§r",
        "flatAmmoBonus": 2, "bulletSpeedMultiplier": 1.2, "reloadTicksFlat": 40,
    },
    "parasite": {
        "title": "ПАРАЗИТ",
        "texture": "rounds_zero:textures/gui/cards/poison_cloud.png",
        "desc": "При убийстве спавнятся чешуйницы.\n§a+1 пуля, +10% DMG§r\n§c-2ХП§r",
        "damageMultiplier": 1.1, "flatAmmoBonus": 1, "maxHealthMultiplier": 0.8,
    },
    "jackpot": {
        "title": "ДЖЕКПОТ",
        "texture": "rounds_zero:textures/gui/cards/blindness_bullets.png",
        "desc": "55% шанс случайного эффекта на 3 с — тебе или цели.\n§a+5 пуль, +10% скорость пули, +10% скорость атаки§r\n§c-2ХП, -10% DMG, +2с reload§r",
        "flatAmmoBonus": 5, "fireRatePercent": 10.0, "bulletSpeedMultiplier": 1.1,
        "maxHealthMultiplier": 0.8, "damageMultiplier": 0.9, "reloadTicksFlat": 40,
    },
    "thor": {
        "title": "ТОР",
        "texture": "rounds_zero:textures/gui/cards/ice_bullets.png",
        "desc": "25% шанс молнии при попадании.\n§a+5ХП, +1 пуля, +10% скорость пули§r\n§c-10% скорость атаки§r",
        "maxHealthMultiplier": 1.5, "flatAmmoBonus": 1, "fireRatePercent": -10.0, "bulletSpeedMultiplier": 1.1,
    },
    "under_speed": {
        "title": "ПОД СПИДАМИ",
        "texture": "rounds_zero:textures/gui/cards/raskrutka.png",
        "desc": "При попадании по врагу — Скорость II на 2 с.\n§a+25% скорость пули, +20% скорость атаки, +1 пуля§r\n§c-20% урон, +1с reload§r",
        "bulletSpeedMultiplier": 1.25, "fireRatePercent": 20.0, "flatAmmoBonus": 1,
        "damageMultiplier": 0.8, "reloadTicksFlat": 20,
    },
    "dep": {
        "title": "ДЕП",
        "texture": "rounds_zero:textures/gui/cards/glass_cannon.png",
        "desc": "50%: +50% урона по цели ИЛИ +35% урона по себе.\n§a+2 пули, +20% скорости пуль, +10% скорости атаки§r\n§c-15% урона, -2ХП, +1с reload§r",
        "flatAmmoBonus": 2, "bulletSpeedMultiplier": 1.2, "fireRatePercent": 10.0,
        "damageMultiplier": 0.85, "maxHealthMultiplier": 0.8, "reloadTicksFlat": 20,
    },
    "time_jump": {
        "title": "ВРЕМЕННОЙ СКАЧОК",
        "texture": "rounds_zero:textures/gui/cards/tochniy_vystrel.png",
        "desc": "30%: телепорт цели на 2 блока.\n§a+20% скорости пуль, +10% скорости атаки, +1ХП§r\n§c-10% урона, +1с reload§r",
        "bulletSpeedMultiplier": 1.2, "fireRatePercent": 10.0, "maxHealthMultiplier": 1.1,
        "damageMultiplier": 0.9, "reloadTicksFlat": 20,
    },
    "summoner": {
        "title": "ПРИЗЫВАТЕЛЬ",
        "texture": "rounds_zero:textures/gui/cards/healing_field.png",
        "desc": "Призыв зомби при блоке.\n§a+10% скорость пули§r\n§c-1 пуля, -2ХП, +2с щит, -10% скорость атаки, -20% DMG§r",
        "maxHealthMultiplier": 0.8, "shieldCooldownTicksFlat": 40, "damageMultiplier": 0.8,
        "fireRatePercent": -10.0, "bulletSpeedMultiplier": 1.1, "flatAmmoBonus": -1,
    },
    "triple_shot": {
        "title": "ТРОЙНОЙ ВЫСТРЕЛ",
        "texture": "rounds_zero:textures/gui/cards/big_bullet.png",
        "desc": "3 пули за 1 патрон.\n§a+10% скорость пули, щит -1с§r\n§c-10% скорость атаки, -20% DMG, -1 пуля, reload +3с§r",
        "bulletSpeedMultiplier": 1.1, "fireRatePercent": -10.0, "damageMultiplier": 0.8,
        "flatAmmoBonus": -1, "reloadTicksFlat": 60, "shieldCooldownTicksFlat": -20,
    },
    "sniper": {
        "title": "СНАЙПЕР",
        "texture": "rounds_zero:textures/gui/cards/tochniy_vystrel.png",
        "desc": "Мощный одиночный выстрел на огромную дистанцию.\n§a+50% DMG, +50% скорость пули, щит -2с§r\n§c-5 пуль, -50% скорость стрельбы, -5 ХП, reload +3с§r",
        "damageMultiplier": 1.5, "bulletSpeedMultiplier": 1.5, "fireRatePercent": -50.0,
        "maxHealthMultiplier": 0.5, "reloadTicksFlat": 60, "shieldCooldownTicksFlat": -40,
    },
    "fire_shot": {
        "title": "ОГНЕННЫЙ ВЫСТРЕЛ",
        "texture": "rounds_zero:textures/gui/cards/glass_cannon.png",
        "desc": "Поджог при попадании по цели.\n§a+15% скорости пуль, +10% скорости атаки, reload -3с§r\n§c-20% DMG, -2 пули§r",
        "bulletSpeedMultiplier": 1.15, "fireRatePercent": 10.0, "damageMultiplier": 0.8,
        "flatAmmoBonus": -2, "reloadTicksFlat": -60,
    },
    "ahui_plan": {
        "title": "НАДЕЖНЫЙ ПЛАН",
        "texture": "rounds_zero:textures/gui/cards/ahui_plan.png",
        "desc": "Каждый выстрел становится медленным, но сокрушительным.\n§aDMG в 2 раза больше§r\n§cСкорость стрельбы в 2 раза ниже, reload +2с, -3 ХП§r",
        "damageMultiplier": 2.0, "fireRatePercent": -100.0, "reloadTicksFlat": 40, "maxHealthMultiplier": 0.7,
    },
    "combine": {
        "title": "КОМБАЙН",
        "texture": "rounds_zero:textures/gui/cards/combine.png",
        "desc": "Увеличивает убойную силу за счёт уменьшения боезапаса.\n§a+50% DMG§r\n§c-3 пули, +1с reload, -2 ХП§r",
        "damageMultiplier": 1.5, "reloadTicksFlat": 20, "flatAmmoBonus": -3, "maxHealthMultiplier": 0.8,
    },
    "bogatyr": {
        "title": "БОГАТЫРЬ",
        "texture": "rounds_zero:textures/gui/cards/bogatyr.png",
        "desc": "Значительно увеличивает выживаемость, ослабляя атаку.\n§a+5 ХП, щит -3с§r\n§c-25% DMG§r",
        "maxHealthMultiplier": 1.5, "shieldCooldownTicksFlat": -60, "damageMultiplier": 0.75,
    },
    "skorostrel": {
        "title": "СКОРОСТРЕЛ",
        "texture": "rounds_zero:textures/gui/cards/skorostrel.png",
        "desc": "Скорость пули и стрельбы в 2 раза выше.\n§aСкорость пули ×2, скорость стрельбы ×2, reload -2с, щит -1с§r\n§c-20% DMG§r",
        "bulletSpeedMultiplier": 2.0, "fireRatePercent": 100.0, "reloadTicksFlat": -40,
        "damageMultiplier": 0.8, "shieldCooldownTicksFlat": -20,
    },
    "glass_cannon": {
        "title": "СТЕКЛЯННАЯ ПУШКА",
        "texture": "rounds_zero:textures/gui/cards/glass_cannon.png",
        "desc": "Максимальный урон при критически низком здоровье.\n§aDMG в 2 раза больше§r\n§c-6 ХП§r",
        "damageMultiplier": 2.0, "maxHealthMultiplier": 0.4,
    },
    "your_mama": {
        "title": "ТВОЯ МАМА",
        "texture": "rounds_zero:textures/gui/cards/your_mama.png",
        "desc": "Огромный запас здоровья.\n§a+8 ХП§r\n§c-40% DMG, +2с щит, +2с reload§r",
        "maxHealthMultiplier": 1.8, "damageMultiplier": 0.6, "shieldCooldownTicksFlat": 40, "reloadTicksFlat": 40,
    },
    "fast_gonzales": {
        "title": "БЫСТРЫЙ ГОНЗАЛЕС",
        "texture": "rounds_zero:textures/gui/cards/fast_gonzales.png",
        "desc": "Максимально быстрое восстановление оружия и защиты.\n§areload в 2 раза быстрее, щит -1с§r",
        "reloadPercent": -50.0, "shieldCooldownTicksFlat": -20,
    },
    "obossivatel": {
        "title": "ОБОССЫВАТЕЛЬ",
        "texture": "rounds_zero:textures/gui/cards/obossivatel.png",
        "desc": "Невероятный темп стрельбы с огромным боекомплектом.\n§aСкорость стрельбы ×10, +17 пуль§r\n§c-50% DMG, +4с reload, -1 ХП§r",
        "fireRatePercent": 900.0, "flatAmmoBonus": 17, "damageMultiplier": 0.5,
        "reloadTicksFlat": 80, "maxHealthMultiplier": 0.9,
    },
    "tank": {
        "title": "ТАНК",
        "texture": "rounds_zero:textures/gui/cards/tank.png",
        "desc": "Несокрушимая огневая точка с медленной атакой.\n§a+10 ХП, +20% DMG, щит -1с§r\n§c-50% скорость стрельбы, -2 пули, reload +3с§r",
        "maxHealthMultiplier": 2.0, "damageMultiplier": 1.2, "fireRatePercent": -50.0,
        "flatAmmoBonus": -2, "reloadTicksFlat": 60, "shieldCooldownTicksFlat": -20,
    },
    "tochniy_vystrel": {
        "title": "ТОЧНЫЙ ВЫСТРЕЛ",
        "texture": "rounds_zero:textures/gui/cards/tochniy_vystrel.png",
        "desc": "Точный выстрел с высокой скоростью полёта пули.\n§aПули в 2 раза быстрее, +10% DMG§r\n§c-20% скорость атаки, +1с reload§r",
        "bulletSpeedMultiplier": 2.0, "fireRatePercent": -20.0, "damageMultiplier": 1.1, "reloadTicksFlat": 20,
    },
    "ice_bullets": {
        "title": "ЛЕДЯНЫЕ ПУЛИ",
        "texture": "rounds_zero:textures/gui/cards/ice_bullets.png",
        "desc": "Замедление цели при попадании.\n§a+3 ХП§r\n§cСкорость стрельбы -10%, скорость пули -10%, reload +1с§r",
        "fireRatePercent": -10.0, "bulletSpeedMultiplier": 0.9, "reloadTicksFlat": 20, "maxHealthMultiplier": 1.3,
    },
    "healing_field": {
        "title": "ЛЕЧАЩЕЕ ПОЛЕ",
        "texture": "rounds_zero:textures/gui/cards/healing_field.png",
        "desc": "Активируй зону лечения при блоке.\n§a+3 ХП§r\n§c+2с щит, -15% DMG§r",
        "maxHealthMultiplier": 1.3, "shieldCooldownTicksFlat": 40, "damageMultiplier": 0.85,
    },
    "poison_bullet": {
        "title": "ОТРАВА",
        "texture": "rounds_zero:textures/gui/cards/poison_bullet.png",
        "desc": "Отрави цель при попадании.\n§a+10% DMG от пули§r\n§cСкорость пули -10%, скорость атаки -10%, -1 пуля§r",
        "bulletSpeedMultiplier": 0.9, "fireRatePercent": -10.0, "flatAmmoBonus": -1, "damageMultiplier": 1.1,
    },
    "poison_cloud": {
        "title": "ЯДОВИТОЕ ОБЛАКО",
        "texture": "rounds_zero:textures/gui/cards/poison_cloud.png",
        "desc": "Ядовитая зона в месте попадания пули.\n§cСкорость стрельбы -20%, reload +2с, DMG -20%§r",
        "fireRatePercent": -20.0, "reloadTicksFlat": 40, "damageMultiplier": 0.8,
    },
    "blindness_bullets": {
        "title": "ОСЛЕПЛЕНИЕ",
        "texture": "rounds_zero:textures/gui/cards/blindness_bullets.png",
        "desc": "Ослепи цель при попадании с шансом 30%.\n§a+10% скорость атаки, +10% скорость пуль§r\n§c-1 пуля§r",
        "fireRatePercent": 10.0, "bulletSpeedMultiplier": 1.1, "flatAmmoBonus": -1,
    },
    "bomb_shield": {
        "title": "БОМБИЧЕСКИЙ НАСТРОЙ",
        "texture": "rounds_zero:textures/gui/cards/healing_field.png",
        "desc": "Взорвись при использовании щита (союзникам урон не наносится).\n§a+3 ХП§r\n§c+3с щит§r",
        "maxHealthMultiplier": 1.3, "shieldCooldownTicksFlat": 60,
    },
    "kaboom": {
        "title": "БАБАХ",
        "texture": "rounds_zero:textures/gui/cards/big_bullet.png",
        "desc": "Пуля взрывается при попадании!\n§a+10% скорость пуль, +10% DMG§r\n§c-20% скорость атаки, +1с reload§r",
        "fireRatePercent": -20.0, "bulletSpeedMultiplier": 1.1, "damageMultiplier": 1.1, "reloadTicksFlat": 20,
    },
}

ORDER = list(CARDS.keys())

lines = [
    "package com.rounds.zero.game.upgrade;",
    "",
    "import java.util.ArrayList;",
    "import java.util.Collections;",
    "import java.util.List;",
    "",
    "public final class UpgradeRegistry {",
    "    private static final List<UpgradeCard> ALL_CARDS = new ArrayList<>();",
    "",
    "    static {",
]

for cid in ORDER:
    c = CARDS[cid]
    lines.append(f'        register(UpgradeCard.builder(')
    lines.append(f'                "{cid}",')
    lines.append(f'                {java_string(c["title"])},')
    lines.append(f'                {java_string(c["desc"])}')
    lines.append(f'        ).texturePath("{c["texture"]}")')

    field_map = [
        ("flatAmmoBonus", "flatAmmoBonus"),
        ("damageMultiplier", "damageMultiplier"),
        ("bulletSpeedMultiplier", "bulletSpeedMultiplier"),
        ("bulletSizeMultiplier", "bulletSizeMultiplier"),
        ("maxHealthMultiplier", "maxHealthMultiplier"),
        ("fireRatePercent", "fireRatePercent"),
        ("shieldCooldownPercent", "shieldCooldownPercent"),
        ("reloadPercent", "reloadPercent"),
        ("reloadTicksFlat", "reloadTicksFlat"),
        ("shieldCooldownTicksFlat", "shieldCooldownTicksFlat"),
    ]
    for key, method in field_map:
        if key in c:
            val = c[key]
            if isinstance(val, float) and val == int(val):
                val = int(val)
            suffix = "L" if key.endswith("TicksFlat") or key == "shieldCooldownTicksFlat" else ""
            if isinstance(val, int) and suffix:
                lines.append(f"                .{method}({val}{suffix})")
            else:
                lines.append(f"                .{method}({val})")
    lines.append("                .build());")
    lines.append("")

lines.extend([
    "    }",
    "",
    "    private UpgradeRegistry() {",
    "    }",
    "",
    "    private static void register(UpgradeCard card) {",
    "        ALL_CARDS.add(card);",
    "    }",
    "",
    "    public static List<UpgradeCard> getAllCards() {",
    "        return Collections.unmodifiableList(ALL_CARDS);",
    "    }",
    "",
    "    public static UpgradeCard findById(String id) {",
    "        if (id == null) {",
    "            return null;",
    "        }",
    "",
    "        for (UpgradeCard card : ALL_CARDS) {",
    "            if (card.getId().equalsIgnoreCase(id)) {",
    "                return card;",
    "            }",
    "        }",
    "",
    "        return null;",
    "    }",
    "}",
    "",
])

out = Path(__file__).parent.parent / "src/main/java/com/rounds/zero/game/upgrade/UpgradeRegistry.java"
out.write_text("\n".join(lines), encoding="utf-8")
print("Wrote", out)
