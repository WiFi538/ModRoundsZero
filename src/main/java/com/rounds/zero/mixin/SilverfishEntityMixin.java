package com.rounds.zero.mixin;

import com.rounds.zero.game.combat.ParasiteSilverfishHelper;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SilverfishEntity.class)
public abstract class SilverfishEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void roundsZero$ensureBlockGoalsRemoved(CallbackInfo ci) {
        SilverfishEntity self = (SilverfishEntity) (Object) this;
        if (ParasiteSilverfishHelper.isParasiteSilverfish(self)) {
            ParasiteSilverfishHelper.configure(self);
        }
    }

    @Inject(method = "getPathfindingFavor", at = @At("HEAD"), cancellable = true)
    private void roundsZero$noStonePathfinding(BlockPos pos, WorldView world, CallbackInfoReturnable<Float> cir) {
        if (ParasiteSilverfishHelper.isParasiteSilverfish((SilverfishEntity) (Object) this)) {
            cir.setReturnValue(0.0F);
        }
    }
}
