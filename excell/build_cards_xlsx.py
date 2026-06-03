"""Generate rounds_zero_cards.xlsx using only stdlib (no openpyxl)."""
import zipfile
from pathlib import Path
from xml.sax.saxutils import escape

OUTPUT = Path(__file__).parent / "rounds_zero_cards.xlsx"

BASE_STATS = (
    "База без карт: 3 патрона, урон пули 4.0 (2❤), скорость пули 1.65, "
    "перезарядка 3 с, задержка выстрела 1 с, HP 20 (10❤), щит 2 с / кулдаун 5 с."
)

CARDS = [
    {
        "id": "cursed_bullet",
        "title": "ПРОКЛЯТАЯ ПУЛЯ",
        "ui_desc": "Попадание заставляет цель светиться. При смерти цель взрывается (блоки не ломаются).",
        "numeric": "Скорость пули ×1.1; +2 патрона; перезарядка +2 с",
        "effect": "При попадании: тег проклятия + Свечение 10 с. При смерти: взрыв сила 2.8 (блоки не ломаются). Тег снимается при смерти/конце раунда/лобби.",
        "stacks": "Да (числа)",
        "stack_how": "Числа: перемножение/сумма. Эффект: флаг 1 раз; свечение max(200 тиков), взрыв max(2.8) — копии не усиливают сверх первой.",
        "synergy": "—",
    },
    {
        "id": "parasite",
        "title": "ПАРАЗИТ",
        "ui_desc": "Убийство пулей: 2 чешуйницы. Убийство чешуйницей: ещё 1.",
        "numeric": "Урон ×1.2; скорость стрельбы −10%; +1 патрон; HP ×1.3",
        "effect": "Убийство игрока пулей → 2 чешуйницы (урон 4). Убийство игрока чешуйницей → +1 чешуйница.",
        "stacks": "Частично",
        "stack_how": "Только числовые модификаторы. Флаг паразита не суммируется.",
        "synergy": "Паразит + Призыватель (см. лист «Синергии»)",
    },
    {
        "id": "jackpot",
        "title": "ДЖЕКПОТ",
        "ui_desc": "50% шанс случайного эффекта на 2 с — тебе или цели.",
        "numeric": "+5 патронов; скорость стрельбы +20%; скорость пули ×0.9; урон ×1.1; перезарядка +1 с",
        "effect": "50% при попадании: случайный эффект 2 с на стрелка ИЛИ цель (скорость, яд, слепота, левитация и др.).",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Шанс max(50%), длительность max(40 тиков) — не суммируются.",
        "synergy": "Деп + Джекпот (см. лист «Синергии»)",
    },
    {
        "id": "thor",
        "title": "ТОР",
        "ui_desc": "15% шанс молнии при попадании.",
        "numeric": "HP ×1.2; +1 патрон; скорость стрельбы −10%; кулдаун щита −1 с",
        "effect": "15%: косметическая молния + 6.0 урона (3❤) по цели.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Шанс max(15%), урон молнии max(6.0).",
        "synergy": "Огненный выстрел + Тор (см. лист «Синергии»)",
    },
    {
        "id": "under_speed",
        "title": "ПОД СПИДАМИ",
        "ui_desc": "При попадании по врагу — Скорость II на 2 с.",
        "numeric": "Скорость пули ×1.2; скорость стрельбы +10%; HP ×0.8; перезарядка +1 с",
        "effect": "При попадании по врагу (другая команда): Скорость II стрелку 2 с.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Длительность/уровень через max — копии не усиливают.",
        "synergy": "—",
    },
    {
        "id": "dep",
        "title": "ДЕП",
        "ui_desc": "50%: +50% урона по цели ИЛИ +20% урона по себе.",
        "numeric": "HP ×0.9; скорость стрельбы +20%; скорость пули ×1.1; +3 патрона",
        "effect": "50%: доп. урон 50% от урона пули по цели ИЛИ 20% по себе.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Шанс и множители через max.",
        "synergy": "Деп + Джекпот",
    },
    {
        "id": "time_jump",
        "title": "ВРЕМЕННОЙ СКАЧОК",
        "ui_desc": "15%: телепорт цели на 2 блока.",
        "numeric": "Скорость пули ×1.1; скорость стрельбы −10%; +1 патрон; перезарядка +1 с",
        "effect": "15%: телепорт цели на 2 блока (вбок/вверх), если нет коллизии.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Шанс max(15%).",
        "synergy": "—",
    },
    {
        "id": "summoner",
        "title": "ПРИЗЫВАТЕЛЬ",
        "ui_desc": "При щите призывает зомби (лимит 6).",
        "numeric": "Кулдаун щита +2 с; HP ×0.9",
        "effect": "При активации щита: зомби (урон 6 / 3❤, лимит 6, не бьёт хозяина, не горит).",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Лимит max(6), урон зомби max(6.0).",
        "synergy": "Паразит + Призыватель",
    },
    {
        "id": "triple_shot",
        "title": "ТРОЙНОЙ ВЫСТРЕЛ",
        "ui_desc": "3 пули за 1 патрон.",
        "numeric": "Скорость пули ×1.05; скорость стрельбы −5%; −1 патрон; перезарядка +2 с; HP ×1.05",
        "effect": "3 пули за 1 патрон (разброс ~6°, смещение). Эффекты на каждую пулю.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Пуль всегда max=3 (вторая копия не даёт 6).",
        "synergy": "—",
    },
    {
        "id": "sniper",
        "title": "СНАЙПЕР",
        "ui_desc": "−5 патронов (мин. 1), +100% урон/скорость пули, −50% скорость стрельбы.",
        "numeric": "Урон ×2; скорость пули ×2; скорость стрельбы −50%; HP ×0.7; перезарядка +2 с; кулдаун щита −2 с",
        "effect": "После всех карт: −5 патронов (если ≤5 → остаётся 1).",
        "stacks": "Да",
        "stack_how": "Числа перемножаются. Штраф −5 патронов применяется снова за каждую копию снайпера.",
        "synergy": "—",
    },
    {
        "id": "fire_shot",
        "title": "ОГНЕННЫЙ ВЫСТРЕЛ",
        "ui_desc": "Сильное горение + доп. огненный урон.",
        "numeric": "Перезарядка +1 с; скорость пули ×1.1; скорость стрельбы −10%; HP ×1.2; кулдаун щита +1 с",
        "effect": "Поджог 5 с + доп. урон 4.0 (2❤) при попадании.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Огонь max(100 тиков), доп. урон max(4.0).",
        "synergy": "Огненный выстрел + Тор",
    },
    {
        "id": "ahui_plan",
        "title": "НАДЕЖНЫЙ ПЛАН",
        "ui_desc": "Урон +100%, скорость стрельбы в 2 раза ниже.",
        "numeric": "Урон ×2; скорость стрельбы −100%; перезарядка +0.5 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Все множители перемножаются по порядку.",
        "synergy": "—",
    },
    {
        "id": "combine",
        "title": "КОМБАЙН",
        "ui_desc": "Урон +100%, −2 патрона.",
        "numeric": "Урон ×2; перезарядка +0.25 с; −2 патрона",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение / сумма патронов.",
        "synergy": "—",
    },
    {
        "id": "bogatyr",
        "title": "БОГАТЫРЬ",
        "ui_desc": "HP +50%, кулдаун щита −25%.",
        "numeric": "Кулдаун щита −25%; HP ×1.5; скорость стрельбы −5%; урон ×1.05",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение / проценты.",
        "synergy": "Лечащее поле + Богатырь/Твоя мама/Танк",
    },
    {
        "id": "skorostrel",
        "title": "СКОРОСТРЕЛ",
        "ui_desc": "Скорость пуль +175%, скорость стрельбы −30%.",
        "numeric": "Скорость пули ×2.75; скорость стрельбы +30%; HP ×0.8; перезарядка +2 с; кулдаун щита +1 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение / проценты.",
        "synergy": "—",
    },
    {
        "id": "glass_cannon",
        "title": "СТЕКЛЯННАЯ ПУШКА",
        "ui_desc": "Урон +100%, HP −60%.",
        "numeric": "Урон ×2; HP ×0.4; перезарядка −0.5 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "HP сильно падает при нескольких копиях (×0.4 каждая).",
        "synergy": "—",
    },
    {
        "id": "your_mama",
        "title": "ТВОЯ МАМА",
        "ui_desc": "HP +80%, перезарядка −1 с, кулдаун щита −1 с.",
        "numeric": "HP ×1.8; перезарядка −1 с; кулдаун щита −1 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение HP.",
        "synergy": "Лечащее поле + Богатырь/Твоя мама/Танк",
    },
    {
        "id": "fast_gonzales",
        "title": "БЫСТРЫЙ ГОНЗАЛЕС",
        "ui_desc": "Перезарядка −50%.",
        "numeric": "Перезарядка −50%; кулдаун щита −10%",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Проценты применяются по очереди.",
        "synergy": "—",
    },
    {
        "id": "obossivatel",
        "title": "ОБОССЫВАТЕЛЬ",
        "ui_desc": "Скорость стрельбы ~×12, +17 патронов, урон −50%.",
        "numeric": "Скорость стрельбы +1100%; +17 патронов; урон ×0.5; перезарядка +3 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Fire rate применяется последовательно — копии очень сильны.",
        "synergy": "—",
    },
    {
        "id": "tank",
        "title": "ТАНК",
        "ui_desc": "HP +100%, урон +20%.",
        "numeric": "HP ×2; урон ×1.2; скорость стрельбы −25%; перезарядка +0.5 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение HP.",
        "synergy": "Лечащее поле + Богатырь/Твоя мама/Танк",
    },
    {
        "id": "tochniy_vystrel",
        "title": "ТОЧНЫЙ ВЫСТРЕЛ",
        "ui_desc": "HP +30%, скорость пуль +150%.",
        "numeric": "HP ×1.3; скорость пули ×2.5; перезарядка +0.25 с",
        "effect": "Только модификаторы статов.",
        "stacks": "Да (только числа)",
        "stack_how": "Перемножение.",
        "synergy": "—",
    },
    {
        "id": "ice_bullets",
        "title": "ЛЕДЯНЫЕ ПУЛИ",
        "ui_desc": "Замедление цели.",
        "numeric": "Перезарядка +0.25 с",
        "effect": "Замедление II на (20×N+20) тиков. N = число карт.",
        "stacks": "Да (эффект)",
        "stack_how": "N=1→2с, N=2→3с, N=3→4с. Уровень замедления всегда II.",
        "synergy": "—",
    },
    {
        "id": "healing_field",
        "title": "ЛЕЧАЩЕЕ ПОЛЕ",
        "ui_desc": "При щите — лечение союзникам.",
        "numeric": "HP ×1.3; кулдаун щита +1 с",
        "effect": "При щите: поле радиус 1 блок, 1.25 с, реген союзникам; кулдаун лечения 8 с.",
        "stacks": "Да (эффект)",
        "stack_how": "Длительность регена: 50+(N−1)×10 тиков. Радиус не растёт.",
        "synergy": "Лечащее поле + Богатырь/Твоя мама/Танк",
    },
    {
        "id": "poison_bullet",
        "title": "ОТРАВА",
        "ui_desc": "Отравление при попадании.",
        "numeric": "Урон ×1.1; перезарядка −20%",
        "effect": "Яд III на 72+(N−1)×24 тиков.",
        "stacks": "Да (эффект)",
        "stack_how": "N=1→3.6с, N=2→4.8с. Уровень яда всегда III.",
        "synergy": "Отрава + Ядовитое облако",
    },
    {
        "id": "poison_cloud",
        "title": "ЯДОВИТОЕ ОБЛАКО",
        "ui_desc": "Облако яда при попадании.",
        "numeric": "Скорость стрельбы −20%; перезарядка +0.5 с",
        "effect": "Поле: радиус 3, яд II, бьёт всех в радиусе. Время жизни 60+(N−1)×20 тиков.",
        "stacks": "Да (эффект)",
        "stack_how": "+1 с жизни облака за копию. Радиус фикс. (синергия +2 радиус).",
        "synergy": "Отрава + Ядовитое облако",
    },
    {
        "id": "blindness_bullets",
        "title": "ОСЛЕПЛЕНИЕ",
        "ui_desc": "30% шанс ослепить.",
        "numeric": "Скорость стрельбы +10%",
        "effect": "30% слепота на 40+(N−1)×20 тиков.",
        "stacks": "Да (частично)",
        "stack_how": "Длительность растёт; шанс всегда 30%.",
        "synergy": "—",
    },
    {
        "id": "bomb_shield",
        "title": "БОМБИЧЕСКИЙ НАСТРОЙ",
        "ui_desc": "Взрыв при щите, союзников не бьёт.",
        "numeric": "HP ×1.2",
        "effect": "При щите: взрыв 5❤, радиус 1.5, союзники в безопасности; +5 с к кулдауну щита.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Урон/радиус/доп.кулдаун через max.",
        "synergy": "—",
    },
    {
        "id": "kaboom",
        "title": "БАБАХ",
        "ui_desc": "Взрыв пули 1❤, бьёт всех.",
        "numeric": "Скорость стрельбы −10%; скорость пули ×0.9; урон ×1.1; HP ×0.9; перезарядка +1 с; кулдаун щита +1 с",
        "effect": "Взрыв при попадании: 1❤, радиус 1.5, урон всем включая себя/союзников.",
        "stacks": "Частично",
        "stack_how": "Числа стакаются. Урон/радиус взрыва max — не суммируются.",
        "synergy": "—",
    },
]

