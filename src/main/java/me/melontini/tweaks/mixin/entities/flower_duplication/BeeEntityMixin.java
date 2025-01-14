package me.melontini.tweaks.mixin.entities.flower_duplication;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinRelatedConfigOption("beeFlowerDuplication")
@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin extends AnimalEntity {

    @Shadow
    @Nullable BlockPos flowerPos;
    @Shadow
    BeeEntity.PollinateGoal pollinateGoal;
    @Unique
    private int mTweaks$plantingCoolDown;

    protected BeeEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (Tweaks.CONFIG.beeFlowerDuplication) {
            BeeEntity bee = (BeeEntity) (Object) this;
            BeeEntity.PollinateGoal pollinateGoal = this.pollinateGoal;
            if (mTweaks$plantingCoolDown > 0) {
                --mTweaks$plantingCoolDown;
            }
            if (pollinateGoal != null) {
                if (pollinateGoal.isRunning() && pollinateGoal.completedPollination() && this.mTweaks$canPlant()) {
                    this.mTweaks$growFlower();
                    LogUtil.info(mTweaks$plantingCoolDown);
                    LogUtil.info("{} stopped pollinating flower at {}", bee, flowerPos);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void mTweaks$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("MT-plantingCoolDown", this.mTweaks$plantingCoolDown);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void mTweaks$readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.mTweaks$plantingCoolDown = nbt.getInt("MT-plantingCoolDown");
    }

    @Unique
    private void mTweaks$growFlower() {
        if (this.flowerPos != null) {
            BlockState flowerState = world.getBlockState(flowerPos);
            if (flowerState.getBlock() instanceof FlowerBlock flowerBlock) {
                mTweaks$plantingCoolDown = world.random.nextBetween(3600, 6490);
                for (int i = -2; i <= 2; i++) {
                    for (int b = -2; b <= 2; b++) {
                        for (int c = -2; c <= 2; c++) {
                            BlockPos pos = new BlockPos(flowerPos.getX() + i, flowerPos.getY() + b, flowerPos.getZ() + c);
                            if (world.getBlockState(pos).getBlock() instanceof AirBlock && flowerBlock.canPlaceAt(flowerState, world, pos)) {
                                if (world.random.nextInt(12) == 0) {
                                    world.setBlockState(pos, flowerState);
                                }
                            }
                        }
                    }
                }
            } else if (flowerState.getBlock() instanceof TallFlowerBlock flowerBlock && Tweaks.CONFIG.beeTallFlowerDuplication) {
                mTweaks$plantingCoolDown = world.random.nextBetween(3600, 8000);
                for (int i = -1; i <= 1; i++) {
                    for (int b = -2; b <= 2; b++) {
                        for (int c = -1; c <= 1; c++) {
                            BlockPos pos = new BlockPos(flowerPos.getX() + i, flowerPos.getY() + b, flowerPos.getZ() + c);
                            if (world.getBlockState(pos).getBlock() instanceof AirBlock && flowerBlock.canPlaceAt(flowerState, world, pos)) {
                                if (world.random.nextInt(6) == 0) {
                                    TallFlowerBlock.placeAt(world, flowerState, pos, Block.NOTIFY_LISTENERS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private boolean mTweaks$canPlant() {
        return this.mTweaks$plantingCoolDown == 0;
    }
}
