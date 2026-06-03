package com.rounds.zero.game.rules;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;

public final class WorldInteractionHandler {
    private WorldInteractionHandler() {
    }

    public static boolean isRestrictedInteraction(BlockState state, BlockEntity blockEntity) {
        Block block = state.getBlock();

        if (block instanceof ChestBlock
                || block instanceof TrappedChestBlock
                || block instanceof BarrelBlock
                || block instanceof AbstractFurnaceBlock
                || block instanceof AnvilBlock
                || block instanceof FenceGateBlock
                || block instanceof TrapdoorBlock
                || block instanceof SignBlock
                || block instanceof HangingSignBlock) {
            return true;
        }

        if (blockEntity instanceof LockableContainerBlockEntity || blockEntity instanceof SignBlockEntity) {
            return true;
        }

        return false;
    }
}
