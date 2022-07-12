package me.melontini.tweaks.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.recipe.Ingredient;

public class VillagerTemptGoal extends TemptGoal {

    private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = (new TargetPredicate()).setBaseMaxDistance(10.0).includeInvulnerable().includeTeammates().ignoreEntityTargetRules().includeHidden();

    public VillagerTemptGoal(VillagerEntity entity, double speed, Ingredient food, boolean canBeScared) {
        super(entity, speed, food, canBeScared);
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        } else {
            if (mob.getBrain().hasActivity(Activity.PANIC) || mob.getBrain().hasActivity(Activity.REST) || mob.getBrain().hasActivity(Activity.HIDE)) {
                return false;
            } else {
                this.closestPlayer = this.mob.world.getClosestPlayer(TEMPTING_ENTITY_PREDICATE, this.mob);
                return closestPlayer != null;
            }
        }
    }
}
