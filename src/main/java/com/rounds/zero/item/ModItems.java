package com.rounds.zero.item;

import com.rounds.zero.RoundsZero;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item PISTOL = register(
            "pistol",
            new Item(new FabricItemSettings().maxCount(1))
    );

    private ModItems() {
    }

    public static void register() {
        // Static initializer registers items.
    }

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RoundsZero.MOD_ID, id), item);
    }
}
