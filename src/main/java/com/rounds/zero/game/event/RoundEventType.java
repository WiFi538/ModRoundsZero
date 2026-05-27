package com.rounds.zero.game.event;

public enum RoundEventType {
    PARTICLE_ACCELERATION("УСКОРЕНИЕ ЧАСТИЦ", "Скорость III до конца раунда. Скорость пуль увеличена вдвое."),
    DARKNESS("ТЬМА", "Слепота у всех игроков до конца раунда."),
    RADIATION("РАДИАЦИЯ", "Отравление I у всех игроков до конца раунда."),
    COSMONAUTS("КОСМОНАВТЫ", "Прыгучесть II и плавное падение до конца раунда."),
    AURA("АУРА", "Невидимость и подсветка у всех до конца раунда."),
    OLYMPUS_WRATH("ГНЕВ ОЛИМПА", "Каждые 7 секунд случайного игрока бьёт молния на 3 сердца."),
    INVASION("НАШЕСТВИЕ", "Каждые 3 секунды рядом с каждым игроком появляется зомби.");

    private final String displayName;
    private final String chatDescription;

    RoundEventType(String displayName, String chatDescription) {
        this.displayName = displayName;
        this.chatDescription = chatDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChatDescription() {
        return chatDescription;
    }
}
