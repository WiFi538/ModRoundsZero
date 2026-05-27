package com.rounds.zero.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ModWeaponItems {
    private static final String PISTOL_TAG = "RoundsZeroPistol";

    private ModWeaponItems() {
    }

    public static ItemStack createPistol() {
        ItemStack stack = new ItemStack(ModItems.PISTOL);
        stack.setCustomName(Text.literal("Пистолет").formatted(Formatting.GOLD));

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(PISTOL_TAG, true);
        nbt.putBoolean("Unbreakable", true);

        return stack;
    }

    public static boolean isPistol(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (!stack.isOf(ModItems.PISTOL)) {
            return false;
        }

        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(PISTOL_TAG);
    }
}
