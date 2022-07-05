package me.melontini.tweaks.mixin.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.minecraft.block.BambooBlock.STAGE;

@Mixin(BambooBlock.class)
public abstract class BambooBlockMixin extends Block implements Fertilizable {
    public BambooBlockMixin(Settings settings) {
        super(settings);
    }
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BambooBlock block = (BambooBlock) (Object) this;
        if (Tweaks.CONFIG.cropsGrowSlowerInCold) {
            if (state.get(STAGE) == 0) {
                float temp = world.getBiome(pos).value().getTemperature();
                if (temp > 0 && temp < 0.6) {
                    //LogUtil.info("cold " + temp);
                    if (world.getRandom().nextInt((int) (25 / (18.5 * (temp + 0.2)))) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        int bambooCount = block.countBambooBelow(world, pos) + 1;
                        if (bambooCount < 16) {
                            block.updateLeaves(state, world, pos, random, bambooCount);
                        }
                    }
                } else if (temp >= 0.6) {
                    //LogUtil.info("normal " + temp);
                    if (world.getRandom().nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        int bambooCount = block.countBambooBelow(world, pos) + 1;
                        if (bambooCount < 16) {
                            block.updateLeaves(state, world, pos, random, bambooCount);
                        }
                    }
                }
            }
            ci.cancel();
        }
    }
}
