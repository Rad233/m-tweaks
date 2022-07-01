package me.melontini.tweaks.mixin.falling_beehives;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.LootTableUtil;
import me.melontini.tweaks.util.PlayerUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

import static me.melontini.tweaks.Tweaks.MODID;
import static me.melontini.tweaks.util.BeeNestUtil.trySpawnFallingBeeNest;

@Mixin(value = BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin extends BlockEntity {
    private static boolean mTweaks$FromFallen;

    public BeehiveBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at = @At("HEAD"), method = "serverTick")
    private static void mTweaks$fallingHive(@NotNull World world, BlockPos pos, BlockState state, BeehiveBlockEntity beehiveBlockEntity, CallbackInfo ci) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.canBeeNestsFall) {
            if (world.getBlockState(pos).getBlock() == Blocks.BEE_NEST) {
                if (world.random.nextInt(24000) == 0) {
                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof AirBlock) {
                        for (int i = 0; i < 4; i++) {
                            switch (i) {
                                case 0 -> {
                                    //I've run out of ways to check if the bee nest is on a tree.
                                    if (world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                }
                                case 1 -> {
                                    if (world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                }
                                case 2 -> {
                                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                }
                                case 3 -> {
                                    if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() instanceof PillarBlock)
                                        if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getMaterial() == Material.WOOD)
                                            trySpawnFallingBeeNest(world, pos, state, beehiveBlockEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "readNbt")
    private void mTweaks$readNbt(@NotNull NbtCompound nbt, CallbackInfo ci) {
        mTweaks$FromFallen = nbt.getBoolean("MT-FromFallenBlock");
    }

    @Inject(at = @At("TAIL"), method = "writeNbt")
    private void mTweaks$writeNbt(@NotNull NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("MT-FromFallenBlock", mTweaks$FromFallen);
    }
}
