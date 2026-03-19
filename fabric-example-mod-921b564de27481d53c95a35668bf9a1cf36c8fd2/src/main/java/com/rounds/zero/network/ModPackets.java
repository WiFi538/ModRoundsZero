package com.rounds.zero.network;

import com.rounds.zero.RoundsZero;
import net.minecraft.util.Identifier;

public final class ModPackets {
    public static final Identifier SHIELD_USE = new Identifier(RoundsZero.MOD_ID, "shield_use");
    public static final Identifier RELOAD_WEAPON = new Identifier(RoundsZero.MOD_ID, "reload_weapon");

    private ModPackets() {
    }
}