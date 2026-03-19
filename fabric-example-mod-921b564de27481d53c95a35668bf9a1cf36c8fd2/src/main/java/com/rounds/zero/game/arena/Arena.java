package com.rounds.zero.game.arena;

import net.minecraft.util.math.BlockPos;

public class Arena {
    private final String name;
    private final BlockPos redSpawn;
    private final BlockPos blueSpawn;

    public Arena(String name, BlockPos redSpawn, BlockPos blueSpawn) {
        this.name = name;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
    }

    public String getName() {
        return name;
    }

    public BlockPos getRedSpawn() {
        return redSpawn;
    }

    public BlockPos getBlueSpawn() {
        return blueSpawn;
    }
}
