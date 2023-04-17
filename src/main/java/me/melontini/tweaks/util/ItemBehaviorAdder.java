package me.melontini.tweaks.util;

import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

public class ItemBehaviorAdder {

    public static void init() {
        ((ThrowableBehaviorDuck) Items.BONE_MEAL).mTweaks$setBehavior((stack, flyingItemEntity, world, user, hitResult) -> {
            if (!world.isClient && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult result = (BlockHitResult) hitResult;
                BlockPos blockPos = result.getBlockPos();
                BlockPos blockPos2 = blockPos.offset(result.getSide());

                if (BoneMealItem.useOnFertilizable(stack, world, blockPos)) {
                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);

                } else {
                    BlockState blockState = world.getBlockState(blockPos);
                    boolean bl = blockState.isSideSolidFullSquare(world, blockPos, result.getSide());
                    if (bl && BoneMealItem.useOnGround(stack, world, blockPos2, result.getSide())) {
                        world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos2, 0);
                    }
                }
                flyingItemEntity.discard();
            }
        });
    }
}