SYNERGIES = [
    ("Отрава + Ядовитое облако", "poison_bullet + poison_cloud", "Радиус облака +2 (итого 5). Длительность яда от пули +40 тиков (+2 с)."),
    ("Паразит + Призыватель", "parasite + summoner", "Чешуйницы не бьют призывателя. Зомби при смерти и при убийстве игрока → 1 чешуйница."),
    ("Деп + Джекпот", "dep + jackpot", "3% при каждом попадании пули по игроку — мгновенная смерть (не связано с проком Депа)."),
    ("Лечащее поле + Богатырь / Твоя мама / Танк", "healing_field + (bogatyr | your_mama | tank)", "Время поля +2 с. В поле: +2❤ сразу + Health Boost до конца раунда/смерти."),
    ("Огненный выстрел + Тор", "fire_shot + thor", "При попадании (существо/блок): поджог зоны 3×3. Молния только по обычному проку Тора (15%)."),
]

STACK_RULES = [
    ("damageMultiplier, bulletSpeedMultiplier, maxHealthMultiplier, bulletSizeMultiplier", "Перемножаются по порядку взятия карт"),
    ("flatAmmoBonus", "Суммируются"),
    ("fireRatePercent", "По очереди к кулдауну выстрела: +% быстрее, −% медленнее"),
    ("reloadPercent", "По очереди к длительности перезарядки"),
    ("reloadTicksFlat, shieldCooldownTicksFlat", "Суммируются (тики)"),
    ("shieldCooldownPercent", "По очереди к кулдауну щита"),
    ("Минимумы после расчёта", "HP ≥ 2 (1❤), патроны ≥ 1, урон пули ≥ 0.1"),
    ("Снайпер (пост-обработка)", "После всех карт: −5 патронов; если было ≤5 → 1 патрон"),
]


