package com.rounds.zero.game.combat;

import net.minecraft.entity.Entity;
import com.rounds.zero.mixin.MobEntityAccessor;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SilverfishEntity;

public final class ParasiteSilverfishHelper {
    public static final String TAG = "rounds_zero_parasite_silverfish";

    private ParasiteSilverfishHelper() {
    }

    public static boolean isParasiteSilverfish(Entity entity) {
        return entity.getCommandTags().contains(TAG);
    }

    public static void configure(SilverfishEntity fish) {
        if (!isParasiteSilverfish(fish)) {
            return;
        }

        ((MobEntityAccessor) (MobEntity) fish).roundsZero$getGoalSelector().getGoals().removeIf(prioritizedGoal -> {
            Goal goal = prioritizedGoal.getGoal();
            String goalName = goal.getClass().getSimpleName();
            return "WanderAndInfestGoal".equals(goalName) || "CallForHelpGoal".equals(goalName);
        });
    }
}
