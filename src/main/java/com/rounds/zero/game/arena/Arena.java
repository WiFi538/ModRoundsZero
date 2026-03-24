package com.rounds.zero.game.arena;

import net.minecraft.util.math.BlockPos;

public class Arena {
    private final String name;
    private final BlockPos redSpawn;
    private final BlockPos blueSpawn;
    private final BlockPos greenSpawn;
    private final BlockPos yellowSpawn;

    public Arena(String name, BlockPos redSpawn, BlockPos blueSpawn, BlockPos greenSpawn, BlockPos yellowSpawn) {
        this.name = name;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.greenSpawn = greenSpawn;
        this.yellowSpawn = yellowSpawn;
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

    public BlockPos getGreenSpawn() {
        return greenSpawn;
    }

    public BlockPos getYellowSpawn() {
        return yellowSpawn;
    }
}