def col_name(index: int) -> str:
    name = ""
    while index >= 0:
        name = chr(65 + index % 26) + name
        index = index // 26 - 1
    return name


def sheet_xml(rows: list[list[str]]) -> str:
    lines = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">',
        "<sheetData>",
    ]
    for r_idx, row in enumerate(rows, start=1):
        lines.append(f'<row r="{r_idx}">')
        for c_idx, value in enumerate(row):
            cell = f"{col_name(c_idx)}{r_idx}"
            text = escape(value) if value is not None else ""
            lines.append(f'<c r="{cell}" t="inlineStr"><is><t>{text}</t></is></c>')
        lines.append("</row>")
    lines.append("</sheetData></worksheet>")
    return "".join(lines)


def build_workbook(path: Path) -> None:
    cards_header = [
        "ID",
        "Название",
        "Описание в игре (UI)",
        "Числовые модификаторы (за 1 копию)",
        "Эффект в коде",
        "Стакается?",
        "Как стакается",
        "Синергии",
    ]
    cards_rows = [cards_header] + [
        [
            c["id"],
            c["title"],
            c["ui_desc"],
            c["numeric"],
            c["effect"],
            c["stacks"],
            c["stack_how"],
            c["synergy"],
        ]
        for c in CARDS
    ]

    syn_header = ["Синергия", "ID карт", "Эффект в коде"]
    syn_rows = [syn_header] + [list(s) for s in SYNERGIES]

    rules_header = ["Параметр", "Правило"]
    rules_rows = [rules_header, [BASE_STATS, ""]] + list(STACK_RULES)

    sheets = [
        ("Карточки", cards_rows),
        ("Синергии", syn_rows),
        ("Правила стака", rules_rows),
    ]

    with zipfile.ZipFile(path, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr(
            "[Content_Types].xml",
            '<?xml version="1.0" encoding="UTF-8"?>'
            '<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">'
            '<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>'
            '<Default Extension="xml" ContentType="application/xml"/>'
            '<Override PartName="/xl/workbook.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>'
            '<Override PartName="/xl/worksheets/sheet1.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>'
            '<Override PartName="/xl/worksheets/sheet2.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>'
            '<Override PartName="/xl/worksheets/sheet3.xml" '
            'ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>'
            "</Types>",
        )
        zf.writestr(
            "_rels/.rels",
            '<?xml version="1.0" encoding="UTF-8"?>'
            '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
            '<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" '
            'Target="xl/workbook.xml"/>'
            "</Relationships>",
        )
        sheet_entries = []
        for i, (title, _) in enumerate(sheets, start=1):
            sheet_entries.append(
                f'<sheet name="{escape(title)}" sheetId="{i}" r:id="rId{i}"/>'
            )
        zf.writestr(
            "xl/workbook.xml",
            '<?xml version="1.0" encoding="UTF-8"?>'
            '<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" '
            'xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">'
            f"<sheets>{''.join(sheet_entries)}</sheets></workbook>",
        )
        wb_rels = []
        for i in range(1, len(sheets) + 1):
            wb_rels.append(
                f'<Relationship Id="rId{i}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" '
                f'Target="worksheets/sheet{i}.xml"/>'
            )
        zf.writestr(
            "xl/_rels/workbook.xml.rels",
            '<?xml version="1.0" encoding="UTF-8"?>'
            '<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">'
            + "".join(wb_rels)
            + "</Relationships>",
        )
        for i, (_, rows) in enumerate(sheets, start=1):
            zf.writestr(f"xl/worksheets/sheet{i}.xml", sheet_xml(rows))


if __name__ == "__main__":
    build_workbook(OUTPUT)
    print(f"Wrote {OUTPUT}")
