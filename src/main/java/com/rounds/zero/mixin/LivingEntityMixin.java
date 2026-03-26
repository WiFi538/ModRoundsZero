package com.rounds.zero.mixin;

import com.rounds.zero.RoundsZero;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(ServerPlayerEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "damage", at = @At("TAIL"))
    private void roundsZero$removeVanillaInvulnerability(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (!RoundsZero.GAME_MANAGER.hasTeam(player)) {
            return;
        }

        resetIfExists(player, "timeUntilRegen");
        resetIfExists(player, "hurtTime");
        resetIfExists(player, "maxHurtTime");
    }

    private static void resetIfExists(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            return;
        }

        try {
            field.setAccessible(true);
            Class<?> type = field.getType();

            if (type == int.class) {
                field.setInt(target, 0);
            } else if (type == float.class) {
                field.setFloat(target, 0.0F);
            } else if (type == double.class) {
                field.setDouble(target, 0.0D);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;

        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        return null;
    }
}