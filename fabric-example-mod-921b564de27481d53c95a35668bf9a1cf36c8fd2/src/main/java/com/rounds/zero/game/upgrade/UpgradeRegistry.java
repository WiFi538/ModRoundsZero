package com.rounds.zero.game.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeRegistry {
    private static final List<UpgradeCard> ALL_CARDS = new ArrayList<>();

    static {
        register("combo", "КОМБАЙН", "+100% урона, -0.25с перезарядки, -2 к боезапасу");
        register("bogatyr", "БОГАТЫРЬ", "Щит: непрерывная блокировка урона 1с");
        register("oreh", "ОРЕШНИК", "Пули взрываются при попадании");
        register("skorostrel", "СКОРОСТРЕЛ", "+50% скорость пули, +0.5с скорость атаки, -0.25с перезарядки");
        register("zalp", "ЗАЛП", "Несколько пуль за раз");
        register("samonavodka", "САМОНАВОДКА", "Пуля наводится на врагов");
        register("vtoroe_prishestvie", "ВТОРОЕ ПРИШЕСТВИЕ", "Одно перерождение после смерти");
        register("otrava", "ОТРАВА", "Пули наносят урон в течение времени");
        register("big_bullet", "БОЛЬШАЯ ПУЛЯ", "Пуля больше");
        register("rikoshet", "РИКОШЕТ", "Пули умеют отскакивать");
        register("tochniy", "ТОЧНЫЙ ВЫСТРЕЛ", "+20% ХП, +100% скорость пули, +0.25с перезарядки");
        register("ochered", "ОЧЕРЕДЬ", "Стрельба очередью");
        register("ledyanie_puli", "ЛЕДЯНЫЕ ПУЛИ", "Замедление врагов пулями");
        register("teleport_shield", "ТЕЛЕПОРТ", "Щит телепортирует вверх врага");
        register("osleplenie", "ОСЛЕПЛЕНИЕ", "Пули оглушают противника");
    }

    private UpgradeRegistry() {
    }

    private static void register(String id, String title, String description) {
        ALL_CARDS.add(new UpgradeCard(id, title, description));
    }

    public static List<UpgradeCard> getAllCards() {
        return Collections.unmodifiableList(ALL_CARDS);
    }
}