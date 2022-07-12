package me.melontini.tweaks.mixin.falling_beehives;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.melontini.tweaks.util.BeeNestUtil.trySpawnFallingBeeNest;

@Mixin(value = BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin extends BlockEntity {
    private static boolean mTweaks$FromFallen;

    public BeehiveBlockEntityMixin(BlockEntityType<?> type) {
        super(type);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void mTweaks$fallingHive(CallbackInfo ci) {
        BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity) (Object) this;
        BlockState state = beehiveBlockEntity.getCachedState();
        if (Tweaks.CONFIG.canBeeNestsFall) {
            if (world.getBlockState(pos).getBlock() == Blocks.BEE_NEST) {
                if (world.random.nextInt(24000) == 0) {
                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof AirBlock) {
                        for (int i = 0; i < 4; i++) {
                            switch (i) {
                                case 0:
                                    //I've run out of ways to check if the bee nest is on a tree.
                                    if (world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                    break;
                                case 1:
                                    if (world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                    break;
                                case 2:
                                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                    break;
                                case 3:
                                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "fromTag")
    private void mTweaks$readNbt(BlockState state, NbtCompound tag, CallbackInfo ci) {
        mTweaks$FromFallen = tag.getBoolean("MT-FromFallenBlock");
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/entity/BlockEntity.writeNbt (Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;", shift = At.Shift.AFTER), method = "writeNbt")
    private void mTweaks$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putBoolean("MT-FromFallenBlock", mTweaks$FromFallen);
    }
}
